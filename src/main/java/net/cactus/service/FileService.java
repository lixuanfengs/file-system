package net.cactus.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.cactus.pojo.FileMeta;
import net.cactus.utils.ResultMessage;
import org.springframework.web.multipart.MultipartFile;


public interface FileService {
    String saveFile(byte[] bytes);

    String saveFile(InputStream input, long size);

    String saveFile(File file);

    byte[] getFileThrougnBytes(String fileKey) throws IOException;

    void getFileThrouthStream(String fileKey, OutputStream output) throws Exception;

    FileMeta getFileInfo(String fileKey) throws Exception;

    int deleteFile(String fileKey);

    String saveFile(MultipartFile file);

    int deleteFile(FileMeta filemeta);

    ResultMessage viewFile(String fileKey, String type, HttpServletResponse response) throws Exception;
}
