package com.yebija.bible.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yebija.bible.domain.BibleBook;
import com.yebija.bible.dto.BibleResponse;
import com.yebija.bible.dto.BibleVerse;
import com.yebija.bible.scraper.GodpeopleBibleScraper;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BibleService {

    private final GodpeopleBibleScraper scraper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration BIBLE_CACHE_TTL = Duration.ofHours(24);
    private static final String CACHE_KEY_PREFIX = "bible::";

    public BibleResponse getVerses(String bookName, int chapter, int verseStart, int verseEnd) {
        BibleBook book = BibleBook.fromKoreanName(bookName);
        String cacheKey = CACHE_KEY_PREFIX + bookName + "::" + chapter;

        List<BibleVerse> allVerses = getOrLoadChapter(book, chapter, cacheKey);

        List<BibleVerse> filtered = allVerses.stream()
                .filter(v -> v.getVerseNumber() >= verseStart && v.getVerseNumber() <= verseEnd)
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            throw new YebijaException(ErrorCode.BIBLE_NOT_FOUND);
        }

        return BibleResponse.of(bookName, chapter, verseStart, verseEnd, filtered);
    }

    @SuppressWarnings("unchecked")
    private List<BibleVerse> getOrLoadChapter(BibleBook book, int chapter, String cacheKey) {
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.convertValue(cached, new TypeReference<List<BibleVerse>>() {});
            } catch (Exception e) {
                log.warn("Redis 캐시 역직렬화 실패, 재스크래핑: {}", cacheKey);
            }
        }

        List<BibleVerse> verses = scraper.scrape(book.getBookIndex(), chapter);
        redisTemplate.opsForValue().set(cacheKey, verses, BIBLE_CACHE_TTL);
        return verses;
    }
}
