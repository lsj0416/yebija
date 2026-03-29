package com.yebija.template.dto;

import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TemplateItemRequest {

    @NotNull(message = "항목 유형은 필수입니다.")
    private ItemType type;

    @NotNull(message = "순서는 필수입니다.")
    @Min(value = 1, message = "순서는 1 이상이어야 합니다.")
    private Integer seq;

    private String label;

    private ItemMode defaultMode;
}
