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
public class SermonSlideGenerator implements SlideGenerator {

    @Override
    public ItemType getSupportedType() {
        return ItemType.SERMON;
    }

    @Override
    public void addSlides(XMLSlideShow pptx, WorshipItem item) {
        Map<String, Object> content = item.getContent();

        String title = content != null ? (String) content.getOrDefault("title", "") : "";
        String bibleRef = content != null ? (String) content.getOrDefault("bibleRef", "") : "";
        String preacher = content != null ? (String) content.getOrDefault("preacher", "") : "";

        XSLFSlide slide = pptx.createSlide();
        SlideUtils.setBackground(slide);

        // 성경 본문
        if (!bibleRef.isBlank()) {
            SlideUtils.addTextBox(slide, bibleRef,
                    80, 160, SlideUtils.W - 160, 70,
                    26.0, false, TextParagraph.TextAlign.CENTER, SlideUtils.TEXT_SECONDARY);
        }

        // 설교 제목
        if (!title.isBlank()) {
            SlideUtils.addTextBox(slide, title,
                    80, 250, SlideUtils.W - 160, 140,
                    52.0, true, TextParagraph.TextAlign.CENTER, SlideUtils.TEXT_PRIMARY);
        }

        // 설교자
        if (!preacher.isBlank()) {
            SlideUtils.addTextBox(slide, preacher,
                    80, 430, SlideUtils.W - 160, 70,
                    26.0, false, TextParagraph.TextAlign.CENTER, SlideUtils.TEXT_SECONDARY);
        }
    }
}
