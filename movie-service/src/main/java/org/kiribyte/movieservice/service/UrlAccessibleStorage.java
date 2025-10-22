package org.kiribyte.movieservice.service;

import java.time.Duration;

public interface UrlAccessibleStorage {
    String getFileUrl(String fileName);
    String getPresignedUrl(String fileName, Duration expiration);
}
