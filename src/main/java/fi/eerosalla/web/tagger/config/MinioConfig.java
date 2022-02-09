package fi.eerosalla.web.tagger.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;

    @JsonIgnore
    private static final String[] BUCKET_NAMES = new String[]{
        "tg-files", "tg-thumbnails"
    };

    @AllArgsConstructor
    private static class StatementEntry {
        @JsonProperty("Effect")
        private String effect;

        @JsonProperty("Principal")
        private Map<String, List<String>> principal;

        @JsonProperty("Action")
        private List<String> action;

        @JsonProperty("Resource")
        private List<String> resource;
    }

    @AllArgsConstructor
    private static class BucketPolicy {
        @JsonProperty("Version")
        private String version;

        @NotNull
        @JsonProperty("Statement")
        private List<StatementEntry> statement;
    }


    @Bean
    @JsonIgnore
    @SneakyThrows
    public MinioClient minioClient(final ObjectMapper objectMapper) {
        MinioClient client = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(
                accessKey,
                secretKey
            ).build();

        for (String bucketName : BUCKET_NAMES) {
            BucketExistsArgs bucketExistsArgs =
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build();

            boolean bucketExists =
                client.bucketExists(bucketExistsArgs);
            if (!bucketExists) {
                MakeBucketArgs makeBucketArgs =
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build();
                client.makeBucket(makeBucketArgs);
            }



            BucketPolicy bucketPolicy = new BucketPolicy(
                "2012-10-17",
                List.of(
                    new StatementEntry(
                        "Allow", //TODO:
                        Map.of("AWS", List.of("*")),
                        List.of("s3:GetObject"),
                        List.of("arn:aws:s3:::" + bucketName + "/*")
                    )
                )
            );

            SetBucketPolicyArgs setBucketPolicyArgs =
                SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(
                        objectMapper.writeValueAsString(
                            bucketPolicy
                        )
                    ).build();
            client.setBucketPolicy(setBucketPolicyArgs);
        }

        return client;
    }

}
