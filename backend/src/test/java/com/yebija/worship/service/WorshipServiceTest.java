package com.yebija.worship.service;

import com.yebija.church.domain.Church;
import com.yebija.church.domain.enums.Denomination;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.file.service.FileService;
import com.yebija.template.domain.WorshipTemplate;
import com.yebija.template.repository.WorshipTemplateRepository;
import com.yebija.worship.domain.Worship;
import com.yebija.worship.domain.WorshipItem;
import com.yebija.worship.dto.WorshipItemUpdateRequest;
import com.yebija.worship.repository.WorshipItemRepository;
import com.yebija.worship.repository.WorshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorshipServiceTest {

    @Mock
    private WorshipRepository worshipRepository;

    @Mock
    private WorshipItemRepository worshipItemRepository;

    @Mock
    private ChurchRepository churchRepository;

    @Mock
    private WorshipTemplateRepository templateRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private WorshipService worshipService;

    @Test
    void updateItemRejectsAutoModeForHymn() {
        Church church = Church.create("교회", Denomination.PRESBYTERIAN, "admin@test.com", "hash");
        ReflectionTestUtils.setField(church, "id", 1L);
        Worship worship = Worship.create(church, (WorshipTemplate) null, LocalDate.of(2026, 4, 13), "주일예배");
        ReflectionTestUtils.setField(worship, "id", 10L);
        WorshipItem item = WorshipItem.create(worship, com.yebija.template.domain.enums.ItemType.HYMN, 1, "찬양", com.yebija.template.domain.enums.ItemMode.FILE);
        ReflectionTestUtils.setField(item, "id", 100L);

        WorshipItemUpdateRequest request = new WorshipItemUpdateRequest();
        ReflectionTestUtils.setField(request, "mode", com.yebija.template.domain.enums.ItemMode.AUTO);
        ReflectionTestUtils.setField(request, "content", Map.of("text", "invalid"));

        when(worshipRepository.findByIdAndChurchId(10L, 1L)).thenReturn(Optional.of(worship));
        when(worshipItemRepository.findByIdAndWorshipId(100L, 10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> worshipService.updateItem(1L, 10L, 100L, request))
                .isInstanceOf(YebijaException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ITEM_MODE_NOT_ALLOWED);
    }
}
