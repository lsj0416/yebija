package com.yebija.ppt.generator;

import com.yebija.bible.dto.BibleResponse;
import com.yebija.bible.dto.BibleVerse;
import com.yebija.bible.service.BibleService;
import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.WorshipItem;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BibleSlideGeneratorTest {

    private final BibleService bibleService = mock(BibleService.class);
    private final BibleSlideGenerator generator = new BibleSlideGenerator(bibleService);

    @Test
    void addSlides_acceptsCurrentVerseKeys() {
        WorshipItem item = createItem(Map.of(
                "book", "John",
                "chapter", 3,
                "startVerse", 16,
                "endVerse", 17
        ));

        when(bibleService.getVerses("John", 3, 16, 17))
                .thenReturn(BibleResponse.of("John", 3, 16, 17, List.of(new BibleVerse(16, "text"))));

        try (XMLSlideShow pptx = new XMLSlideShow()) {
            generator.addSlides(pptx, item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(bibleService).getVerses("John", 3, 16, 17);
    }

    @Test
    void addSlides_fallsBackToLegacyVerseKeys() {
        WorshipItem item = createItem(Map.of(
                "book", "John",
                "chapter", 3,
                "verseStart", 16,
                "verseEnd", 17
        ));

        when(bibleService.getVerses("John", 3, 16, 17))
                .thenReturn(BibleResponse.of("John", 3, 16, 17, List.of(new BibleVerse(16, "text"))));

        try (XMLSlideShow pptx = new XMLSlideShow()) {
            generator.addSlides(pptx, item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(bibleService).getVerses("John", 3, 16, 17);
    }

    @Test
    void addSlides_prefersCurrentVerseKeysWhenBothFormatsExist() {
        WorshipItem item = createItem(Map.of(
                "book", "John",
                "chapter", 3,
                "startVerse", 16,
                "endVerse", 17,
                "verseStart", 1,
                "verseEnd", 2
        ));

        when(bibleService.getVerses("John", 3, 16, 17))
                .thenReturn(BibleResponse.of("John", 3, 16, 17, List.of(new BibleVerse(16, "text"))));

        try (XMLSlideShow pptx = new XMLSlideShow()) {
            generator.addSlides(pptx, item);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(bibleService).getVerses("John", 3, 16, 17);
        verify(bibleService, never()).getVerses("John", 3, 1, 2);
    }

    private WorshipItem createItem(Map<String, Object> content) {
        WorshipItem item = WorshipItem.create(null, ItemType.BIBLE, 1, "label", ItemMode.AUTO);
        item.updateContent("label", ItemMode.AUTO, content);
        return item;
    }
}
