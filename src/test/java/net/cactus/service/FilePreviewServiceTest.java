package net.cactus.service;

import net.cactus.pojo.FileMeta;
import net.cactus.service.dto.FilePreviewResult;
import net.cactus.service.impl.FilePreviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件预览服务测试
 */
public class FilePreviewServiceTest {
    
    private FilePreviewService filePreviewService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        filePreviewService = new FilePreviewServiceImpl();
    }
    
    @Test
    void testPreviewTextFile() throws IOException {
        // 创建测试文本文件
        Path textFile = tempDir.resolve("test.txt");
        Files.write(textFile, "Hello, World!\nThis is a test file.".getBytes());
        
        FileMeta fileMeta = new FileMeta();
        fileMeta.setUuid("test-uuid");
        fileMeta.setFilename("test.txt");
        fileMeta.setFilepath(textFile.toString());
        fileMeta.setFilesize(textFile.toFile().length());
        fileMeta.setSubfix("txt");
        fileMeta.setCreatetime(new Date());
        
        // 测试预览
        FilePreviewResult result = filePreviewService.previewFile(fileMeta);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(FilePreviewResult.PreviewType.HTML, result.getType());
        assertNotNull(result.getContent());
        assertTrue(result.getContent().contains("Hello, World!"));
    }
    
    @Test
    void testPreviewMarkdownFile() throws IOException {
        // 创建测试Markdown文件
        Path mdFile = tempDir.resolve("test.md");
        String markdownContent = "# Test Markdown\n\nThis is a **bold** text and *italic* text.\n\n- Item 1\n- Item 2";
        Files.write(mdFile, markdownContent.getBytes());
        
        FileMeta fileMeta = new FileMeta();
        fileMeta.setUuid("test-md-uuid");
        fileMeta.setFilename("test.md");
        fileMeta.setFilepath(mdFile.toString());
        fileMeta.setFilesize(mdFile.toFile().length());
        fileMeta.setSubfix("md");
        fileMeta.setCreatetime(new Date());
        
        // 测试预览
        FilePreviewResult result = filePreviewService.previewFile(fileMeta);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(FilePreviewResult.PreviewType.HTML, result.getType());
        assertNotNull(result.getContent());
        assertTrue(result.getContent().contains("<h1>"));
        assertTrue(result.getContent().contains("<strong>"));
        assertTrue(result.getContent().contains("<em>"));
    }
    
    @Test
    void testIsPreviewSupported() {
        FileMeta textFile = new FileMeta();
        textFile.setFilename("test.txt");
        assertTrue(filePreviewService.isPreviewSupported(textFile));
        
        FileMeta mdFile = new FileMeta();
        mdFile.setFilename("test.md");
        assertTrue(filePreviewService.isPreviewSupported(mdFile));
        
        FileMeta pdfFile = new FileMeta();
        pdfFile.setFilename("test.pdf");
        assertTrue(filePreviewService.isPreviewSupported(pdfFile));
        
        FileMeta unsupportedFile = new FileMeta();
        unsupportedFile.setFilename("test.unknown");
        assertFalse(filePreviewService.isPreviewSupported(unsupportedFile));
    }
    
    @Test
    void testPreviewNonExistentFile() {
        FileMeta fileMeta = new FileMeta();
        fileMeta.setUuid("non-existent");
        fileMeta.setFilename("non-existent.txt");
        fileMeta.setFilepath("/non/existent/path.txt");
        fileMeta.setSubfix("txt");
        
        FilePreviewResult result = filePreviewService.previewFile(fileMeta);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(FilePreviewResult.PreviewType.ERROR, result.getType());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testPreviewUnsupportedFile() throws IOException {
        // 创建不支持的文件类型
        Path unsupportedFile = tempDir.resolve("test.unknown");
        Files.write(unsupportedFile, "Some content".getBytes());
        
        FileMeta fileMeta = new FileMeta();
        fileMeta.setUuid("unsupported-uuid");
        fileMeta.setFilename("test.unknown");
        fileMeta.setFilepath(unsupportedFile.toString());
        fileMeta.setSubfix("unknown");
        
        FilePreviewResult result = filePreviewService.previewFile(fileMeta);
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(FilePreviewResult.PreviewType.UNSUPPORTED, result.getType());
    }
}
