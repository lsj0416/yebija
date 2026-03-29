package com.yebija.church.repository;

import com.yebija.church.domain.Church;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChurchRepository extends JpaRepository<Church, Long> {

    Optional<Church> findByAdminEmail(String adminEmail);

    boolean existsByAdminEmail(String adminEmail);
}
