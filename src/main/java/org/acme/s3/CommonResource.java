package org.acme.s3;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

public class CommonResource {
    private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @ConfigProperty(name = "bucket.name")
    String bucketName;

    protected PutObjectRequest buildPutRequest(FormData formData) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(formData.fileName)
                .contentType(formData.mimeType)
                .build();
    }

    protected GetObjectRequest buildGetRequest(String objectKey) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
    }

    protected File tempFilePath() {
        return new File(TEMP_DIR, "s3AsyncDownloadedTemp" +
                (new Date()).getTime() + UUID.randomUUID() +
                "." + ".tmp");
    }

    protected File uploadToTemp(InputStream data) {
        File tempPath;
        try {
            tempPath = File.createTempFile("uploadS3Tmp", ".tmp");
            Files.copy(data, tempPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return tempPath;
    }

}
