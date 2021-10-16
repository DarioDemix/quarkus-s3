package config.services;

import io.quarkus.arc.properties.IfBuildProperty;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@IfBuildProperty(name = "quarkus.s3.enabled", stringValue = "true")
public class MinioService implements DevService {
    @Inject
    S3Client s3Client;
    @ConfigProperty(name = "quarkus.s3.endpoint-override", defaultValue = "http://locahost:9000")
    String s3Endpoint;
    @ConfigProperty(name = "quarkus.s3.aws.credentials.static-provider.access-key-id", defaultValue = "test-key")
    String accessKeyId;
    @ConfigProperty(name = "quarkus.s3.aws.credentials.static-provider.secret-access-key", defaultValue = "test-secret")
    String secretAccessKey;
    @ConfigProperty(name = "bucket.name", defaultValue = "bucket-a")
    String bucketName;

    public List<String> start() {
        try {
            int port = new URL(s3Endpoint).getPort();
            return isPortInUse(port) ? List.of("Port " + port + " already in use. Skipping minio container creation...") : startMinioContainer(port);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> startMinioContainer(int portToExpose) {
        final DockerImageName minioImage = DockerImageName.parse("minio/minio");

        final Map<String, String> envVars = Map.of(
                "MINIO_ROOT_USER", accessKeyId,
                "MINIO_ROOT_PASSWORD", secretAccessKey
        );

        final int uiInternalPort = 9001;

        final List<Integer> internalPorts = List.of(9000, uiInternalPort);

        final String command = "server /data --console-address :" + uiInternalPort;

        GenericContainer<?> minioContainer =
                new FixedHostPortGenericContainer<>(minioImage.toString())
                        .withEnv(envVars)
                        .withFixedExposedPort(portToExpose, 9000)
                        .withExposedPorts(9001)
                        .withCommand(command);

        minioContainer.start();

        s3Client.createBucket(builder -> builder.bucket(bucketName).build());

        return internalPorts
                .stream()
                .map(internalPort -> "Minio port -> http://localhost:" + minioContainer.getMappedPort(internalPort))
                .collect(Collectors.toList());
    }

}
