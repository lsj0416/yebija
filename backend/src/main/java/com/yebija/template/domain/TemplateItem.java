package com.yebija.template.domain;

import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemplateItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private WorshipTemplate template;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemType type;

    @Column(nullable = false)
    private int seq;

    @Column(length = 100)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ItemMode defaultMode = ItemMode.AUTO;

    public static TemplateItem create(WorshipTemplate template, ItemType type, int seq, String label, ItemMode defaultMode) {
        TemplateItem item = new TemplateItem();
        item.template = template;
        item.type = type;
        item.seq = seq;
        item.label = label;
        item.defaultMode = defaultMode != null ? defaultMode : ItemMode.AUTO;
        return item;
    }
}
