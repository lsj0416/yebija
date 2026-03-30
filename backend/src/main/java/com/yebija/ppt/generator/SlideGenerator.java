package com.yebija.ppt.generator;

import com.yebija.template.domain.enums.ItemType;
import com.yebija.worship.domain.WorshipItem;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

public interface SlideGenerator {

    ItemType getSupportedType();

    void addSlides(XMLSlideShow pptx, WorshipItem item);
}
