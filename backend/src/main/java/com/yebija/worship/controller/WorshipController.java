package com.yebija.worship.controller;

import com.yebija.common.response.ApiResponse;
import com.yebija.common.util.SecurityUtil;
import com.yebija.worship.dto.WorshipCreateRequest;
import com.yebija.worship.dto.WorshipItemUpdateRequest;
import com.yebija.worship.dto.WorshipResponse;
import com.yebija.worship.dto.WorshipUpdateRequest;
import com.yebija.worship.service.WorshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worships")
@RequiredArgsConstructor
public class WorshipController {

    private final WorshipService worshipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorshipResponse> create(@Valid @RequestBody WorshipCreateRequest request) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(worshipService.create(churchId, request));
    }

    @GetMapping
    public ApiResponse<List<WorshipResponse>> findAll() {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(worshipService.findAll(churchId));
    }

    @GetMapping("/{id}")
    public ApiResponse<WorshipResponse> findById(@PathVariable Long id) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(worshipService.findById(churchId, id));
    }

    @PutMapping("/{id}")
    public ApiResponse<WorshipResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody WorshipUpdateRequest request) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(worshipService.update(churchId, id, request));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<WorshipResponse> complete(@PathVariable Long id) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(worshipService.complete(churchId, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        worshipService.delete(churchId, id);
    }

    @PutMapping("/{id}/items/{itemId}")
    public ApiResponse<WorshipResponse> updateItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody WorshipItemUpdateRequest request) {
        Long churchId = SecurityUtil.getCurrentChurchId();
        return ApiResponse.ok(worshipService.updateItem(churchId, id, itemId, request));
    }
}
