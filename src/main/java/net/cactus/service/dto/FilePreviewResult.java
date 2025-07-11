package net.cactus.service.dto;

/**
 * 文件预览结果DTO
 */
public class FilePreviewResult {
    
    /**
     * 预览类型枚举
     */
    public enum PreviewType {
        HTML,           // HTML内容预览
        TEXT,           // 纯文本预览
        IMAGE,          // 图片预览
        PDF,            // PDF预览
        BINARY,         // 二进制文件直接下载
        ERROR,          // 预览失败
        UNSUPPORTED     // 不支持的文件类型
    }
    
    private PreviewType type;
    private String content;
    private String mimeType;
    private String fileName;
    private String errorMessage;
    private boolean success;
    
    public FilePreviewResult() {}
    
    public FilePreviewResult(PreviewType type, String content, String mimeType) {
        this.type = type;
        this.content = content;
        this.mimeType = mimeType;
        this.success = true;
    }
    
    public static FilePreviewResult success(PreviewType type, String content, String mimeType) {
        return new FilePreviewResult(type, content, mimeType);
    }
    
    public static FilePreviewResult error(String errorMessage) {
        FilePreviewResult result = new FilePreviewResult();
        result.type = PreviewType.ERROR;
        result.errorMessage = errorMessage;
        result.success = false;
        return result;
    }
    
    public static FilePreviewResult unsupported(String fileName) {
        FilePreviewResult result = new FilePreviewResult();
        result.type = PreviewType.UNSUPPORTED;
        result.fileName = fileName;
        result.success = false;
        return result;
    }
    
    // Getters and Setters
    public PreviewType getType() {
        return type;
    }
    
    public void setType(PreviewType type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
