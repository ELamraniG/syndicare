package com.syndicare.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${syndicare.storage.location}")
    private String storageLocation;

    private Path root;

    @PostConstruct
    public void init() throws IOException {
        root = Paths.get(storageLocation).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    /** Stores file under {category}/{uuid-original}. Returns relative path. */
    public String store(MultipartFile file, String category) {
        try {
            Path dir = root.resolve(category);
            Files.createDirectories(dir);

            String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safe = original.replaceAll("[^A-Za-z0-9._-]", "_");
            String filename = UUID.randomUUID() + "-" + safe;
            Path target = dir.resolve(filename);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return category + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Resource load(String relativePath) {
        try {
            Path file = root.resolve(relativePath).normalize();
            if (!file.startsWith(root)) throw new SecurityException("Invalid path");
            UrlResource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found: " + relativePath);
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + relativePath, e);
        }
    }

    public void delete(String relativePath) {
        try {
            Path file = root.resolve(relativePath).normalize();
            if (file.startsWith(root)) Files.deleteIfExists(file);
        } catch (IOException ignored) {}
    }
}
