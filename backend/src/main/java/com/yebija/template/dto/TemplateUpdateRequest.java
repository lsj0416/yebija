package com.yebija.template.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TemplateUpdateRequest {

    @NotBlank(message = "템플릿 이름은 필수입니다.")
    @Size(max = 100, message = "템플릿 이름은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 255, message = "설명은 255자 이하여야 합니다.")
    private String description;

    private boolean isDefault = false;

    @Valid
    private List<TemplateItemRequest> items = new ArrayList<>();
}
