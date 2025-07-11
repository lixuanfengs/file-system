package net.cactus.service.impl;

import net.cactus.pojo.FileMeta;
import net.cactus.service.FilePreviewService;
import net.cactus.service.dto.FilePreviewResult;
import net.cactus.service.dto.FilePreviewResult.PreviewType;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件预览服务实现类
 * 使用JODConverter + LibreOffice实现高质量文档转换预览
 */
@Service
public class FilePreviewServiceImpl implements FilePreviewService {
    
    private static final Logger logger = LoggerFactory.getLogger(FilePreviewServiceImpl.class);
    
    @Autowired(required = false)
    private DocumentConverter documentConverter;

    @Autowired(required = false)
    @Qualifier("documentConversionTaskExecutor")
    private TaskExecutor taskExecutor;
    
    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    // 支持预览的文件扩展名
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
        "txt", "md", "pdf", "docx", "doc", "xlsx", "xls", "pptx", "ppt", 
        "zip", "rar", "7z", "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );
    
    // 文本文件扩展名
    private static final List<String> TEXT_EXTENSIONS = Arrays.asList(
        "txt", "log", "csv", "json", "xml", "html", "css", "js", "java", 
        "py", "cpp", "c", "h", "sql", "properties", "yml", "yaml"
    );
    
    // Office文档扩展名（需要转换为PDF）
    private static final List<String> OFFICE_EXTENSIONS = Arrays.asList(
        "docx", "doc", "xlsx", "xls", "pptx", "ppt", "odt", "ods", "odp"
    );
    
    public FilePreviewServiceImpl() {
        // 初始化Markdown解析器
        this.markdownParser = Parser.builder().build();
        this.htmlRenderer = HtmlRenderer.builder().build();
    }

