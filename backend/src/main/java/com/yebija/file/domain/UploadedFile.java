package com.yebija.file.domain;

import com.yebija.church.domain.Church;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Column(name = "worship_item_id")
    private Long worshipItemId;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, unique = true, length = 500)
    private String storageKey;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static UploadedFile create(Church church, String originalName,
                                      String storageKey, Long fileSize, String mimeType) {
        UploadedFile file = new UploadedFile();
        file.church = church;
        file.originalName = originalName;
        file.storageKey = storageKey;
        file.fileSize = fileSize;
        file.mimeType = mimeType;
        return file;
    }

    public void linkToWorshipItem(Long worshipItemId) {
        this.worshipItemId = worshipItemId;
    }
}
