import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.AttributeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static software.amazon.awssdk.http.SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES;

@Testcontainers
class S3MockContainerJupiterTest {
    private static final String S3MOCK_VERSION = System.getProperty("s3mock.version", "latest");
    private static final String TEST_ENC_KEYREF =
            "arn:aws:kms:us-east-1:1234567890:key/valid-test-key-ref";
    private static final Collection<String> INITIAL_BUCKET_NAMES = asList("bucket-a", "bucket-b");
    private static final String BUCKET_NAME = "mydemotestbucket";
    private static final String UPLOAD_FILE_NAME = "src/test/resources/sampleFile.txt";

    protected S3Client s3Client;

    @Container
    private final S3MockContainer s3MockContainer =
            new S3MockContainer(S3MOCK_VERSION)
                    .withValidKmsKeys(TEST_ENC_KEYREF)
                    .withInitialBuckets(String.join(",", INITIAL_BUCKET_NAMES));

    @BeforeEach
    void setUp() {
        // Must create S3Client after S3MockContainer is started, otherwise we can't request the random
        // locally mapped port for the endpoint
        String endpoint = s3MockContainer.getHttpsEndpoint();
        s3Client = createS3ClientV2(endpoint);
    }

    @Test
    void shouldUploadAndDownloadObject() throws Exception {
        final File uploadFile = new File(UPLOAD_FILE_NAME);

        s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());
        s3Client.putObject(
                PutObjectRequest.builder().bucket(BUCKET_NAME).key(uploadFile.getName()).build(),
                RequestBody.fromFile(uploadFile));

        final ResponseInputStream<GetObjectResponse> response =
                s3Client.getObject(
                        GetObjectRequest.builder().bucket(BUCKET_NAME).key(uploadFile.getName()).build());

        final InputStream uploadFileIs = new FileInputStream(uploadFile);
        String responseFilename = response.response().getValueForField("key", String.class).get();
        uploadFileIs.close();
        response.close();

        assertEquals(UPLOAD_FILE_NAME, responseFilename);
    }

    @Test
    void defaultBucketsGotCreated() {
        final List<Bucket> buckets = s3Client.listBuckets().buckets();
        final Set<String> bucketNames = buckets.stream().map(Bucket::name)
                .filter(INITIAL_BUCKET_NAMES::contains).collect(Collectors.toSet());

        assertTrue(bucketNames.containsAll(INITIAL_BUCKET_NAMES));
    }

    private S3Client createS3ClientV2(String endpoint) {
        return S3Client.builder()
                .region(Region.of("us-east-1"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("foo", "bar")))
                .endpointOverride(URI.create(endpoint))
                .httpClient(UrlConnectionHttpClient.builder().buildWithDefaults(
                        AttributeMap.builder().put(TRUST_ALL_CERTIFICATES, Boolean.TRUE).build()))
                .build();
    }

}