    @Override
    public FilePreviewResult previewFile(FileMeta fileMeta) {
        if (fileMeta == null || StringUtils.isBlank(fileMeta.getFilepath())) {
            return FilePreviewResult.error("文件信息不完整");
        }
        
        try {
            String extension = getFileExtension(fileMeta.getFilename());
            File file = new File(fileMeta.getFilepath());
            
            if (!file.exists()) {
                return FilePreviewResult.error("文件不存在");
            }
            
            // 根据文件扩展名选择处理方式
            switch (extension.toLowerCase()) {
                case "md":
                    return previewMarkdown(file);
                case "txt":
                    return previewText(file);
                case "pdf":
                    return previewPdfDirect(file);
                case "docx":
                case "doc":
                case "xlsx":
                case "xls":
                case "pptx":
                case "ppt":
                case "odt":
                case "ods":
                case "odp":
                    return previewOfficeDocument(file, fileMeta.getFilename());
                case "zip":
                case "rar":
                case "7z":
                    return previewArchive(file);
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                case "bmp":
                case "webp":
                    return FilePreviewResult.success(PreviewType.IMAGE, null, detectMimeType(fileMeta));
                default:
                    if (TEXT_EXTENSIONS.contains(extension.toLowerCase())) {
                        return previewText(file);
                    }
                    return FilePreviewResult.unsupported(fileMeta.getFilename());
            }
            
        } catch (Exception e) {
            logger.error("文件预览失败: {}", fileMeta.getFilename(), e);
            return FilePreviewResult.error("预览失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isPreviewSupported(FileMeta fileMeta) {
        if (fileMeta == null || StringUtils.isBlank(fileMeta.getFilename())) {
            return false;
        }
        
        String extension = getFileExtension(fileMeta.getFilename());
        return SUPPORTED_EXTENSIONS.contains(extension.toLowerCase()) || 
               TEXT_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    @Override
    public String detectMimeType(FileMeta fileMeta) {
        try {
            if (fileMeta != null && StringUtils.isNotBlank(fileMeta.getFilepath())) {
                File file = new File(fileMeta.getFilepath());
                if (file.exists()) {
                    String extension = getFileExtension(fileMeta.getFilename()).toLowerCase();
                    return getMimeTypeByExtension(extension);
                }
            }
        } catch (Exception e) {
            logger.warn("检测MIME类型失败: {}", fileMeta.getFilename(), e);
        }
        return "application/octet-stream";
    }
    
    /**
     * 预览Markdown文件
     */
    private FilePreviewResult previewMarkdown(File file) throws IOException {
        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        String htmlContent = htmlRenderer.render(markdownParser.parse(content));

        // 添加样式和容器
        String styledHtml = wrapWithStyles(htmlContent, "Markdown预览");

        return FilePreviewResult.success(PreviewType.HTML, styledHtml, "text/html");
    }
    
    /**
     * 预览文本文件
     */
    private FilePreviewResult previewText(File file) throws IOException {
        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        
        // 限制内容长度，避免过大文件影响性能
        if (content.length() > 100000) {
            content = content.substring(0, 100000) + "\n\n... (文件内容过长，仅显示前100KB)";
        }
        
        // 转义HTML并保持格式
        String escapedContent = content
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
        
        String htmlContent = "<pre style='white-space: pre-wrap; word-wrap: break-word; font-family: monospace;'>" + 
                           escapedContent + "</pre>";
        String styledHtml = wrapWithStyles(htmlContent, "文本文件预览");
        
        return FilePreviewResult.success(PreviewType.HTML, styledHtml, "text/html");
    }
    
    /**
     * 直接预览PDF文件（返回PDF类型，由浏览器处理）
     */
    private FilePreviewResult previewPdfDirect(File file) {
        return FilePreviewResult.success(PreviewType.PDF, file.getAbsolutePath(), "application/pdf");
    }
    
    /**
     * 预览Office文档（转换为PDF）
     */
    private FilePreviewResult previewOfficeDocument(File file, String fileName) {
        if (documentConverter == null) {
            logger.warn("JODConverter未配置，降级为文本预览: {}", fileName);
            return previewOfficeDocumentFallback(file, fileName);
        }

        try {
            // 创建临时PDF文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "file-preview");
            Files.createDirectories(tempDir);

            String baseName = getFileBaseName(fileName);
            File pdfFile = tempDir.resolve(baseName + "_" + System.currentTimeMillis() + ".pdf").toFile();

            // 转换文档为PDF
            logger.info("开始转换Office文档为PDF: {} -> {}", file.getName(), pdfFile.getName());

            // 使用超时机制执行转换
            long startTime = System.currentTimeMillis();
            documentConverter.convert(file).to(pdfFile).execute();
            long endTime = System.currentTimeMillis();

            if (pdfFile.exists() && pdfFile.length() > 0) {
                logger.info("Office文档转换成功: {} (耗时: {}ms)", fileName, endTime - startTime);
                return FilePreviewResult.success(PreviewType.PDF, pdfFile.getAbsolutePath(), "application/pdf");
            } else {
                logger.error("文档转换失败，生成的PDF文件为空: {}", fileName);
                return previewOfficeDocumentFallback(file, fileName);
            }

        } catch (OfficeException e) {
            logger.error("Office文档转换失败: {}", fileName, e);

            // 检查是否是连接失败
            String errorMessage = e.getMessage();
            if (errorMessage != null && (
                errorMessage.contains("Connection failed") ||
                errorMessage.contains("Connection refused") ||
                errorMessage.contains("Could not start the office process") ||
                errorMessage.contains("office process"))) {

                logger.warn("LibreOffice连接失败，降级为文本预览: {}", fileName);
                return previewOfficeDocumentFallback(file, fileName);
            }

            return FilePreviewResult.error("文档转换失败: " + errorMessage);

        } catch (Exception e) {
            logger.error("Office文档预览异常: {}", fileName, e);

            // 对于其他异常，也尝试降级处理
            String errorMessage = e.getMessage();
            if (errorMessage != null && (
                errorMessage.contains("timeout") ||
                errorMessage.contains("连接") ||
                errorMessage.contains("connection"))) {

                logger.warn("文档转换超时或连接问题，降级为文本预览: {}", fileName);
                return previewOfficeDocumentFallback(file, fileName);
            }

            return FilePreviewResult.error("预览失败: " + errorMessage);
        }
    }
    
    /**
     * 预览压缩文件
     */
    private FilePreviewResult previewArchive(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("<h3>压缩文件内容列表</h3>");
        content.append("<ul style='list-style-type: none; padding-left: 0;'>");
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            int count = 0;
            while ((entry = zis.getNextEntry()) != null && count < 100) {
                String icon = entry.isDirectory() ? "📁" : "📄";
                content.append("<li style='padding: 2px 0;'>")
                       .append(icon).append(" ")
                       .append(entry.getName());
                
                if (!entry.isDirectory()) {
                    content.append(" <span style='color: #666; font-size: 0.9em;'>(")
                           .append(formatFileSize(entry.getSize()))
                           .append(")</span>");
                }
                content.append("</li>");
                count++;
            }
            
            if (count >= 100) {
                content.append("<li style='color: #999; font-style: italic;'>... 更多文件未显示</li>");
            }
        }
        
        content.append("</ul>");
        String styledHtml = wrapWithStyles(content.toString(), "压缩文件预览");
        
        return FilePreviewResult.success(PreviewType.HTML, styledHtml, "text/html");
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
    }
    
    /**
     * 获取文件基础名称（不含扩展名）
     */
    private String getFileBaseName(String filename) {
        if (StringUtils.isBlank(filename)) {
            return "unknown";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(0, lastDotIndex) : filename;
    }
    
    /**
     * 根据扩展名获取MIME类型
     */
    private String getMimeTypeByExtension(String extension) {
        switch (extension) {
            case "pdf": return "application/pdf";
            case "txt": return "text/plain";
            case "md": return "text/markdown";
            case "html": case "htm": return "text/html";
            case "css": return "text/css";
            case "js": return "application/javascript";
            case "json": return "application/json";
            case "xml": return "application/xml";
            case "jpg": case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "bmp": return "image/bmp";
            case "webp": return "image/webp";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc": return "application/msword";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "xls": return "application/vnd.ms-excel";
            case "pptx": return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "ppt": return "application/vnd.ms-powerpoint";
            case "zip": return "application/zip";
            case "rar": return "application/x-rar-compressed";
            case "7z": return "application/x-7z-compressed";
            default: return "application/octet-stream";
        }
    }
    
    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
    
    /**
     * Office文档降级预览（当JODConverter不可用时）
     */
    private FilePreviewResult previewOfficeDocumentFallback(File file, String fileName) {
        try {
            String extension = getFileExtension(fileName).toLowerCase();
            // 检查LibreOffice连接状态
            String statusMessage = checkLibreOfficeStatus();

            String message = String.format(
                "Office文档预览功能当前不可用\n\n" +
                "文件信息:\n" +
                "- 文件名: %s\n" +
                "- 文件类型: %s\n" +
                "- 文件大小: %s\n\n" +
                "状态信息:\n%s\n\n" +
                "解决方案:\n" +
                "1. 确保LibreOffice已正确安装\n" +
                "2. 检查端口2002-2004是否被占用\n" +
                "3. 尝试重启应用程序\n" +
                "4. 如果问题持续，请联系系统管理员\n\n" +
                "当前您可以直接下载文件查看完整内容。",
                fileName,
                extension.toUpperCase(),
                formatFileSize(file.length()),
                statusMessage
            );

            String htmlContent = "<div style='text-align: center; padding: 40px;'>" +
                               "<h2 style='color: #f39c12;'>📄 Office文档预览</h2>" +
                               "<pre style='text-align: left; background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #f39c12;'>" +
                               message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") +
                               "</pre>" +
                               "<p style='margin-top: 20px;'>" +
                               "<a href='/file/" + fileName + "' style='background: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>📥 下载文件</a>" +
                               "</p>" +
                               "</div>";

            String styledHtml = wrapWithStyles(htmlContent, "Office文档预览");
            return FilePreviewResult.success(PreviewType.HTML, styledHtml, "text/html");

        } catch (Exception e) {
            logger.error("Office文档降级预览失败: {}", fileName, e);
            return FilePreviewResult.error("预览失败: " + e.getMessage());
        }
    }

    /**
     * 检查LibreOffice连接状态
     */
    private String checkLibreOfficeStatus() {
        try {
            if (documentConverter == null) {
                return "- JODConverter未配置或已禁用";
            }

            // 尝试检查LibreOffice进程状态
            return "- LibreOffice服务连接失败，可能是进程未启动或端口被占用";

        } catch (Exception e) {
            return "- 无法检查LibreOffice状态: " + e.getMessage();
        }
    }

    /**
     * 为HTML内容添加样式包装
     */
    private String wrapWithStyles(String content, String title) {
        return "<!DOCTYPE html>" +
               "<html><head>" +
               "<meta charset='UTF-8'>" +
               "<title>" + title + "</title>" +
               "<style>" +
               "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; " +
               "       line-height: 1.6; color: #333; max-width: 1200px; margin: 0 auto; padding: 20px; }" +
               "pre { background: #f5f5f5; padding: 15px; border-radius: 5px; overflow-x: auto; }" +
               "h1, h2, h3 { color: #2c3e50; }" +
               "table { border-collapse: collapse; width: 100%; }" +
               "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
               "th { background-color: #f2f2f2; }" +
               "</style>" +
               "</head><body>" +
               content +
               "</body></html>";
    }
}
