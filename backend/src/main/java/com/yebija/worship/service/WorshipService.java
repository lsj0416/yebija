package com.yebija.worship.service;

import com.yebija.church.domain.Church;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.template.domain.TemplateItem;
import com.yebija.template.domain.WorshipTemplate;
import com.yebija.template.repository.WorshipTemplateRepository;
import com.yebija.worship.domain.Worship;
import com.yebija.worship.domain.WorshipItem;
import com.yebija.worship.dto.WorshipCreateRequest;
import com.yebija.worship.dto.WorshipItemUpdateRequest;
import com.yebija.worship.dto.WorshipResponse;
import com.yebija.worship.dto.WorshipUpdateRequest;
import com.yebija.worship.repository.WorshipItemRepository;
import com.yebija.worship.repository.WorshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorshipService {

    private final WorshipRepository worshipRepository;
    private final WorshipItemRepository worshipItemRepository;
    private final ChurchRepository churchRepository;
    private final WorshipTemplateRepository templateRepository;

    @Transactional
    public WorshipResponse create(Long churchId, WorshipCreateRequest request) {
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.CHURCH_NOT_FOUND));

        WorshipTemplate template = null;
        if (request.getTemplateId() != null) {
            template = templateRepository.findByIdAndChurchId(request.getTemplateId(), churchId)
                    .orElseThrow(() -> new YebijaException(ErrorCode.TEMPLATE_NOT_FOUND));
        }

        Worship worship = Worship.create(church, template, request.getWorshipDate(), request.getTitle());

        if (template != null) {
            for (TemplateItem templateItem : template.getItems()) {
                WorshipItem item = WorshipItem.create(
                        worship,
                        templateItem.getType(),
                        templateItem.getSeq(),
                        templateItem.getLabel(),
                        templateItem.getDefaultMode()
                );
                worship.getItems().add(item);
            }
        }

        return WorshipResponse.from(worshipRepository.save(worship));
    }

    @Transactional(readOnly = true)
    public List<WorshipResponse> findAll(Long churchId) {
        return worshipRepository.findAllByChurchIdOrderByWorshipDateDesc(churchId).stream()
                .map(WorshipResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorshipResponse findById(Long churchId, Long worshipId) {
        return WorshipResponse.from(getWorship(churchId, worshipId));
    }

    @Transactional
    public WorshipResponse update(Long churchId, Long worshipId, WorshipUpdateRequest request) {
        Worship worship = getWorship(churchId, worshipId);
        worship.update(request.getWorshipDate(), request.getTitle());
        return WorshipResponse.from(worship);
    }

    @Transactional
    public WorshipResponse complete(Long churchId, Long worshipId) {
        Worship worship = getWorship(churchId, worshipId);
        worship.complete();
        return WorshipResponse.from(worship);
    }

    @Transactional
    public void delete(Long churchId, Long worshipId) {
        Worship worship = getWorship(churchId, worshipId);
        worshipRepository.delete(worship);
    }

    @Transactional
    public WorshipResponse updateItem(Long churchId, Long worshipId, Long itemId,
                                      WorshipItemUpdateRequest request) {
        Worship worship = getWorship(churchId, worshipId);
        WorshipItem item = worshipItemRepository.findByIdAndWorshipId(itemId, worshipId)
                .orElseThrow(() -> new YebijaException(ErrorCode.WORSHIP_ITEM_NOT_FOUND));

        item.updateContent(request.getLabel(), request.getMode(), request.getContent());
        return WorshipResponse.from(worship);
    }

    private Worship getWorship(Long churchId, Long worshipId) {
        return worshipRepository.findByIdAndChurchId(worshipId, churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.WORSHIP_NOT_FOUND));
    }
}
