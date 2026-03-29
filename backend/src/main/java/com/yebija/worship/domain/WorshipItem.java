package com.yebija.worship.domain;

import com.yebija.template.domain.enums.ItemMode;
import com.yebija.template.domain.enums.ItemType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "worship_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorshipItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worship_id", nullable = false)
    private Worship worship;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemType type;

    @Column(nullable = false)
    private int seq;

    @Column(length = 100)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ItemMode mode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> content;

    @Column(length = 500)
    private String fileStorageKey;

    public static WorshipItem create(Worship worship, ItemType type, int seq,
                                     String label, ItemMode mode) {
        WorshipItem item = new WorshipItem();
        item.worship = worship;
        item.type = type;
        item.seq = seq;
        item.label = label;
        item.mode = mode;
        return item;
    }

    public void updateContent(String label, ItemMode mode, Map<String, Object> content) {
        this.label = label;
        this.mode = mode;
        this.content = content;
    }

    public void updateFileKey(String fileStorageKey) {
        this.fileStorageKey = fileStorageKey;
    }
}
