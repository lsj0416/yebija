package com.yebija.file.dto;

import com.yebija.file.domain.UploadedFile;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FileUploadResponse {

    private final Long id;
    private final String originalName;
    private final String storageKey;
    private final Long fileSize;
    private final String mimeType;
    private final LocalDateTime createdAt;

    private FileUploadResponse(UploadedFile file) {
        this.id = file.getId();
        this.originalName = file.getOriginalName();
        this.storageKey = file.getStorageKey();
        this.fileSize = file.getFileSize();
        this.mimeType = file.getMimeType();
        this.createdAt = file.getCreatedAt();
    }

    public static FileUploadResponse from(UploadedFile file) {
        return new FileUploadResponse(file);
    }
}
