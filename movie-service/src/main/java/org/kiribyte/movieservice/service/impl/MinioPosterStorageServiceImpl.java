package org.kiribyte.movieservice.service.impl;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.kiribyte.movieservice.exception.*;
import org.kiribyte.movieservice.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MinioPosterStorageServiceImpl implements ImageStorageService {


    private final MinioClient minioClient;

    @Value("${minio.buckets.posters.name}")
    private String bucketName;

    @Value("${minio.buckets.posters.url-expiration-hours}")
    private int expirationHours;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSizeConfig;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png"
    );

    public MinioPosterStorageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    private void initializeBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket {} created", bucketName);
            }
        } catch (Exception e) {
            log.error("Error initialization bucket {}", bucketName, e);
            throw new StorageInitializationException("Failed to initialize storage bucket: " + bucketName, e);
        }
    }

    @Override
    public String upload(MultipartFile file, String fileName) {
        validateFile(file);
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info("File {} success uploaded to {}", fileName, bucketName);
            return getFileUrl(fileName);
        } catch (Exception e) {
            log.error("Failed to upload file {} in MinIO",fileName, e);
            throw new FileUploadException("Failed to upload file: " + fileName, e);
        }
    }

    @Override
    public byte[] download(String fileName) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build())) {

            byte[] fileData = inputStream.readAllBytes();
            log.info("File {} downloaded from bucket {}", fileName, bucketName);
            return fileData;

        } catch (Exception e) {
            log.error("Failed download image {} from MinIO", fileName, e);
            throw new FileDownloadException("Failed to download file: " + fileName, e);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return getPresignedUrl(fileName, Duration.ofHours(expirationHours));
    }

    @Override
    public String getPresignedUrl(String fileName, Duration expiration) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expirationHours, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed generate url for file {}", fileName, e);
            throw new UrlGenerationException("Failed to generate URL for file: " + fileName, e);
        }

    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty or null");
        }
        long maxFileSize = getMaxFileSizeInBytes();
        if (file.getSize() > maxFileSize) {
            throw new InvalidFileException("File size exceeds maximum allowed size: " + maxFileSize + " bytes");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileException("Invalid file type. Allowed types: " + ALLOWED_CONTENT_TYPES);
        }
    }


    private long getMaxFileSizeInBytes() {
        if (maxFileSizeConfig.endsWith("MB")) {
            long mb = Long.parseLong(maxFileSizeConfig.replace("MB", ""));
            return mb * 1024 * 1024;
        } else if (maxFileSizeConfig.endsWith("KB")) {
            long kb = Long.parseLong(maxFileSizeConfig.replace("KB", ""));
            return kb * 1024;
        } else {
            return Long.parseLong(maxFileSizeConfig);
        }
    }
}