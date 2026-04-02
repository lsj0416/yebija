package com.yebija.file.service;

import com.yebija.church.domain.Church;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.file.domain.UploadedFile;
import com.yebija.file.dto.FileUploadResponse;
import com.yebija.file.repository.UploadedFileRepository;
import com.yebija.file.storage.FileStorage;
import com.yebija.worship.domain.WorshipItem;
import com.yebija.worship.repository.WorshipItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorage fileStorage;
    private final UploadedFileRepository uploadedFileRepository;
    private final ChurchRepository churchRepository;
    private final WorshipItemRepository worshipItemRepository;

    @Transactional
    public FileUploadResponse upload(Long churchId, MultipartFile file) {
        validateFile(file);

        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.CHURCH_NOT_FOUND));

        String storageKey = buildStorageKey(churchId, file.getOriginalFilename());
        fileStorage.store(file, storageKey);

        UploadedFile uploadedFile = UploadedFile.create(
                church,
                file.getOriginalFilename(),
                storageKey,
                file.getSize(),
                file.getContentType()
        );

        return FileUploadResponse.from(uploadedFileRepository.save(uploadedFile));
    }

    @Transactional
    public FileUploadResponse attachToWorshipItem(Long churchId, Long worshipItemId, MultipartFile file) {
        validateFile(file);

        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.CHURCH_NOT_FOUND));

        WorshipItem worshipItem = worshipItemRepository.findById(worshipItemId)
                .orElseThrow(() -> new YebijaException(ErrorCode.WORSHIP_ITEM_NOT_FOUND));

        String storageKey = buildStorageKey(churchId, file.getOriginalFilename());
        fileStorage.store(file, storageKey);

        UploadedFile uploadedFile = UploadedFile.create(
                church,
                file.getOriginalFilename(),
                storageKey,
                file.getSize(),
                file.getContentType()
        );
        uploadedFile.linkToWorshipItem(worshipItemId);
        worshipItem.updateFileKey(storageKey);

        return FileUploadResponse.from(uploadedFileRepository.save(uploadedFile));
    }

    public byte[] download(Long churchId, Long fileId) {
        UploadedFile uploadedFile = uploadedFileRepository.findByIdAndChurchId(fileId, churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.FILE_NOT_FOUND));
        return fileStorage.load(uploadedFile.getStorageKey());
    }

    @Transactional
    public void delete(Long churchId, Long fileId) {
        UploadedFile uploadedFile = uploadedFileRepository.findByIdAndChurchId(fileId, churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.FILE_NOT_FOUND));
        fileStorage.delete(uploadedFile.getStorageKey());
        uploadedFileRepository.delete(uploadedFile);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new YebijaException(ErrorCode.FILE_EMPTY);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
            throw new YebijaException(ErrorCode.FILE_INVALID_TYPE);
        }
        // PPTX는 ZIP 기반(OOXML) → 매직 바이트 PK (0x50 0x4B) 로 실제 포맷 검증
        // .ppt(OLE2) 파일을 .pptx로 이름 변경한 경우 MIME 검사를 통과하지만 여기서 차단
        try (InputStream is = file.getInputStream()) {
            byte[] magic = new byte[2];
            if (is.read(magic) < 2 || magic[0] != 0x50 || magic[1] != 0x4B) {
                throw new YebijaException(ErrorCode.FILE_INVALID_TYPE);
            }
        } catch (IOException e) {
            throw new YebijaException(ErrorCode.FILE_EMPTY);
        }
    }

    private String buildStorageKey(Long churchId, String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "churches/" + churchId + "/" + UUID.randomUUID() + ext;
    }
}
