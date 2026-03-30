package com.yebija.ppt.generator;

import com.yebija.bible.dto.BibleResponse;
import com.yebija.bible.dto.BibleVerse;
import com.yebija.bible.service.BibleService;
import com.yebija.ppt.util.SlideUtils;
import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.WorshipItem;
import lombok.RequiredArgsConstructor;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BibleSlideGenerator implements SlideGenerator {

    private final BibleService bibleService;

    @Override
    public ItemType getSupportedType() {
        return ItemType.BIBLE;
    }

    @Override
    public void addSlides(XMLSlideShow pptx, WorshipItem item) {
        Map<String, Object> content = item.getContent();
        if (content == null) return;

        String book = (String) content.get("book");
        int chapter = ((Number) content.get("chapter")).intValue();
        int verseStart = ((Number) content.get("verseStart")).intValue();
        int verseEnd = ((Number) content.get("verseEnd")).intValue();

        BibleResponse response = bibleService.getVerses(book, chapter, verseStart, verseEnd);
        String baseRef = book + " " + chapter + ":" + verseStart
                + (verseStart != verseEnd ? "-" + verseEnd : "");

        for (BibleVerse verse : response.getVerses()) {
            XSLFSlide slide = pptx.createSlide();
            SlideUtils.setBackground(slide);

            // 상단 성경 참조
            SlideUtils.addTextBox(slide, baseRef,
                    40, 30, SlideUtils.W - 80, 55,
                    22.0, false, TextParagraph.TextAlign.RIGHT, SlideUtils.TEXT_SECONDARY);

            // 절 번호 + 본문
            String verseText = verse.getVerseNumber() + "  " + verse.getText();
            SlideUtils.addTextBox(slide, verseText,
                    80, 120, SlideUtils.W - 160, 500,
                    34.0, false, TextParagraph.TextAlign.CENTER, SlideUtils.TEXT_PRIMARY);
        }
    }
}
