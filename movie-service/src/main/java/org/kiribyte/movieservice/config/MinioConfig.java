package org.kiribyte.movieservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.config.endpoint}")
    private String endpoint;

    @Value("${minio.config.login}")
    private String login;

    @Value("${minio.config.password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(login, password)
                .build();
    }
}
