package com.yebija.church.domain;

import com.yebija.church.domain.enums.MemberRole;
import com.yebija.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "church_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChurchMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    public static ChurchMember create(Church church, String email, String passwordHash,
                                      String name, MemberRole role) {
        ChurchMember member = new ChurchMember();
        member.church = church;
        member.email = email;
        member.passwordHash = passwordHash;
        member.name = name;
        member.role = role;
        return member;
    }
}
