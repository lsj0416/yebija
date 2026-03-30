package com.yebija.template.dto;

import com.yebija.template.domain.TemplateItem;
import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import lombok.Getter;

@Getter
public class TemplateItemDto {

    private final Long id;
    private final ItemType type;
    private final int seq;
    private final String label;
    private final ItemMode defaultMode;

    private TemplateItemDto(TemplateItem item) {
        this.id = item.getId();
        this.type = item.getType();
        this.seq = item.getSeq();
        this.label = item.getLabel();
        this.defaultMode = item.getDefaultMode();
    }

    public static TemplateItemDto from(TemplateItem item) {
        return new TemplateItemDto(item);
    }
}
