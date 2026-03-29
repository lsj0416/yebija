package com.yebija.worship.dto;

import com.yebija.template.domain.enums.ItemMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.Map;

@Getter
public class WorshipItemUpdateRequest {

    private String label;

    @NotNull(message = "모드는 필수입니다.")
    private ItemMode mode;

    private Map<String, Object> content;
}
