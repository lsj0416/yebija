package com.yebija.worship.repository;

import com.yebija.worship.domain.WorshipItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorshipItemRepository extends JpaRepository<WorshipItem, Long> {

    Optional<WorshipItem> findByIdAndWorshipId(Long id, Long worshipId);
}
