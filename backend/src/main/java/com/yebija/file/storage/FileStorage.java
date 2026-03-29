package com.yebija.file.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    /**
     * 파일을 저장하고 storageKey를 반환한다.
     */
    String store(MultipartFile file, String storageKey);

    /**
     * storageKey로 파일 바이트를 읽어온다.
     */
    byte[] load(String storageKey);

    /**
     * storageKey로 파일을 삭제한다.
     */
    void delete(String storageKey);
}
