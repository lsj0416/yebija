package com.yebija.file.service;

import com.yebija.church.domain.Church;
import com.yebija.church.domain.enums.Denomination;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.file.domain.UploadedFile;
import com.yebija.file.repository.UploadedFileRepository;
import com.yebija.file.storage.FileStorage;
import com.yebija.worship.domain.Worship;
import com.yebija.worship.domain.WorshipItem;
import com.yebija.worship.repository.WorshipItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileStorage fileStorage;

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private ChurchRepository churchRepository;

    @Mock
    private WorshipItemRepository worshipItemRepository;

    @InjectMocks
    private FileService fileService;

    @Test
    void attachToWorshipItemRejectsCrossChurchItem() {
        Church church = Church.create("교회", Denomination.PRESBYTERIAN, "admin@test.com", "hash");
        ReflectionTestUtils.setField(church, "id", 1L);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                new byte[]{0x50, 0x4B, 0x03, 0x04}
        );

        when(churchRepository.findById(1L)).thenReturn(Optional.of(church));
        when(worshipItemRepository.findByIdAndWorshipChurchId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.attachToWorshipItem(1L, 99L, file))
                .isInstanceOf(YebijaException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.WORSHIP_ITEM_NOT_FOUND);
    }

    @Test
    void attachToWorshipItemReplacesPreviousAttachment() {
        Church church = Church.create("교회", Denomination.PRESBYTERIAN, "admin@test.com", "hash");
        ReflectionTestUtils.setField(church, "id", 1L);
        Worship worship = Worship.create(church, null, LocalDate.of(2026, 4, 13), "주일예배");
        ReflectionTestUtils.setField(worship, "id", 10L);
        WorshipItem item = WorshipItem.create(worship, com.yebija.template.domain.enums.ItemType.HYMN, 1, "찬양", com.yebija.template.domain.enums.ItemMode.FILE);
        ReflectionTestUtils.setField(item, "id", 100L);
        item.updateFileKey("churches/1/old.pptx");

        UploadedFile existing = UploadedFile.create(
                church,
                "old.pptx",
                "churches/1/old.pptx",
                100L,
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        );
        ReflectionTestUtils.setField(existing, "id", 55L);
        existing.linkToWorshipItem(100L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.pptx",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                new byte[]{0x50, 0x4B, 0x03, 0x04}
        );

        when(churchRepository.findById(1L)).thenReturn(Optional.of(church));
        when(worshipItemRepository.findByIdAndWorshipChurchId(100L, 1L)).thenReturn(Optional.of(item));
        when(uploadedFileRepository.findAllByChurchIdAndWorshipItemId(1L, 100L)).thenReturn(List.of(existing));
        when(uploadedFileRepository.save(any(UploadedFile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        fileService.attachToWorshipItem(1L, 100L, file);

        verify(fileStorage).delete("churches/1/old.pptx");
        verify(uploadedFileRepository).delete(existing);
        verify(fileStorage).store(any(), anyString());
    }

    @Test
    void deleteClearsWorshipItemReferenceOnlyForMatchingStorageKey() {
        Church church = Church.create("교회", Denomination.PRESBYTERIAN, "admin@test.com", "hash");
        ReflectionTestUtils.setField(church, "id", 1L);
        Worship worship = Worship.create(church, null, LocalDate.of(2026, 4, 13), "주일예배");
        ReflectionTestUtils.setField(worship, "id", 10L);
        WorshipItem item = WorshipItem.create(worship, com.yebija.template.domain.enums.ItemType.HYMN, 1, "찬양", com.yebija.template.domain.enums.ItemMode.FILE);
        ReflectionTestUtils.setField(item, "id", 100L);
        item.updateFileKey("churches/1/current.pptx");

        UploadedFile uploaded = UploadedFile.create(
                church,
                "current.pptx",
                "churches/1/current.pptx",
                100L,
                "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        );
        ReflectionTestUtils.setField(uploaded, "id", 55L);
        uploaded.linkToWorshipItem(100L);

        when(uploadedFileRepository.findByIdAndChurchId(55L, 1L)).thenReturn(Optional.of(uploaded));
        when(worshipItemRepository.findByIdAndWorshipChurchId(100L, 1L)).thenReturn(Optional.of(item));

        fileService.delete(1L, 55L);

        verify(fileStorage).delete("churches/1/current.pptx");
        verify(uploadedFileRepository).delete(uploaded);
    }
}
