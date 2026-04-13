package com.yebija.template.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTypeTest {

    @Test
    void fileOnlyTypesRejectAutoMode() {
        assertThat(ItemType.HYMN.supportsMode(ItemMode.AUTO)).isFalse();
        assertThat(ItemType.RESPONSIVE_READING.supportsMode(ItemMode.AUTO)).isFalse();
        assertThat(ItemType.HYMN.getRecommendedMode()).isEqualTo(ItemMode.FILE);
        assertThat(ItemType.RESPONSIVE_READING.getRecommendedMode()).isEqualTo(ItemMode.FILE);
    }

    @Test
    void autoCapableTypesKeepAutoAsRecommendedMode() {
        assertThat(ItemType.BIBLE.supportsMode(ItemMode.AUTO)).isTrue();
        assertThat(ItemType.PRAYER.supportsMode(ItemMode.AUTO)).isTrue();
        assertThat(ItemType.SERMON.supportsMode(ItemMode.AUTO)).isTrue();
        assertThat(ItemType.CUSTOM.getRecommendedMode()).isEqualTo(ItemMode.AUTO);
    }
}
