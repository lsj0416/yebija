package com.yebija.ppt.service;

import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.file.storage.FileStorage;
import com.yebija.ppt.generator.SlideGenerator;
import com.yebija.ppt.util.SlideUtils;
import com.yebija.template.domain.enums.ItemMode;
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

    @Transactional(readOnly = true)
    public byte[] export(Long churchId, Long worshipId) {
        Worship worship = worshipRepository.findByIdAndChurchId(worshipId, churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.WORSHIP_NOT_FOUND));

        Map<com.yebija.template.domain.enums.ItemType, SlideGenerator> generatorMap = generators.stream()
                .collect(Collectors.toMap(SlideGenerator::getSupportedType, Function.identity()));

        try (XMLSlideShow merged = new XMLSlideShow()) {
            merged.setPageSize(new Dimension(SlideUtils.W, SlideUtils.H));

            List<WorshipItem> items = worship.getItems(); // seq ASC 정렬됨
            if (items.isEmpty()) {
                addEmptySlide(merged);
            }

            for (WorshipItem item : items) {
                if (item.getMode() == ItemMode.FILE) {
                    appendFileSlides(merged, item);
                } else {
                    SlideGenerator generator = generatorMap.get(item.getType());
                    if (generator != null) {
                        generator.addSlides(merged, item);
                    } else {
                        log.warn("AUTO 슬라이드 생성 미지원 타입: {}", item.getType());
                    }
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            merged.write(out);
            return out.toByteArray();

        } catch (YebijaException e) {
            throw e;
        } catch (Exception e) {
            log.error("PPT 병합 실패: worshipId={}", worshipId, e);
            throw new YebijaException(ErrorCode.PPT_MERGE_FAILED);
        }
    }

    private void appendFileSlides(XMLSlideShow merged, WorshipItem item) {
        String storageKey = item.getFileStorageKey();
        if (storageKey == null || storageKey.isBlank()) {
            log.warn("FILE 모드이지만 fileStorageKey 없음: worshipItemId={}", item.getId());
            return;
        }

        byte[] fileBytes = fileStorage.load(storageKey);
        try (XMLSlideShow source = new XMLSlideShow(new ByteArrayInputStream(fileBytes))) {
            for (XSLFSlide srcSlide : source.getSlides()) {
                XSLFSlide destSlide = merged.createSlide();
                destSlide.importContent(srcSlide);
            }
        } catch (IOException e) {
            log.error("첨부 파일 슬라이드 복사 실패: storageKey={}", storageKey, e);
            throw new YebijaException(ErrorCode.PPT_MERGE_FAILED);
        }
    }

    private void addEmptySlide(XMLSlideShow pptx) {
        XSLFSlide slide = pptx.createSlide();
        SlideUtils.setBackground(slide);
    }
}
