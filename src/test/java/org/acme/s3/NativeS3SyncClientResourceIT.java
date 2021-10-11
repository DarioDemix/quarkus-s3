package org.acme.s3;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeS3SyncClientResourceIT extends S3SyncClientResourceTest {

    // Execute the same tests but in native mode.
}