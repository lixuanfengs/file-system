package net.cactus.service;

import net.cactus.pojo.FileMeta;
import net.cactus.service.dto.FilePreviewResult;

/**
 * 文件预览服务接口
 * 提供各种文件类型的预览功能
 */
public interface FilePreviewService {
    
    /**
     * 预览文件内容
     * @param fileMeta 文件元数据
     * @return 预览结果
     */
    FilePreviewResult previewFile(FileMeta fileMeta);
    
    /**
     * 检查文件是否支持预览
     * @param fileMeta 文件元数据
     * @return 是否支持预览
     */
    boolean isPreviewSupported(FileMeta fileMeta);
    
    /**
     * 获取文件的MIME类型
     * @param fileMeta 文件元数据
     * @return MIME类型
     */
    String detectMimeType(FileMeta fileMeta);
}
