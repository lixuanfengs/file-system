package net.cactus.service;

import java.io.InputStream;
import java.nio.file.Path;
import jakarta.servlet.http.HttpServletResponse;
import net.cactus.pojo.FileMeta;
import net.cactus.utils.ResultMessage;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/service/StorageService.class */
public interface StorageService {
    FileMeta storageFile(InputStream input, long size);

    FileMeta storageFile(byte[] bytes);

    FileMeta storageFile(Path path);

    Path getFileByKey(String fileKey);

    void deleteFile(String uuid);

    FileMeta storageFile(MultipartFile file);

    ResultMessage viewFile(FileMeta fileInfo, HttpServletResponse response, String type) throws Exception;
}
