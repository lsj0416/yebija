package com.yebija.worship.dto;

import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.WorshipItem;
import lombok.Getter;

import java.util.Map;

@Getter
public class WorshipItemDto {

    private final Long id;
    private final ItemType type;
    private final int seq;
    private final String label;
    private final ItemMode mode;
    private final Map<String, Object> content;
    private final String fileStorageKey;

    private WorshipItemDto(WorshipItem item) {
        this.id = item.getId();
        this.type = item.getType();
        this.seq = item.getSeq();
        this.label = item.getLabel();
        this.mode = item.getMode();
        this.content = item.getContent();
        this.fileStorageKey = item.getFileStorageKey();
    }

    public static WorshipItemDto from(WorshipItem item) {
        return new WorshipItemDto(item);
    }
}
