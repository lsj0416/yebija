package com.yebija.worship.domain;

import com.yebija.church.domain.Church;
import com.yebija.common.entity.BaseEntity;
import com.yebija.template.domain.WorshipTemplate;
import com.yebija.worship.domain.enums.WorshipStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "worship")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Worship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private WorshipTemplate template;

    @Column(nullable = false)
    private LocalDate worshipDate;

    @Column(length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorshipStatus status = WorshipStatus.DRAFT;

    @OneToMany(mappedBy = "worship", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seq ASC")
    private List<WorshipItem> items = new ArrayList<>();

    public static Worship create(Church church, WorshipTemplate template,
                                 LocalDate worshipDate, String title) {
        Worship worship = new Worship();
        worship.church = church;
        worship.template = template;
        worship.worshipDate = worshipDate;
        worship.title = title;
        return worship;
    }

    public void update(LocalDate worshipDate, String title) {
        this.worshipDate = worshipDate;
        this.title = title;
    }

    public void complete() {
        this.status = WorshipStatus.COMPLETED;
    }
}
