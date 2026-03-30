package com.yebija.file.controller;

import com.yebija.common.response.ApiResponse;
import com.yebija.common.util.SecurityUtil;
import com.yebija.file.dto.FileUploadResponse;
import com.yebija.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(fileService.upload(churchId, file));
    }

    @PostMapping(value = "/worship-items/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FileUploadResponse> attachToWorshipItem(
            @PathVariable Long itemId,
            @RequestParam("file") MultipartFile file) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(fileService.attachToWorshipItem(churchId, itemId, file));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        byte[] bytes = fileService.download(churchId, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                .body(bytes);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        fileService.delete(churchId, id);
    }
}
