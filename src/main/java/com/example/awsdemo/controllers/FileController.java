package com.example.awsdemo.controllers;

import com.example.awsdemo.model.FileMetadata;
import com.example.awsdemo.repository.FileMetadataRepository;
import com.example.awsdemo.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileController {

    private final S3Service s3Service;

    @Autowired
    private FileMetadataRepository repository;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String response = s3Service.uploadFile(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> listFiles() {
        return ResponseEntity.ok(s3Service.listFiles());
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String file) {
        byte[] data = s3Service.downloadFile(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/secure-download")
    public ResponseEntity<String> getSecureDownloadLink(@RequestParam String file) {
        String presignedUrl = s3Service.generatePresignedUrl(file);
        return ResponseEntity.ok(presignedUrl);
    }


    @GetMapping("/upload-history")
    public List<FileMetadata> getUploads() {
        return repository.findAll();
    }

    @PostMapping("/uploadDB")
    public ResponseEntity<String> uploadFileDB(@RequestParam("file") MultipartFile file) {
        try {
            String response = s3Service.uploadFileDB(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }



}