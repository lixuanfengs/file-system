package net.cactus.utils;

import org.springframework.stereotype.Component;

/**
 * 文件服务器工具类
 * 提供文件大小格式化等工具方法
 */
@Component("fileServerUtils")
public class FileServerUtils {
    
    /**
     * 格式化文件大小
     * @param bytes 字节数
     * @return 格式化后的文件大小字符串
     */
    public String formatFileSize(long bytes) {
        if (bytes < 0) {
            return "0 B";
        }
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * 获取文件类型图标
     * @param fileExtension 文件扩展名
     * @return 对应的图标类名或emoji
     */
    public String getFileTypeIcon(String fileExtension) {
        if (fileExtension == null || fileExtension.isEmpty()) {
            return "📄";
        }
        
        String ext = fileExtension.toLowerCase();
        switch (ext) {
            case "pdf":
                return "📕";
            case "doc":
            case "docx":
                return "📘";
            case "xls":
            case "xlsx":
                return "📗";
            case "ppt":
            case "pptx":
                return "📙";
            case "txt":
                return "📝";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "🖼️";
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
                return "🎬";
            case "mp3":
            case "wav":
            case "flac":
                return "🎵";
            case "zip":
            case "rar":
            case "7z":
                return "📦";
            default:
                return "📄";
        }
    }
    
    /**
     * 截断文件名
     * @param filename 原文件名
     * @param maxLength 最大长度
     * @return 截断后的文件名
     */
    public String truncateFilename(String filename, int maxLength) {
        if (filename == null || filename.length() <= maxLength) {
            return filename;
        }
        
        // 保留文件扩展名
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            String name = filename.substring(0, dotIndex);
            String extension = filename.substring(dotIndex);
            
            int availableLength = maxLength - extension.length() - 3; // 3 for "..."
            if (availableLength > 0) {
                return name.substring(0, Math.min(name.length(), availableLength)) + "..." + extension;
            }
        }
        
        return filename.substring(0, maxLength - 3) + "...";
    }
}
