package net.cactus.controller;

import net.cactus.pojo.FileMeta;
import net.cactus.service.FileMetaService;
import net.cactus.service.FilePreviewService;
import net.cactus.service.StorageService;
import net.cactus.service.dto.FilePreviewResult;
import net.cactus.utils.ResultMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 增强的文件预览控制器
 * 支持多种文件类型的预览功能
 */
@RestController
@RequestMapping("/file/preview")
public class FilePreviewController {
    
    private static final Logger logger = LoggerFactory.getLogger(FilePreviewController.class);
    
    @Autowired
    private FilePreviewService filePreviewService;
    
    @Autowired
    private FileMetaService fileMetaService;
    
    @Autowired
    private StorageService storageService;
    
    /**
     * 预览文件内容
     * @param fileKey 文件唯一标识
     * @return 预览结果
     */
    @GetMapping("/{fileKey}")
    public ResponseEntity<?> previewFile(@PathVariable String fileKey) {
        try {
            if (StringUtils.isBlank(fileKey)) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body(createErrorHtml("文件标识不能为空"));
            }
            
            // 获取文件元数据
            FileMeta fileMeta = fileMetaService.getInfo(fileKey);
            if (fileMeta == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 检查是否支持预览
            if (!filePreviewService.isPreviewSupported(fileMeta)) {
                return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(createUnsupportedHtml(fileMeta.getFilename()));
            }
            
            // 执行预览
            FilePreviewResult result = filePreviewService.previewFile(fileMeta);
            
            if (!result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(createErrorHtml(result.getErrorMessage()));
            }
            
            // 根据预览类型返回不同的响应
            switch (result.getType()) {
                case HTML:
                    return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                        .body(result.getContent());

                case TEXT:
                    return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_PLAIN)
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                        .body(result.getContent());
                        
                case IMAGE:
                    // 对于图片，重定向到原始的viewFile接口
                    return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/file/viewFile/" + fileKey)
                        .build();
                        
                case PDF:
                    // 对于PDF，直接返回PDF文件流
                    return servePdfFile(result.getContent(), fileMeta.getFilename());
                        
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.TEXT_HTML)
                        .body(createErrorHtml("不支持的预览类型"));
            }
            
        } catch (Exception e) {
            logger.error("预览文件失败: {}", fileKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_HTML)
                .body(createErrorHtml("预览文件时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 服务PDF文件
     */
    private ResponseEntity<Resource> servePdfFile(String pdfPath, String originalFileName) {
        try {
            File pdfFile = new File(pdfPath);
            if (!pdfFile.exists()) {
                throw new FileNotFoundException("PDF文件不存在: " + pdfPath);
            }

            Resource resource = new FileSystemResource(pdfFile);

            // 生成安全的文件名
            String safeFileName = generateSafeFileName(originalFileName, "pdf");

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + safeFileName + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .body(resource);

        } catch (Exception e) {
            logger.error("PDF文件服务失败: {}", pdfPath, e);
            // 返回空的Resource
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 生成安全的文件名
     */
    private String generateSafeFileName(String originalFileName, String extension) {
        if (StringUtils.isBlank(originalFileName)) {
            return "preview." + extension;
        }

        // 移除扩展名并添加新扩展名
        String baseName = originalFileName;
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            baseName = originalFileName.substring(0, lastDotIndex);
        }

        // 清理文件名中的特殊字符
        baseName = baseName.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5_\\-\\s]", "_");

        return baseName + "_preview." + extension;
    }

    /**
     * 检查文件是否支持预览
     * @param fileKey 文件唯一标识
     * @return 检查结果
     */
    @GetMapping("/check/{fileKey}")
    public ResultMessage checkPreviewSupport(@PathVariable String fileKey) {
        try {
            if (StringUtils.isBlank(fileKey)) {
                return ResultMessage.newErrorMessage("文件标识不能为空");
            }
            
            FileMeta fileMeta = fileMetaService.getInfo(fileKey);
            if (fileMeta == null) {
                return ResultMessage.newErrorMessage("文件不存在");
            }
            
            boolean supported = filePreviewService.isPreviewSupported(fileMeta);
            String mimeType = filePreviewService.detectMimeType(fileMeta);
            
            return ResultMessage.newSuccessMessage(new PreviewCheckResult(supported, mimeType));
            
        } catch (Exception e) {
            logger.error("检查文件预览支持失败: {}", fileKey, e);
            return ResultMessage.newErrorMessage("检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取文件信息和预览状态
     * @param fileKey 文件唯一标识
     * @return 文件信息
     */
    @GetMapping("/info/{fileKey}")
    public ResultMessage getFilePreviewInfo(@PathVariable String fileKey) {
        try {
            if (StringUtils.isBlank(fileKey)) {
                return ResultMessage.newErrorMessage("文件标识不能为空");
            }
            
            FileMeta fileMeta = fileMetaService.getInfo(fileKey);
            if (fileMeta == null) {
                return ResultMessage.newErrorMessage("文件不存在");
            }
            
            boolean supported = filePreviewService.isPreviewSupported(fileMeta);
            String mimeType = filePreviewService.detectMimeType(fileMeta);
            
            FilePreviewInfo info = new FilePreviewInfo();
            info.setFileMeta(fileMeta);
            info.setPreviewSupported(supported);
            info.setMimeType(mimeType);
            
            return ResultMessage.newSuccessMessage(info);
            
        } catch (Exception e) {
            logger.error("获取文件预览信息失败: {}", fileKey, e);
            return ResultMessage.newErrorMessage("获取信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建错误页面HTML
     */
    private String createErrorHtml(String errorMessage) {
        return "<!DOCTYPE html>" +
               "<html><head>" +
               "<meta charset='UTF-8'>" +
               "<title>预览错误</title>" +
               "<style>" +
               "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; " +
               "       text-align: center; padding: 50px; color: #666; }" +
               ".error-container { max-width: 500px; margin: 0 auto; }" +
               ".error-icon { font-size: 64px; margin-bottom: 20px; }" +
               ".error-title { font-size: 24px; font-weight: bold; margin-bottom: 10px; color: #e74c3c; }" +
               ".error-message { font-size: 16px; line-height: 1.5; }" +
               "</style>" +
               "</head><body>" +
               "<div class='error-container'>" +
               "<div class='error-icon'>❌</div>" +
               "<div class='error-title'>预览失败</div>" +
               "<div class='error-message'>" + (errorMessage != null ? errorMessage : "未知错误") + "</div>" +
               "</div>" +
               "</body></html>";
    }
    
    /**
     * 创建不支持预览的页面HTML
     */
    private String createUnsupportedHtml(String fileName) {
        return "<!DOCTYPE html>" +
               "<html><head>" +
               "<meta charset='UTF-8'>" +
               "<title>不支持预览</title>" +
               "<style>" +
               "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; " +
               "       text-align: center; padding: 50px; color: #666; }" +
               ".unsupported-container { max-width: 500px; margin: 0 auto; }" +
               ".unsupported-icon { font-size: 64px; margin-bottom: 20px; }" +
               ".unsupported-title { font-size: 24px; font-weight: bold; margin-bottom: 10px; color: #f39c12; }" +
               ".unsupported-message { font-size: 16px; line-height: 1.5; margin-bottom: 20px; }" +
               ".download-btn { display: inline-block; padding: 10px 20px; background: #3498db; " +
               "               color: white; text-decoration: none; border-radius: 5px; }" +
               ".download-btn:hover { background: #2980b9; }" +
               "</style>" +
               "</head><body>" +
               "<div class='unsupported-container'>" +
               "<div class='unsupported-icon'>📄</div>" +
               "<div class='unsupported-title'>暂不支持预览</div>" +
               "<div class='unsupported-message'>文件 \"" + (fileName != null ? fileName : "未知文件") + "\" 的格式暂不支持在线预览</div>" +
               "<a href='javascript:history.back()' class='download-btn'>返回</a>" +
               "</div>" +
               "</body></html>";
    }
    
    /**
     * 预览检查结果
     */
    public static class PreviewCheckResult {
        private boolean supported;
        private String mimeType;
        
        public PreviewCheckResult(boolean supported, String mimeType) {
            this.supported = supported;
            this.mimeType = mimeType;
        }
        
        // Getters and Setters
        public boolean isSupported() { return supported; }
        public void setSupported(boolean supported) { this.supported = supported; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    }
    
    /**
     * 文件预览信息
     */
    public static class FilePreviewInfo {
        private FileMeta fileMeta;
        private boolean previewSupported;
        private String mimeType;
        
        // Getters and Setters
        public FileMeta getFileMeta() { return fileMeta; }
        public void setFileMeta(FileMeta fileMeta) { this.fileMeta = fileMeta; }
        public boolean isPreviewSupported() { return previewSupported; }
        public void setPreviewSupported(boolean previewSupported) { this.previewSupported = previewSupported; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    }
}
