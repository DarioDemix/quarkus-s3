package org.acme.s3;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/s3")
public class S3SyncClientResource extends CommonResource {

    @Inject
    S3Client s3Client;

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@MultipartForm FormData formData) throws Exception {
        if (formData.fileName == null || formData.fileName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (formData.mimeType == null || formData.mimeType.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        PutObjectResponse putResponse = s3Client.putObject(buildPutRequest(formData),
                RequestBody.fromFile(uploadToTemp(formData.data)));
        if (putResponse != null) {
            return Response.ok().status(Response.Status.CREATED).build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("download/{objectKey}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("objectKey") String objectKey) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GetObjectResponse object = s3Client.getObject(buildGetRequest(objectKey), ResponseTransformer.toOutputStream(baos));

        Response.ResponseBuilder response = Response.ok((StreamingOutput) output -> baos.writeTo(output));
        response.header("Content-Disposition", "attachment;filename=" + objectKey);
        response.header("Content-Type", object.contentType());
        return response.build();
    }

    @GET
    public List<FileObject> listFiles() {
        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();

        //HEAD S3 objects to get metadata
        return s3Client.listObjects(listRequest).contents().stream().sorted(Comparator.comparing(S3Object::lastModified).reversed())
                .map(FileObject::new).collect(Collectors.toList());
    }
}