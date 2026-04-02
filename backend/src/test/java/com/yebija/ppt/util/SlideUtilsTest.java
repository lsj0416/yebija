package com.yebija.ppt.util;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlideUtilsTest {

    @Test
    void normalizePageSize_returnsFalseWhenAlreadyNormalized() throws Exception {
        try (XMLSlideShow pptx = new XMLSlideShow()) {
            pptx.setPageSize(new Dimension(SlideUtils.W, SlideUtils.H));

            boolean normalized = SlideUtils.normalizePageSize(pptx, 960, 540);

            assertThat(normalized).isFalse();
            assertThat(pptx.getPageSize()).isEqualTo(new Dimension(SlideUtils.W, SlideUtils.H));
        }
    }

    @Test
    void normalizePageSize_rejectsMismatchedPageSize() throws Exception {
        try (XMLSlideShow pptx = new XMLSlideShow()) {
            pptx.setPageSize(new Dimension(1280, 720));

            assertThatThrownBy(() -> SlideUtils.normalizePageSize(pptx, 960, 540))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("expected 960x540")
                    .hasMessageContaining("was 1280x720");
        }
    }
}
