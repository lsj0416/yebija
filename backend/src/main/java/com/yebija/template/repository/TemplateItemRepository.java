package com.yebija.template.repository;

import com.yebija.template.domain.TemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateItemRepository extends JpaRepository<TemplateItem, Long> {
}
