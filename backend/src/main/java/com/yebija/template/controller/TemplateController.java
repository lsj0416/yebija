package com.yebija.template.controller;

import com.yebija.common.response.ApiResponse;
import com.yebija.common.util.SecurityUtil;
import com.yebija.template.dto.TemplateCreateRequest;
import com.yebija.template.dto.TemplateResponse;
import com.yebija.template.dto.TemplateUpdateRequest;
import com.yebija.template.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TemplateResponse> create(@Valid @RequestBody TemplateCreateRequest request) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(templateService.create(churchId, request));
    }

    @GetMapping
    public ApiResponse<List<TemplateResponse>> findAll() {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(templateService.findAll(churchId));
    }

    @GetMapping("/{id}")
    public ApiResponse<TemplateResponse> findById(@PathVariable Long id) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(templateService.findById(churchId, id));
    }

    @PutMapping("/{id}")
    public ApiResponse<TemplateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TemplateUpdateRequest request) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(templateService.update(churchId, id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        templateService.delete(churchId, id);
    }
}
