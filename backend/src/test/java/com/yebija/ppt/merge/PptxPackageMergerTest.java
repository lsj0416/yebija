package com.yebija.ppt.merge;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PptxPackageMergerTest {

    private static final String CT_PRESENTATION =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml";

    @Test
    void isXmlContentType_detectsXmlBasedApplicationTypes() {
        assertThat(PptxPackageMerger.isXmlContentType("application/vnd.openxmlformats-officedocument.presentationml.slide+xml"))
                .isTrue();
        assertThat(PptxPackageMerger.isXmlContentType("application/xml")).isTrue();
        assertThat(PptxPackageMerger.isXmlContentType("application/vnd.ms-office.vbaProject")).isFalse();
        assertThat(PptxPackageMerger.isXmlContentType("application/vnd.openxmlformats-officedocument.oleObject")).isFalse();
    }

    @Test
    void shouldCopyAsBinary_treatsNonXmlApplicationPartsAsBinary() {
        assertThat(PptxPackageMerger.shouldCopyAsBinary("image/png")).isTrue();
        assertThat(PptxPackageMerger.shouldCopyAsBinary("audio/mpeg")).isTrue();
        assertThat(PptxPackageMerger.shouldCopyAsBinary("video/mp4")).isTrue();
        assertThat(PptxPackageMerger.shouldCopyAsBinary("application/vnd.ms-office.vbaProject")).isTrue();
        assertThat(PptxPackageMerger.shouldCopyAsBinary("application/octet-stream")).isTrue();
        assertThat(PptxPackageMerger.shouldCopyAsBinary("application/vnd.openxmlformats-officedocument.presentationml.slide+xml"))
                .isFalse();
    }

    @Test
    void appendPptx_readsSlidesInOrderEvenWhenPresentationUsesDifferentPrefix() throws Exception {
        byte[] source = createDeck("first", "second");
        byte[] sourceWithAltPrefix = rewritePresentationPrefix(source, "q");

        byte[] mergedBytes;
        try (PptxPackageMerger merger = PptxPackageMerger.create(960, 540)) {
            merger.appendPptx(sourceWithAltPrefix);
            mergedBytes = merger.toBytes();
        }

        try (XMLSlideShow merged = new XMLSlideShow(new ByteArrayInputStream(mergedBytes))) {
            assertThat(merged.getPageSize()).isEqualTo(new Dimension(960, 540));
            assertThat(merged.getSlides()).hasSize(2);
            assertThat(getSlideText(merged.getSlides().get(0))).contains("first");
            assertThat(getSlideText(merged.getSlides().get(1))).contains("second");
        }
    }

    private byte[] createDeck(String... texts) throws Exception {
        try (XMLSlideShow pptx = new XMLSlideShow()) {
            for (String text : texts) {
                XSLFSlide slide = pptx.createSlide();
                slide.createTextBox().setText(text);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pptx.write(out);
            return out.toByteArray();
        }
    }

    private byte[] rewritePresentationPrefix(byte[] pptxBytes, String prefix) throws Exception {
        try (OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(pptxBytes))) {
            PackagePart presentation = pkg.getPartsByContentType(CT_PRESENTATION).get(0);
            String xml = new String(presentation.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            xml = xml.replace("xmlns:p=", "xmlns:" + prefix + "=")
                    .replace("<p:", "<" + prefix + ":")
                    .replace("</p:", "</" + prefix + ":");

            try (OutputStream os = presentation.getOutputStream()) {
                os.write(xml.getBytes(StandardCharsets.UTF_8));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pkg.save(out);
            return out.toByteArray();
        }
    }

    private String getSlideText(XSLFSlide slide) {
        return slide.getShapes().stream()
                .filter(XSLFTextShape.class::isInstance)
                .map(XSLFTextShape.class::cast)
                .map(XSLFTextShape::getText)
                .reduce("", String::concat);
    }
}
