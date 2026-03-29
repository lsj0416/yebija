package com.yebija.bible.scraper;

import com.yebija.bible.dto.BibleVerse;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GodpeopleBibleScraper {

    private static final String URL_TEMPLATE =
            "https://bible.godpeople.com/?bible=GAE&bid=%d&chap=%d";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public List<BibleVerse> scrape(int bookIndex, int chapter) {
        String url = String.format(URL_TEMPLATE, bookIndex, chapter);
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(10_000)
                    .get();

            Elements verseNumberElements = doc.select("td.bidx_listTd_yak");
            Elements contentElements = doc.select("td.bidx_listTd_phrase span.line_pen_ > span.line_pen_");

            if (verseNumberElements.isEmpty() || contentElements.isEmpty()) {
                log.warn("성경 스크래핑 결과 없음 — bookIndex={}, chapter={}", bookIndex, chapter);
                throw new YebijaException(ErrorCode.BIBLE_NOT_FOUND);
            }

            List<BibleVerse> verses = new ArrayList<>();
            int size = Math.min(verseNumberElements.size(), contentElements.size());

            for (int i = 0; i < size; i++) {
                Element numElement = verseNumberElements.get(i);
                Element textElement = contentElements.get(i);

                String numText = numElement.ownText().trim();
                String verseText = textElement.ownText().trim();

                if (numText.isEmpty() || verseText.isEmpty()) {
                    continue;
                }

                try {
                    int verseNumber = Integer.parseInt(numText);
                    verses.add(new BibleVerse(verseNumber, verseText));
                } catch (NumberFormatException e) {
                    log.debug("절 번호 파싱 실패: {}", numText);
                }
            }

            if (verses.isEmpty()) {
                throw new YebijaException(ErrorCode.BIBLE_NOT_FOUND);
            }

            return verses;

        } catch (YebijaException e) {
            throw e;
        } catch (IOException e) {
            log.error("성경 스크래핑 실패 — bookIndex={}, chapter={}, error={}", bookIndex, chapter, e.getMessage());
            throw new YebijaException(ErrorCode.BIBLE_SCRAPING_FAILED);
        }
    }
}
