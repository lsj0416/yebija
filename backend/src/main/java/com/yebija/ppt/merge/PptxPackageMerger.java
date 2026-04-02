package com.yebija.ppt.merge;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 여러 PPTX byte[]를 OPCPackage(ZIP) 레벨에서 병합.
 * importContent() 방식과 달리 슬라이드 마스터·레이아웃·미디어를 원본 그대로 복사한다.
 *
 * 처리 순서: 미디어 → 테마 → 마스터 → 레이아웃 → 슬라이드 (의존성 역순)
 */
@Slf4j
public class PptxPackageMerger implements Closeable {

    // ── Content types ─────────────────────────────────────────────────────────
    private static final String CT_PRESENTATION =
        "application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml";
    private static final String CT_SLIDE =
        "application/vnd.openxmlformats-officedocument.presentationml.slide+xml";
    private static final String CT_SLIDE_MASTER =
        "application/vnd.openxmlformats-officedocument.presentationml.slideMaster+xml";
    private static final String CT_SLIDE_LAYOUT =
        "application/vnd.openxmlformats-officedocument.presentationml.slideLayout+xml";
    private static final String CT_THEME =
        "application/vnd.openxmlformats-officedocument.theme+xml";
    private static final String NS_PRESENTATIONML =
        "http://schemas.openxmlformats.org/presentationml/2006/main";
    private static final String NS_RELATIONSHIPS =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships";

    // ── Relationship types ────────────────────────────────────────────────────
    private static final String RT_SLIDE =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide";
    private static final String RT_SLIDE_MASTER =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideMaster";

    // ── Destination package ───────────────────────────────────────────────────
    private OPCPackage destPkg;
    private PackagePart destPresPart;

    // ── Part name counters ────────────────────────────────────────────────────
    private int slideCounter;
    private int masterCounter;
    private int layoutCounter;
    private int mediaCounter;
    private int themeCounter;

    // ── Presentation XML element IDs ──────────────────────────────────────────
    private int  nextSlideId;
    private long nextMasterId;

    // ── Cross-file deduplication: SHA-256 → dest path ─────────────────────────
    private final Map<String, String> mediaHashToPath     = new HashMap<>();
    private final Set<String>         registeredMasterPaths = new HashSet<>();

    // ── Presentation XML kept in memory; written once at toBytes() ─────────────
    private String presXml;

    private PptxPackageMerger() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    public static PptxPackageMerger create(int pageWidth, int pageHeight) throws Exception {
        PptxPackageMerger m = new PptxPackageMerger();

        // 빈 XMLSlideShow를 베이스로 사용 (기본 마스터·레이아웃 포함)
        try (XMLSlideShow base = new XMLSlideShow()) {
            base.setPageSize(new Dimension(pageWidth, pageHeight));
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            base.write(buf);
            m.destPkg = OPCPackage.open(new ByteArrayInputStream(buf.toByteArray()));
        }

        m.destPresPart = m.destPkg.getPartsByContentType(CT_PRESENTATION).get(0);

        // 기존 파트 수를 기반으로 카운터 초기화 (이름 충돌 방지)
        m.slideCounter  = m.destPkg.getPartsByContentType(CT_SLIDE).size()        + 1;
        m.masterCounter = m.destPkg.getPartsByContentType(CT_SLIDE_MASTER).size() + 1;
        m.layoutCounter = m.destPkg.getPartsByContentType(CT_SLIDE_LAYOUT).size() + 1;
        m.themeCounter  = m.destPkg.getPartsByContentType(CT_THEME).size()        + 1;
        m.mediaCounter  = 1;

        m.presXml       = new String(readAll(m.destPresPart.getInputStream()), StandardCharsets.UTF_8);
        m.nextSlideId   = m.parseMaxSlideId()  + 1;
        m.nextMasterId  = m.parseMaxMasterId() + 1;

        return m;
    }

