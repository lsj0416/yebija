package com.yebija.template.domain.enums;

public enum ItemType {
    HYMN,
    BIBLE,
    RESPONSIVE_READING,
    PRAYER,
    SERMON,
    CUSTOM;

    public boolean supportsMode(ItemMode mode) {
        if (mode == null) {
            return true;
        }

        return switch (this) {
            case HYMN, RESPONSIVE_READING -> mode == ItemMode.FILE;
            case BIBLE, PRAYER, SERMON, CUSTOM -> true;
        };
    }

    public ItemMode getRecommendedMode() {
        return switch (this) {
            case HYMN, RESPONSIVE_READING -> ItemMode.FILE;
            case BIBLE, PRAYER, SERMON, CUSTOM -> ItemMode.AUTO;
        };
    }
}
