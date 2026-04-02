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
        Number chapterNum    = (Number) content.get("chapter");
        Number verseStartNum = getNumber(content, "startVerse", "verseStart");
        Number verseEndNum   = getNumber(content, "endVerse", "verseEnd");

        if (book == null || chapterNum == null || verseStartNum == null || verseEndNum == null) return;

        int chapter    = chapterNum.intValue();
        int verseStart = verseStartNum.intValue();
        int verseEnd   = verseEndNum.intValue();

        BibleResponse response = bibleService.getVerses(book, chapter, verseStart, verseEnd);
        String baseRef = book + " " + chapter + ":" + verseStart
                + (verseStart != verseEnd ? "-" + verseEnd : "");

        for (BibleVerse verse : response.getVerses()) {
            XSLFSlide slide = pptx.createSlide();
            SlideUtils.setBackground(slide);

            // 상단 성경 참조
            SlideUtils.addTextBox(slide, baseRef,
                    30, 20, SlideUtils.W - 60, 42,
                    17.0, false, TextParagraph.TextAlign.RIGHT, SlideUtils.TEXT_SECONDARY);

            // 절 번호 + 본문
            String verseText = verse.getVerseNumber() + "  " + verse.getText();
            SlideUtils.addTextBox(slide, verseText,
                    60, 90, SlideUtils.W - 120, 390,
                    26.0, false, TextParagraph.TextAlign.CENTER, SlideUtils.TEXT_PRIMARY);
        }
    }

    private Number getNumber(Map<String, Object> content, String preferredKey, String legacyKey) {
        Object value = content.get(preferredKey);
        if (value instanceof Number number) {
            return number;
        }

        Object legacyValue = content.get(legacyKey);
        return legacyValue instanceof Number number ? number : null;
    }
}
