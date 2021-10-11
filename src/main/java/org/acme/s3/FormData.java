package org.acme.s3;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import static javax.ws.rs.core.MediaType.*;
import java.io.InputStream;

public class FormData {

    @FormParam("file")
    @PartType(APPLICATION_OCTET_STREAM)
    public InputStream data;

    @FormParam("filename")
    @PartType(TEXT_PLAIN)
    public String fileName;

    @FormParam("mimetype")
    @PartType(TEXT_PLAIN)
    public String mimeType;
}
