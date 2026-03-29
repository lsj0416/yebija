package com.yebija.template.dto;

import com.yebija.template.domain.WorshipTemplate;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class TemplateResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final boolean isDefault;
    private final List<TemplateItemDto> items;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private TemplateResponse(WorshipTemplate template) {
        this.id = template.getId();
        this.name = template.getName();
        this.description = template.getDescription();
        this.isDefault = template.isDefault();
        this.items = template.getItems().stream()
                .map(TemplateItemDto::from)
                .toList();
        this.createdAt = template.getCreatedAt();
        this.updatedAt = template.getUpdatedAt();
    }

    public static TemplateResponse from(WorshipTemplate template) {
        return new TemplateResponse(template);
    }
}
