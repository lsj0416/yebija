package com.yebija.file.storage;

import com.yebija.common.exception.ErrorCode;
import com.yebija.common.exception.YebijaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    private final Path rootPath;

    public LocalFileStorage(@Value("${storage.local.path:/tmp/yebija/uploads}") String uploadPath) {
        this.rootPath = Paths.get(uploadPath);
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new IllegalStateException("파일 업로드 디렉토리를 생성할 수 없습니다: " + uploadPath, e);
        }
    }

    @Override
    public String store(MultipartFile file, String storageKey) {
        try {
            Path target = resolveAndCreateDirs(storageKey);
            file.transferTo(target);
            return storageKey;
        } catch (IOException e) {
            throw new YebijaException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public byte[] load(String storageKey) {
        try {
            return Files.readAllBytes(rootPath.resolve(storageKey));
        } catch (IOException e) {
            throw new YebijaException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(rootPath.resolve(storageKey));
        } catch (IOException e) {
            throw new YebijaException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    private Path resolveAndCreateDirs(String storageKey) throws IOException {
        Path target = rootPath.resolve(storageKey).normalize();
        if (!target.startsWith(rootPath)) {
            throw new YebijaException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        Files.createDirectories(target.getParent());
        return target;
    }
}
