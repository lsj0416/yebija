package com.yebija.ppt.service;

import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.file.storage.FileStorage;
import com.yebija.ppt.generator.SlideGenerator;
import com.yebija.ppt.merge.AutoSlidePptxSerializer;
import com.yebija.ppt.merge.PptxPackageMerger;
import com.yebija.ppt.util.SlideUtils;
import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.Worship;
import com.yebija.worship.domain.WorshipItem;
import com.yebija.worship.repository.WorshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PptMergeService {

    private final WorshipRepository worshipRepository;
    private final FileStorage fileStorage;
    private final List<SlideGenerator> generators;
    private final AutoSlidePptxSerializer autoSerializer;

    @Transactional(readOnly = true)
    public byte[] export(Long churchId, Long worshipId) {
        Worship worship = worshipRepository.findByIdAndChurchId(worshipId, churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.WORSHIP_NOT_FOUND));

        Map<ItemType, SlideGenerator> generatorMap = generators.stream()
                .collect(Collectors.toMap(SlideGenerator::getSupportedType, Function.identity()));

        List<WorshipItem> items = worship.getItems();
        if (items.isEmpty()) {
            return generateEmptyPptx();
        }

        List<byte[]> segments;
        try {
            segments = buildSegments(items, generatorMap);
        } catch (IOException e) {
            log.error("AUTO 슬라이드 직렬화 실패: worshipId={}", worshipId, e);
            throw new YebijaException(ErrorCode.PPT_MERGE_FAILED);
        }

        try (PptxPackageMerger merger = PptxPackageMerger.create(SlideUtils.W, SlideUtils.H)) {
            for (byte[] segment : segments) {
                merger.appendPptx(segment);
            }
            return merger.toBytes();
        } catch (YebijaException e) {
            throw e;
        } catch (Exception e) {
            log.error("PPT 병합 실패: worshipId={}", worshipId, e);
            throw new YebijaException(ErrorCode.PPT_MERGE_FAILED);
        }
    }

    /**
     * 예배 항목을 PPTX 세그먼트(byte[]) 목록으로 변환한다.
     * - 연속된 AUTO 항목들 → autoSerializer로 하나의 임시 PPTX
     * - FILE 항목 → 원본 파일 byte[] 그대로
     */
    private List<byte[]> buildSegments(List<WorshipItem> items,
                                        Map<ItemType, SlideGenerator> generatorMap) throws IOException {
        List<byte[]> segments = new ArrayList<>();
        List<WorshipItem> pendingAuto = new ArrayList<>();

        for (WorshipItem item : items) {
            if (item.getMode() == ItemMode.FILE) {
                if (!pendingAuto.isEmpty()) {
                    segments.add(autoSerializer.serialize(pendingAuto, generatorMap));
                    pendingAuto.clear();
                }
                String key = item.getFileStorageKey();
                if (key != null && !key.isBlank()) {
                    segments.add(normalizeFileSegment(fileStorage.load(key)));
                } else {
                    log.warn("FILE 모드이지만 fileStorageKey 없음: worshipItemId={}", item.getId());
                }
            } else {
                pendingAuto.add(item);
            }
        }
        if (!pendingAuto.isEmpty()) {
            segments.add(autoSerializer.serialize(pendingAuto, generatorMap));
        }
        return segments;
    }

    private byte[] normalizeFileSegment(byte[] fileBytes) throws IOException {
        try (XMLSlideShow source = new XMLSlideShow(new ByteArrayInputStream(fileBytes))) {
            try {
                if (!SlideUtils.normalizePageSize(source, SlideUtils.W, SlideUtils.H)) {
                    return fileBytes;
                }
            } catch (IllegalArgumentException e) {
                log.warn("FILE 슬라이드 페이지 크기 불일치 — 원본 크기로 병합: {}", e.getMessage());
                return fileBytes;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            source.write(out);
            return out.toByteArray();
        }
    }

    /** 예배 항목이 없을 때 빈 슬라이드 한 장짜리 PPTX를 반환한다. */
    private byte[] generateEmptyPptx() {
        try (XMLSlideShow pptx = new XMLSlideShow()) {
            pptx.setPageSize(new Dimension(SlideUtils.W, SlideUtils.H));
            XSLFSlide slide = pptx.createSlide();
            SlideUtils.setBackground(slide);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pptx.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new YebijaException(ErrorCode.PPT_MERGE_FAILED);
        }
    }
}
