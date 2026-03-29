package com.yebija.bible.controller;

import com.yebija.bible.dto.BibleResponse;
import com.yebija.bible.service.BibleService;
import com.yebija.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bible")
@RequiredArgsConstructor
public class BibleController {

    private final BibleService bibleService;

    @GetMapping("/verses")
    public ResponseEntity<ApiResponse<BibleResponse>> getVerses(
            @RequestParam String book,
            @RequestParam int chapter,
            @RequestParam int verseStart,
            @RequestParam int verseEnd) {

        BibleResponse response = bibleService.getVerses(book, chapter, verseStart, verseEnd);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