    /**
     * PPTX byte[]의 슬라이드를 순서대로 대상에 추가한다.
     * 슬라이드가 의존하는 레이아웃·마스터·테마·미디어도 모두 함께 복사된다.
     */
    public void appendPptx(byte[] pptxBytes) throws Exception {
        try (OPCPackage srcPkg = OPCPackage.open(new ByteArrayInputStream(pptxBytes))) {
            // 소스 파일 내 경로 매핑 (src 절대 경로 → dest 절대 경로)
            Map<String, String> pathMap = new HashMap<>();

            List<PackagePart> slides = getSlidesInOrder(srcPkg);
            for (PackagePart srcSlide : slides) {
                String destSlidePath = ensureCopied(srcPkg, srcSlide, pathMap);

                URI relUri = PackagingURIHelper.relativizeURI(
                    destPresPart.getPartName().getURI(), new URI(destSlidePath));
                PackageRelationship slideRel = destPresPart.addRelationship(
                    relUri, TargetMode.INTERNAL, RT_SLIDE);
                addSlideToPresXml(slideRel.getId());
            }

            // 새로 추가된 마스터를 presentation의 sldMasterIdLst에 등록
            for (Map.Entry<String, String> entry : pathMap.entrySet()) {
                PackagePart srcPart = getPartSafely(srcPkg, entry.getKey());
                if (srcPart == null || !CT_SLIDE_MASTER.equals(srcPart.getContentType())) continue;

                String destMasterPath = entry.getValue();
                if (registeredMasterPaths.contains(destMasterPath)) continue;
                registeredMasterPaths.add(destMasterPath);

                URI relUri = PackagingURIHelper.relativizeURI(
                    destPresPart.getPartName().getURI(), new URI(destMasterPath));
                PackageRelationship masterRel = destPresPart.addRelationship(
                    relUri, TargetMode.INTERNAL, RT_SLIDE_MASTER);
                addMasterToPresXml(masterRel.getId());
            }
        }
    }

