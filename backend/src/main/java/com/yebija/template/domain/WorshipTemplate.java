package com.yebija.template.domain;

import com.yebija.church.domain.Church;
import com.yebija.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "worship_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorshipTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean isDefault = false;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seq ASC")
    private List<TemplateItem> items = new ArrayList<>();

    public static WorshipTemplate create(Church church, String name, String description, boolean isDefault) {
        WorshipTemplate template = new WorshipTemplate();
        template.church = church;
        template.name = name;
        template.description = description;
        template.isDefault = isDefault;
        return template;
    }

    public void update(String name, String description, boolean isDefault) {
        this.name = name;
        this.description = description;
        this.isDefault = isDefault;
    }

    public void clearItems() {
        this.items.clear();
    }
}
