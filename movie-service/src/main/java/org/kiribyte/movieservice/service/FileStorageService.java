package org.kiribyte.movieservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file, String fileName);
    String upload(byte[] bytes, String fileName, String contentType);
    byte[] download(String fileName);
    void delete(String fileName);
}