    /** 병합 결과를 byte[]로 반환한다. */
    public byte[] toBytes() throws Exception {
        try (OutputStream os = destPresPart.getOutputStream()) {
            os.write(presXml.getBytes(StandardCharsets.UTF_8));
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        destPkg.save(out);
        return out.toByteArray();
    }

    @Override
    public void close() {
        try {
            if (destPkg != null) destPkg.close();
        } catch (Exception e) {
            log.warn("OPCPackage 닫기 실패", e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Core: recursive part copy
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * srcPart와 그것이 의존하는 모든 파트를 재귀적으로 destPkg에 복사한다.
     * pathMap으로 이미 복사된 파트를 추적해 중복 복사·순환 참조를 방지한다.
     *
     * @return 대상 패키지에서의 절대 경로
     */
    private String ensureCopied(OPCPackage srcPkg, PackagePart srcPart,
                                 Map<String, String> pathMap) throws Exception {
        String srcPath = srcPart.getPartName().getName();
        if (pathMap.containsKey(srcPath)) {
            return pathMap.get(srcPath);
        }

        // 바이너리 파트(image/audio/video/application/* 등)는 raw bytes 그대로 복사한다.
        String ct = srcPart.getContentType();
        if (shouldCopyAsBinary(ct)) {
            return ensureBinaryCopied(srcPart, srcPath, pathMap);
        }

        String destPath = allocateDestPath(ct, srcPath);
        pathMap.put(srcPath, destPath); // 순환 참조 방지를 위해 조기 등록

        PackagePart destPart = destPkg.createPart(
            PackagingURIHelper.createPartName(destPath), ct);

        // 관계 복사: 대상 파트를 먼저 재귀 복사 후 새 관계 ID 매핑
        Map<String, String> oldToNewId = new HashMap<>();
        for (PackageRelationship rel : srcPart.getRelationships()) {
            if (rel.getTargetMode() == TargetMode.EXTERNAL) {
                PackageRelationship newRel = destPart.addExternalRelationship(
                    rel.getTargetURI().toString(), rel.getRelationshipType());
                oldToNewId.put(rel.getId(), newRel.getId());
                continue;
            }

            URI absUri = resolveUri(srcPart.getPartName().getURI(), rel.getTargetURI());
            PackagePart targetPart = getPartSafely(srcPkg, absUri);
            if (targetPart == null) continue;

            String targetDestPath = ensureCopied(srcPkg, targetPart, pathMap);

            URI relUri = PackagingURIHelper.relativizeURI(
                destPart.getPartName().getURI(), new URI(targetDestPath));
            PackageRelationship newRel = destPart.addRelationship(
                relUri, TargetMode.INTERNAL, rel.getRelationshipType());
            oldToNewId.put(rel.getId(), newRel.getId());
        }

        // XML 내 r:id 값을 새 관계 ID로 치환 후 기록
        byte[] content = readAll(srcPart.getInputStream());
        byte[] remapped = remapRelIds(content, oldToNewId);
        try (OutputStream os = destPart.getOutputStream()) {
            os.write(remapped);
        }

        return destPath;
    }

    /** 바이너리 파트 복사 (SHA-256 중복 제거). */
    private String ensureBinaryCopied(PackagePart srcPart, String srcPath,
                                      Map<String, String> pathMap) throws Exception {
        if (pathMap.containsKey(srcPath)) return pathMap.get(srcPath);

        byte[] data = readAll(srcPart.getInputStream());
        String hash = sha256(data);

        String destPath;
        if (mediaHashToPath.containsKey(hash)) {
            destPath = mediaHashToPath.get(hash);
        } else {
            destPath = allocateBinaryDestPath(srcPath);
            PackagePart destMedia = destPkg.createPart(
                PackagingURIHelper.createPartName(destPath), srcPart.getContentType());
            try (OutputStream os = destMedia.getOutputStream()) {
                os.write(data);
            }
            mediaHashToPath.put(hash, destPath);
        }
        pathMap.put(srcPath, destPath);
        return destPath;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Presentation XML helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void addSlideToPresXml(String relId) {
        String entry = "<p:sldId id=\"" + nextSlideId++ + "\" r:id=\"" + relId + "\"/>";
        if (presXml.contains("</p:sldIdLst>")) {
            presXml = presXml.replace("</p:sldIdLst>", entry + "</p:sldIdLst>");
        } else if (presXml.contains("<p:sldIdLst/>")) {
            presXml = presXml.replace("<p:sldIdLst/>", "<p:sldIdLst>" + entry + "</p:sldIdLst>");
        } else {
            presXml = presXml.replace("</p:presentation>",
                "<p:sldIdLst>" + entry + "</p:sldIdLst></p:presentation>");
        }
    }

    private void addMasterToPresXml(String relId) {
        String entry = "<p:sldMasterId id=\"" + nextMasterId++ + "\" r:id=\"" + relId + "\"/>";
        if (presXml.contains("</p:sldMasterIdLst>")) {
            presXml = presXml.replace("</p:sldMasterIdLst>", entry + "</p:sldMasterIdLst>");
        } else {
            presXml = presXml.replace("</p:presentation>",
                "<p:sldMasterIdLst>" + entry + "</p:sldMasterIdLst></p:presentation>");
        }
    }

    private int parseMaxSlideId() {
        Matcher m = Pattern.compile("<p:sldId[^>]+\\bid=\"(\\d+)\"").matcher(presXml);
        int max = 255;
        while (m.find()) max = Math.max(max, Integer.parseInt(m.group(1)));
        return max;
    }

    private long parseMaxMasterId() {
        Matcher m = Pattern.compile("<p:sldMasterId[^>]+\\bid=\"(\\d+)\"").matcher(presXml);
        long max = 2147483646L;
        while (m.find()) max = Math.max(max, Long.parseLong(m.group(1)));
        return max;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilities
    // ─────────────────────────────────────────────────────────────────────────

    private String allocateDestPath(String ct, String srcPath) {
        return switch (ct) {
            case CT_SLIDE        -> "/ppt/slides/slide"             + slideCounter++  + ".xml";
            case CT_SLIDE_MASTER -> "/ppt/slideMasters/slideMaster" + masterCounter++ + ".xml";
            case CT_SLIDE_LAYOUT -> "/ppt/slideLayouts/slideLayout" + layoutCounter++ + ".xml";
            case CT_THEME        -> "/ppt/theme/theme"              + themeCounter++  + ".xml";
            default -> {
                String ext = srcPath.contains(".") ? srcPath.substring(srcPath.lastIndexOf('.')) : "";
                yield "/ppt/media/file" + mediaCounter++ + ext;
            }
        };
    }

    private String allocateBinaryDestPath(String srcPath) {
        String ext = srcPath.contains(".") ? srcPath.substring(srcPath.lastIndexOf('.')) : "";
        return "/ppt/media/file" + mediaCounter++ + ext;
    }

    /** presentation.xml의 sldIdLst 순서대로 슬라이드 파트를 반환한다. */
    private List<PackagePart> getSlidesInOrder(OPCPackage srcPkg) throws Exception {
        PackagePart srcPres = srcPkg.getPartsByContentType(CT_PRESENTATION).get(0);
        Document doc = parseXml(readAll(srcPres.getInputStream()));

        List<String> relIds = new ArrayList<>();
        NodeList slideIdNodes = doc.getElementsByTagNameNS(NS_PRESENTATIONML, "sldId");
        for (int i = 0; i < slideIdNodes.getLength(); i++) {
            Element slideId = (Element) slideIdNodes.item(i);
            String relId = slideId.getAttributeNS(NS_RELATIONSHIPS, "id");
            if (!relId.isBlank()) {
                relIds.add(relId);
            }
        }

        List<PackagePart> slides = new ArrayList<>();
        for (String relId : relIds) {
            PackageRelationship rel = srcPres.getRelationship(relId);
            if (rel == null) continue;
            URI absUri = resolveUri(srcPres.getPartName().getURI(), rel.getTargetURI());
            PackagePart slide = getPartSafely(srcPkg, absUri);
            if (slide != null) slides.add(slide);
        }
        return slides;
    }

    /** XML 바이트 내 r:id 값을 새 관계 ID로 교체한다 (문자열 치환). */
    private static byte[] remapRelIds(byte[] xmlBytes, Map<String, String> oldToNew) {
        if (oldToNew.isEmpty()) return xmlBytes;
        String xml = new String(xmlBytes, StandardCharsets.UTF_8);
        for (Map.Entry<String, String> e : oldToNew.entrySet()) {
            // 큰따옴표·작은따옴표 양쪽 모두 처리
            xml = xml.replace("\"" + e.getKey() + "\"", "\"" + e.getValue() + "\"");
            xml = xml.replace("'"  + e.getKey() + "'",  "'"  + e.getValue() + "'");
        }
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    static boolean isXmlContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.endsWith("/xml")
            || contentType.endsWith("+xml")
            || "application/xml".equals(contentType)
            || "text/xml".equals(contentType);
    }

    static boolean shouldCopyAsBinary(String contentType) {
        return !isXmlContentType(contentType);
    }

    /** base URI에서 target URI를 절대 경로로 해석한다. */
    private static URI resolveUri(URI base, URI target) {
        if (target.isAbsolute()) return target;
        return base.resolve(".").resolve(target).normalize();
    }

    private static PackagePart getPartSafely(OPCPackage pkg, URI absUri) {
        try {
            return pkg.getPart(PackagingURIHelper.createPartName(absUri));
        } catch (Exception e) {
            return null;
        }
    }

    private static PackagePart getPartSafely(OPCPackage pkg, String absPath) {
        try {
            return pkg.getPart(PackagingURIHelper.createPartName(absPath));
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] readAll(InputStream is) throws IOException {
        try (is) {
            return is.readAllBytes();
        }
    }

    private static Document parseXml(byte[] xmlBytes) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(xmlBytes));
    }

    private static String sha256(byte[] data) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
