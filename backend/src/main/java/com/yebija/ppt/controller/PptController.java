package com.yebija.ppt.controller;

import com.yebija.common.util.SecurityUtil;
import com.yebija.ppt.service.PptMergeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/worships")
@RequiredArgsConstructor
public class PptController {

    private final PptMergeService pptMergeService;

    @PostMapping("/{worshipId}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long worshipId) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        byte[] pptBytes = pptMergeService.export(churchId, worshipId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("worship-" + worshipId + ".pptx", StandardCharsets.UTF_8)
                        .build());
        headers.setContentLength(pptBytes.length);

        return ResponseEntity.ok().headers(headers).body(pptBytes);
    }
}
