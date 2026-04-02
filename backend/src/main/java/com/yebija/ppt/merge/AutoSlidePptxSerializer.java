package com.yebija.ppt.merge;

import com.yebija.ppt.generator.SlideGenerator;
import com.yebija.ppt.util.SlideUtils;
import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.WorshipItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * AUTO 모드 WorshipItem 목록을 임시 PPTX byte[]로 직렬화.
 * PptxPackageMerger에 세그먼트로 전달하기 위해 사용된다.
 */
@Slf4j
@Component
public class AutoSlidePptxSerializer {

    public byte[] serialize(List<WorshipItem> items,
                            Map<ItemType, SlideGenerator> generatorMap) throws IOException {
        try (XMLSlideShow pptx = new XMLSlideShow()) {
            pptx.setPageSize(new Dimension(SlideUtils.W, SlideUtils.H));
            for (WorshipItem item : items) {
                SlideGenerator gen = generatorMap.get(item.getType());
                if (gen == null) {
                    log.warn("AUTO 슬라이드 생성기 누락: worshipItemId={}, type={}", item.getId(), item.getType());
                    throw new IOException("No slide generator registered for AUTO item type: " + item.getType());
                }
                gen.addSlides(pptx, item);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pptx.write(out);
            return out.toByteArray();
        }
    }
}
