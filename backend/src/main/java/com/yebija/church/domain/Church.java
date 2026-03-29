package com.yebija.church.domain;

import com.yebija.church.domain.enums.Denomination;
import com.yebija.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "church")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Church extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Denomination denomination;

    @Column(nullable = false, unique = true, length = 200)
    private String adminEmail;

    @Column(nullable = false)
    private String passwordHash;

    public static Church create(String name, Denomination denomination,
                                String adminEmail, String passwordHash) {
        Church church = new Church();
        church.name = name;
        church.denomination = denomination;
        church.adminEmail = adminEmail;
        church.passwordHash = passwordHash;
        return church;
    }
}
