package com.yebija.church.repository;

import com.yebija.church.domain.ChurchMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChurchMemberRepository extends JpaRepository<ChurchMember, Long> {

    Optional<ChurchMember> findByEmail(String email);

    boolean existsByEmail(String email);
}
