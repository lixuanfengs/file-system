package net.cactus.service.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import jakarta.servlet.http.HttpServletResponse;
import net.coobird.thumbnailator.Thumbnails;
import net.cactus.config.Config;
import net.cactus.pojo.FileMeta;
import net.cactus.service.AbstractService;
import net.cactus.service.StorageService;
import net.cactus.utils.RandomUtils;
import net.cactus.utils.ResultMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service

public class StorageServiceImpl extends AbstractService implements StorageService {
    String[] fileTypes = {".pdf", ".jpg", ".jpeg", ".png", ".gif", ".doc", ".docx", ".xlsx", ".xls"};

    @Autowired
    private Config CONFIG;

    @Override
    public FileMeta storageFile(InputStream input, long size) {
        String fileKey = RandomUtils.getRandomFileKey();
        Path path = null;
        try {
            path = getFileByKey(fileKey);
            try (OutputStream output = new FileOutputStream(path.toFile())) {
                byte[] bt = new byte[1024];
                int len;
                while ((len = input.read(bt)) != -1) {
                    output.write(bt, 0, len);
                }
                output.flush();
                this.logger.info("StorageService存储文件：将uuid为：" + fileKey + "的数据输出到" + path.toFile().getAbsolutePath() + "中");
            }
            return new FileMeta(fileKey, "", "", Long.valueOf(path.toFile().length()), "", new Date());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件存储失败", e);
        }
    }

    @Override
    public FileMeta storageFile(byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        return storageFile(input, bytes.length);
    }

    @Override
    public FileMeta storageFile(Path path) {
        try (InputStream input = new FileInputStream(path.toFile())) {
            return storageFile(input, path.toFile().length());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件存储失败", e);
        }
    }

    @Override
    public Path getFileByKey(String fileKey) {
        String firstDirString = this.CONFIG.FILESTORE + "/" + fileKey.substring(0, 2);
        String secondDirString = firstDirString + "/" + fileKey.substring(2, 4);
        Path path = null;
        try {
            if (!Files.exists(Paths.get(firstDirString))) {
                Files.createDirectories(Paths.get(firstDirString));
            }
            if (!Files.exists(Paths.get(secondDirString))) {
                Files.createDirectories(Paths.get(secondDirString));
            }
            Path filePath = Paths.get(secondDirString + "/" + fileKey);
            if (!Files.exists(filePath)) {
                path = Files.createFile(filePath);
            } else {
                path = filePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("创建文件路径失败", e);
        }
        this.logger.info("读取uuid为：" + fileKey + "的文件");
        return path;
    }

    @Override
    public void deleteFile(String uuid) {
        Path path = Paths.get(this.CONFIG.FILESTORE + "/" + uuid.substring(0, 2) + "/" + uuid.substring(2, 4) + "/" + uuid);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.logger.info("storageService文件存储：删除uuid为：" + uuid + "的文件");
    }

    @Override
    public FileMeta storageFile(MultipartFile file) {
        String fileKey = RandomUtils.getRandomFileKey();
        String filename = file.getOriginalFilename();
        if (filename == null) {
            filename = "unknown";
        }

        // 处理Base64编码的文件名
        try {
            filename = new String(Base64.getDecoder().decode(filename), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 如果不是Base64编码，直接使用原文件名
        }

        String subfix = "";
        if (filename.contains(".")) {
            subfix = filename.substring(filename.lastIndexOf('.') + 1);
        }

        Path path = getFileByKey(fileKey + "." + subfix);
        try {
            Files.write(path, file.getBytes());
            if (subfix.equalsIgnoreCase("jpg") || subfix.equalsIgnoreCase("jpeg") ||
                subfix.equalsIgnoreCase("png") || subfix.equalsIgnoreCase("gif")) {
                try {
                    Path path2 = getFileByKey(fileKey + "_." + subfix);
                    Thumbnails.of(path.toString()).size(247, 247).toFile(path2.toString());
                } catch (Exception e) {
                    this.logger.warn("生成缩略图失败: " + e.getMessage());
                }
            }
            this.logger.info("StorageService存储文件：将uuid为：" + fileKey + "的数据输出到" + path.toFile().getAbsolutePath() + "中");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件存储失败", e);
        }
        return new FileMeta(fileKey, filename, path.toFile().getAbsolutePath(), Long.valueOf(file.getSize()), subfix, new Date());
    }

    @Override
    public ResultMessage viewFile(FileMeta fileInfo, HttpServletResponse response, String type) throws Exception {
        if (fileInfo == null) {
            return ResultMessage.newErrorMessage("文件不存在！");
        }
        String fileName = fileInfo.getFilename();
        if (StringUtils.isBlank(fileName)) {
            return ResultMessage.newErrorMessage("文件不存在！");
        }
        String filePath = fileInfo.getFilepath();
        if (StringUtils.isBlank(filePath)) {
            return ResultMessage.newErrorMessage("文件不存在！");
        }
        String subfix = fileName.substring(fileName.lastIndexOf(46) + 1, fileName.length());
        readFile(response, filePath, subfix, fileInfo.getUuid(), type);
        return null;
    }

    private void readFile(HttpServletResponse response, String filePath, String subfix, String key, String type) throws IOException {
        response.setStatus(200);
        if (subfix.equalsIgnoreCase("jpg") || subfix.equalsIgnoreCase("jpeg") || subfix.equalsIgnoreCase("png") || subfix.equalsIgnoreCase("gif")) {
            response.setContentType("image/jpeg;charset=UTF-8");
        } else if (subfix.equalsIgnoreCase("pdf")) {
            response.setContentType("application/pdf;charset=UTF-8");
        }

        String targetPath = filePath;
        if ("2".equals(type)) {
            targetPath = filePath.replace(key, key + "_");
        }

        Path path = Paths.get(targetPath);

        try (OutputStream out = response.getOutputStream();
             FileInputStream in = FileUtils.openInputStream(path.toFile())) {
            FileChannel channel = in.getChannel();
            ByteBuffer bf = ByteBuffer.allocate(2048);
            int length;
            while ((length = channel.read(bf)) != -1) {
                out.write(bf.array(), 0, length);
                bf.clear();
            }
            out.flush();
        }
    }
}
