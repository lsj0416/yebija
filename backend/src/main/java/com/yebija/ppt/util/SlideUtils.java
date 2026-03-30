package com.yebija.ppt.util;

import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SlideUtils {

    public static final int W = 1280;
    public static final int H = 720;

    public static final Color BG_COLOR = new Color(18, 18, 28);
    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(160, 160, 190);
    public static final String FONT_FAMILY = "맑은 고딕";

    private SlideUtils() {}

    public static void setBackground(XSLFSlide slide) {
        setBackground(slide, BG_COLOR);
    }

    public static void setBackground(XSLFSlide slide, Color color) {
        XSLFAutoShape rect = slide.createAutoShape();
        rect.setShapeType(ShapeType.RECT);
        rect.setAnchor(new Rectangle2D.Double(0, 0, W, H));
        rect.setFillColor(color);
        rect.setLineColor(color);
    }

    public static void addTextBox(XSLFSlide slide, String text,
                                   double x, double y, double w, double h,
                                   double fontSize, boolean bold,
                                   TextParagraph.TextAlign align, Color color) {
        XSLFTextBox tb = slide.createTextBox();
        tb.setAnchor(new Rectangle2D.Double(x, y, w, h));
        tb.setVerticalAlignment(VerticalAlignment.MIDDLE);
        tb.clearText();

        XSLFTextParagraph para = tb.addNewTextParagraph();
        para.setTextAlign(align);

        XSLFTextRun run = para.addNewTextRun();
        run.setText(text);
        run.setFontSize(fontSize);
        run.setBold(bold);
        run.setFontColor(color);
        run.setFontFamily(FONT_FAMILY);
    }
}
