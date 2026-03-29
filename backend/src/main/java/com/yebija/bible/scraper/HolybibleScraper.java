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

/**
 * holybible.or.kr (대한성서공회 운영) CGI 엔드포인트를 통한 개역개정 성경 스크래핑
 * URL 패턴: http://www.holybible.or.kr/mobile/B_GAE/cgi/bibleftxt.php?VR=GAE&VL={책번호}&CN={장}&CV=99
 *
 * 저작권 고지: 본 서비스의 성경 본문은 대한성서공회의 저작물입니다.
 * 참고: docs/decisions.md ADR-009
 */
@Slf4j
@Component
public class HolybibleScraper {

    private static final String URL_TEMPLATE =
            "http://www.holybible.or.kr/mobile/B_GAE/cgi/bibleftxt.php?VR=GAE&VL=%d&CN=%d&CV=99";
    private static final String USER_AGENT = "Mozilla/5.0";

    public List<BibleVerse> scrape(int bookIndex, int chapter) {
        String url = String.format(URL_TEMPLATE, bookIndex, chapter);
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(8_000)
                    .get();

            // <ol start=001 id="b_001"> 안의 <li><font class=tk4l>절 내용</font>
            Elements listItems = doc.select("ol[id^=b_] li");

            if (listItems.isEmpty()) {
                log.warn("holybible 스크래핑 결과 없음 — bookIndex={}, chapter={}", bookIndex, chapter);
                throw new YebijaException(ErrorCode.BIBLE_NOT_FOUND);
            }

            List<BibleVerse> verses = new ArrayList<>();
            for (int i = 0; i < listItems.size(); i++) {
                Element li = listItems.get(i);
                // <a> 태그(단어 사전 링크) 제거 후 텍스트 추출
                li.select("a").remove();
                String text = li.text().trim();
                if (!text.isEmpty()) {
                    verses.add(new BibleVerse(i + 1, text));
                }
            }

            if (verses.isEmpty()) {
                throw new YebijaException(ErrorCode.BIBLE_NOT_FOUND);
            }

            return verses;

        } catch (YebijaException e) {
            throw e;
        } catch (IOException e) {
            log.error("holybible 스크래핑 실패 — bookIndex={}, chapter={}, error={}", bookIndex, chapter, e.getMessage());
            throw new YebijaException(ErrorCode.BIBLE_SCRAPING_FAILED);
        }
    }
}
