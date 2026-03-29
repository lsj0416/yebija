package com.yebija.worship.repository;

import com.yebija.worship.domain.Worship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorshipRepository extends JpaRepository<Worship, Long> {

    List<Worship> findAllByChurchIdOrderByWorshipDateDesc(Long churchId);

    Optional<Worship> findByIdAndChurchId(Long id, Long churchId);
}
