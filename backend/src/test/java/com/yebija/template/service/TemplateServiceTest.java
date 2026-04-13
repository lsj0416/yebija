package com.yebija.template.service;

import com.yebija.church.domain.Church;
import com.yebija.church.domain.enums.Denomination;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.template.dto.TemplateCreateRequest;
import com.yebija.template.repository.WorshipTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private WorshipTemplateRepository templateRepository;

    @Mock
    private ChurchRepository churchRepository;

    @InjectMocks
    private TemplateService templateService;

    @Test
    void createRejectsAutoModeForHymnTemplateItem() {
        Church church = Church.create("교회", Denomination.PRESBYTERIAN, "admin@test.com", "hash");
        ReflectionTestUtils.setField(church, "id", 1L);

        TemplateCreateRequest request = new TemplateCreateRequest();
        ReflectionTestUtils.setField(request, "name", "주일예배");
        ReflectionTestUtils.setField(request, "items", List.of(itemRequest("HYMN", "AUTO")));

        org.mockito.Mockito.when(churchRepository.findById(1L)).thenReturn(Optional.of(church));

        assertThatThrownBy(() -> templateService.create(1L, request))
                .isInstanceOf(YebijaException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ITEM_MODE_NOT_ALLOWED);
    }

    private com.yebija.template.dto.TemplateItemRequest itemRequest(String type, String mode) {
        com.yebija.template.dto.TemplateItemRequest item = new com.yebija.template.dto.TemplateItemRequest();
        ReflectionTestUtils.setField(item, "type", com.yebija.template.domain.enums.ItemType.valueOf(type));
        ReflectionTestUtils.setField(item, "seq", 1);
        ReflectionTestUtils.setField(item, "label", "항목");
        ReflectionTestUtils.setField(item, "defaultMode", com.yebija.template.domain.enums.ItemMode.valueOf(mode));
        return item;
    }
}
