package com.yebija.ppt.generator;

import com.yebija.ppt.util.SlideUtils;
import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.WorshipItem;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PrayerSlideGenerator implements SlideGenerator {

    @Override
    public ItemType getSupportedType() {
        return ItemType.PRAYER;
    }

    @Override
    public void addSlides(XMLSlideShow pptx, WorshipItem item) {
        Map<String, Object> content = item.getContent();

        String role = content != null ? (String) content.getOrDefault("role", "기도") : "기도";
        String leader = content != null ? (String) content.getOrDefault("leader", "") : "";

        XSLFSlide slide = pptx.createSlide();
        SlideUtils.setBackground(slide);

        // 기도 구분 (대표기도 / 헌금기도 등)
        SlideUtils.addTextBox(slide, role,
                80, 200, SlideUtils.W - 160, 100,
                36.0, false, TextParagraph.TextAlign.CENTER, SlideUtils.TEXT_SECONDARY);

        // 담당자 이름
        if (!leader.isBlank()) {
            SlideUtils.addTextBox(slide, leader,
                    80, 320, SlideUtils.W - 160, 120,
                    52.0, true, TextParagraph.TextAlign.CENTER, SlideUtils.TEXT_PRIMARY);
        }
    }
}
