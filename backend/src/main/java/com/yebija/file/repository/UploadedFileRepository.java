package com.yebija.file.repository;

import com.yebija.file.domain.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    Optional<UploadedFile> findByIdAndChurchId(Long id, Long churchId);

    Optional<UploadedFile> findByStorageKey(String storageKey);
}
