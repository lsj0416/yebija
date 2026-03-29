package com.yebija.bible.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class BibleResponse {

    private String book;
    private int chapter;
    private int verseStart;
    private int verseEnd;
    private List<BibleVerse> verses;
    private String fullText;

    public static BibleResponse of(String book, int chapter, int verseStart, int verseEnd,
                                   List<BibleVerse> verses) {
        String fullText = verses.stream()
                .map(v -> v.getVerseNumber() + " " + v.getText())
                .collect(Collectors.joining(" "));
        return new BibleResponse(book, chapter, verseStart, verseEnd, verses, fullText);
    }
}
