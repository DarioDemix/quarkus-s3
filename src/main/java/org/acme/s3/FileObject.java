package org.acme.s3;

import software.amazon.awssdk.services.s3.model.S3Object;

public class FileObject {
    private String objectKey;
    private Long size;

    public FileObject() {
    }

    public FileObject(S3Object s3Object) {
        if (s3Object != null) {
            setObjectKey(s3Object.key());
            setSize(s3Object.size());
        }
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

}
