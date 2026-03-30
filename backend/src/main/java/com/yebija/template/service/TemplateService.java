package com.yebija.template.service;

import com.yebija.church.domain.Church;
import com.yebija.church.repository.ChurchRepository;
import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import com.yebija.template.domain.TemplateItem;
import com.yebija.template.domain.WorshipTemplate;
import com.yebija.template.dto.TemplateCreateRequest;
import com.yebija.template.dto.TemplateItemRequest;
import com.yebija.template.dto.TemplateResponse;
import com.yebija.template.dto.TemplateUpdateRequest;
import com.yebija.template.repository.WorshipTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final WorshipTemplateRepository templateRepository;
    private final ChurchRepository churchRepository;

    @Transactional
    public TemplateResponse create(Long churchId, TemplateCreateRequest request) {
        Church church = churchRepository.findById(churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.CHURCH_NOT_FOUND));

        if (request.isDefault()) {
            clearDefaultFlag(churchId);
        }

        WorshipTemplate template = WorshipTemplate.create(
                church, request.getName(), request.getDescription(), request.isDefault()
        );

        addItems(template, request.getItems());
        return TemplateResponse.from(templateRepository.save(template));
    }

    @Transactional(readOnly = true)
    public List<TemplateResponse> findAll(Long churchId) {
        return templateRepository.findAllByChurchId(churchId).stream()
                .map(TemplateResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TemplateResponse findById(Long churchId, Long templateId) {
        WorshipTemplate template = getTemplate(churchId, templateId);
        return TemplateResponse.from(template);
    }

    @Transactional
    public TemplateResponse update(Long churchId, Long templateId, TemplateUpdateRequest request) {
        WorshipTemplate template = getTemplate(churchId, templateId);

        if (request.isDefault() && !template.isDefault()) {
            clearDefaultFlag(churchId);
        }

        template.update(request.getName(), request.getDescription(), request.isDefault());
        template.clearItems();
        addItems(template, request.getItems());

        return TemplateResponse.from(template);
    }

    @Transactional
    public void delete(Long churchId, Long templateId) {
        WorshipTemplate template = getTemplate(churchId, templateId);
        templateRepository.delete(template);
    }

    private WorshipTemplate getTemplate(Long churchId, Long templateId) {
        return templateRepository.findByIdAndChurchId(templateId, churchId)
                .orElseThrow(() -> new YebijaException(ErrorCode.TEMPLATE_NOT_FOUND));
    }

    private void clearDefaultFlag(Long churchId) {
        templateRepository.findAllByChurchId(churchId).stream()
                .filter(WorshipTemplate::isDefault)
                .forEach(t -> t.update(t.getName(), t.getDescription(), false));
    }

    private void addItems(WorshipTemplate template, List<TemplateItemRequest> itemRequests) {
        for (TemplateItemRequest req : itemRequests) {
            TemplateItem item = TemplateItem.create(
                    template, req.getType(), req.getSeq(), req.getLabel(), req.getDefaultMode()
            );
            template.getItems().add(item);
        }
    }
}
