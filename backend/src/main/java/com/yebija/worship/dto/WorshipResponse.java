package com.yebija.worship.dto;

import com.yebija.worship.domain.Worship;
import com.yebija.worship.domain.enums.WorshipStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class WorshipResponse {

    private final Long id;
    private final LocalDate worshipDate;
    private final String title;
    private final WorshipStatus status;
    private final Long templateId;
    private final List<WorshipItemDto> items;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private WorshipResponse(Worship worship) {
        this.id = worship.getId();
        this.worshipDate = worship.getWorshipDate();
        this.title = worship.getTitle();
        this.status = worship.getStatus();
        this.templateId = worship.getTemplate() != null ? worship.getTemplate().getId() : null;
        this.items = worship.getItems().stream()
                .map(WorshipItemDto::from)
                .toList();
        this.createdAt = worship.getCreatedAt();
        this.updatedAt = worship.getUpdatedAt();
    }

    public static WorshipResponse from(Worship worship) {
        return new WorshipResponse(worship);
    }
}
