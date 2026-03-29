package com.yebija.template.repository;

import com.yebija.template.domain.WorshipTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorshipTemplateRepository extends JpaRepository<WorshipTemplate, Long> {

    List<WorshipTemplate> findAllByChurchId(Long churchId);

    Optional<WorshipTemplate> findByIdAndChurchId(Long id, Long churchId);

    boolean existsByChurchIdAndIsDefault(Long churchId, boolean isDefault);
}
