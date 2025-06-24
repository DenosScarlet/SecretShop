package com.secretShop.dtl.service;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.InvalidKeyException;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    public void uploadFile(String bucketName, String objectName, InputStream file, long size, String contentType) throws MinioException, java.security.NoSuchAlgorithmException, java.io.IOException, InvalidKeyException {
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(file, size, -1)
                .contentType(contentType)
                .build();
        minioClient.putObject(args);
    }

    public InputStream downloadFile(String bucketName, String objectName) throws MinioException, java.security.NoSuchAlgorithmException, java.io.IOException, InvalidKeyException {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        return minioClient.getObject(args);
    }

    public void deleteFile(String bucketName, String objectName) {
        try {
            // Проверяем существование файла
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // Если файл существует - удаляем
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
               // log.warn("File not found: {}/{}", bucketName, objectName);
            } else {
                throw new RuntimeException("MinIO error: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file: " + e.getMessage());
        }
    }
}
