package org.kiribyte.movieservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file, String fileName);
    byte[] download(String fileName);
}
