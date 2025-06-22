package com.example.awsdemo.service;

import com.example.awsdemo.model.FileMetadata;
import com.example.awsdemo.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Autowired
    private FileMetadataRepository repository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final String region = "eu-north-1"; // replace with your actual region
    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(Region.EU_NORTH_1) // your region
                .build();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String key = file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return "Uploaded: " + key;
    }

    public List<String> listFiles() {
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);

        return listRes.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public byte[] downloadFile(String key) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getReq);
        return objectBytes.asByteArray();
    }

    public String generatePresignedUrl(String key) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.EU_NORTH_1) // use your actual region
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // link valid for 5 mins
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

        presigner.close(); // release resources
        return presignedRequest.url().toString();
    }

    public String uploadFileDB(MultipartFile file) throws IOException {
        String key = file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        FileMetadata meta = new FileMetadata();
        meta.setFileName(file.getOriginalFilename());
        meta.setS3Key(key);
        meta.setUploadedBy("admin"); // you can pass this from controller
        meta.setUploadTime(LocalDateTime.now());
        String url = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
        meta.setS3Url(url);
        repository.save(meta);
        return "Uploaded successfully: " + key + ", accessible at: " + url;

    }

}
