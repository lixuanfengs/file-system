package net.cactus.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.cactus.pojo.FileMeta;
import net.cactus.service.AbstractService;
import net.cactus.service.FileMetaService;
import net.cactus.service.FileService;
import net.cactus.service.StorageService;
import net.cactus.utils.ResultMessage;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service

public class FileServiceImpl extends AbstractService implements FileService {

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileMetaService fileMetaService;

    @Override // net.qiyuesuo.service.FileService
    public String saveFile(byte[] bytes) {
        FileMeta info = this.storageService.storageFile(bytes);
        this.fileMetaService.saveInfo(info);
        return info.getUuid();
    }

    @Override // net.qiyuesuo.service.FileService
    public String saveFile(InputStream input, long size) {
        FileMeta info = this.storageService.storageFile(input, size);
        this.fileMetaService.saveInfo(info);
        return info.getUuid();
    }

    @Override // net.qiyuesuo.service.FileService
    public String saveFile(File file) {
        FileMeta info = this.storageService.storageFile(file.toPath());
        this.fileMetaService.saveInfo(info);
        return info.getUuid();
    }

    @Override // net.qiyuesuo.service.FileService
    public byte[] getFileThrougnBytes(String fileKey) throws IOException {
        File file = this.storageService.getFileByKey(fileKey).toFile();
        return FileUtils.readFileToByteArray(file);
    }

    @Override // net.qiyuesuo.service.FileService
    public void getFileThrouthStream(String uuid, OutputStream output) throws Exception {
        File file = this.storageService.getFileByKey(uuid).toFile();
        new FileOutputStream(file);
    }

    @Override // net.qiyuesuo.service.FileService
    public FileMeta getFileInfo(String fileKey) throws Exception {
        this.logger.info("获取uuid为：" + fileKey + "的文件的元数据信息");
        return this.fileMetaService.getInfo(fileKey);
    }

    @Override // net.qiyuesuo.service.FileService
    public int deleteFile(String fileKey) {
        this.storageService.deleteFile(fileKey);
        return this.fileMetaService.deleteInfo(fileKey);
    }

    @Override // net.qiyuesuo.service.FileService
    public String saveFile(MultipartFile file) {
        FileMeta info = this.storageService.storageFile(file);
        this.fileMetaService.saveInfo(info);
        return info.getUuid();
    }

    @Override // net.qiyuesuo.service.FileService
    public ResultMessage viewFile(String fileKey, String type, HttpServletResponse response) throws Exception {
        this.logger.info("预览文件：viewFile");
        FileMeta info = this.fileMetaService.getInfo(fileKey);
        return this.storageService.viewFile(info, response, type);
    }

    @Override // net.qiyuesuo.service.FileService
    public int deleteFile(FileMeta filemeta) {
        this.storageService.deleteFile(filemeta.getUuid() + "." + filemeta.getSubfix());
        return this.fileMetaService.deleteInfo(filemeta.getUuid());
    }
}
