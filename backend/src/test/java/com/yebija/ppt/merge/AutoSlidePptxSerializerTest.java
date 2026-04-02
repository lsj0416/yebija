package com.yebija.ppt.merge;

import com.yebija.ppt.generator.SlideGenerator;
import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.WorshipItem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AutoSlidePptxSerializerTest {

    private final AutoSlidePptxSerializer serializer = new AutoSlidePptxSerializer();

    @Test
    void serialize_throwsWhenAutoItemHasNoRegisteredGenerator() {
        WorshipItem item = WorshipItem.create(null, ItemType.CUSTOM, 1, "custom", ItemMode.AUTO);
        SlideGenerator otherGenerator = mock(SlideGenerator.class);
        when(otherGenerator.getSupportedType()).thenReturn(ItemType.BIBLE);

        assertThatThrownBy(() -> serializer.serialize(List.of(item), Map.of(ItemType.BIBLE, otherGenerator)))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("CUSTOM");
    }
}
