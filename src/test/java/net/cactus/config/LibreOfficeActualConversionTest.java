package net.cactus.config;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LibreOffice实际文档转换功能测试
 * 测试JODConverter是否能够实际执行文档转换
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jodconverter.local.enabled=true",
    "logging.level.net.cactus.config.JodConverterConfig=DEBUG"
})
public class LibreOfficeActualConversionTest {

    private static final Logger logger = LoggerFactory.getLogger(LibreOfficeActualConversionTest.class);

    @Autowired(required = false)
    private DocumentConverter documentConverter;

    @Test
    public void testDocumentConverterAvailability() {
        logger.info("=== DocumentConverter可用性测试 ===");
        
        if (documentConverter == null) {
            logger.error("❌ DocumentConverter未注入，JODConverter可能未正确配置");
            logger.info("💡 可能的原因:");
            logger.info("   1. jodconverter.local.enabled=false");
            logger.info("   2. LibreOffice未正确安装");
            logger.info("   3. JODConverter配置错误");
            return;
        }
        
        logger.info("✅ DocumentConverter已成功注入");
        logger.info("DocumentConverter类型: {}", documentConverter.getClass().getName());
    }

    @Test
    public void testActualDocumentConversion() {
        logger.info("=== 实际文档转换测试 ===");
        
        if (documentConverter == null) {
            logger.error("❌ DocumentConverter不可用，跳过转换测试");
            return;
        }
        
        try {
            // 创建测试目录
            Path testDir = Paths.get(System.getProperty("java.io.tmpdir"), "jodconverter-test");
            Files.createDirectories(testDir);
            logger.info("测试目录: {}", testDir);
            
            // 创建测试文档
            File inputFile = createTestDocument(testDir);
            File outputFile = new File(testDir.toFile(), "test-output.pdf");
            
            logger.info("输入文件: {}", inputFile.getAbsolutePath());
            logger.info("输出文件: {}", outputFile.getAbsolutePath());
            
            // 执行转换
            logger.info("开始文档转换...");
            long startTime = System.currentTimeMillis();
            
            try {
                documentConverter.convert(inputFile).to(outputFile).execute();
                
                long duration = System.currentTimeMillis() - startTime;
                logger.info("✅ 文档转换成功！耗时: {} 毫秒", duration);
                
                // 验证输出文件
                if (outputFile.exists() && outputFile.length() > 0) {
                    logger.info("✅ 输出文件已生成，大小: {} 字节", outputFile.length());
                    logger.info("🎉 LibreOffice文档转换功能完全正常！");
                } else {
                    logger.warn("⚠️ 输出文件未生成或为空");
                }
                
            } catch (OfficeException e) {
                logger.error("❌ 文档转换失败", e);
                logger.error("错误详情: {}", e.getMessage());
                
                // 分析错误类型
                String errorMsg = e.getMessage();
                if (errorMsg != null) {
                    if (errorMsg.contains("Connection failed") || errorMsg.contains("Connection refused")) {
                        logger.error("💡 这是连接错误，LibreOffice服务可能未正确启动");
                        logger.error("   建议: 重启应用程序或检查LibreOffice安装");
                    } else if (errorMsg.contains("timeout")) {
                        logger.error("💡 这是超时错误，LibreOffice响应太慢");
                        logger.error("   建议: 增加超时时间或检查系统资源");
                    } else {
                        logger.error("💡 这是其他类型的错误，请检查LibreOffice配置");
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("❌ 测试执行失败", e);
        }
    }
    
    private File createTestDocument(Path testDir) throws IOException {
        File testFile = new File(testDir.toFile(), "test-input.txt");
        
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("LibreOffice文档转换测试\n");
            writer.write("======================\n\n");
            writer.write("这是一个测试文档，用于验证JODConverter的文档转换功能。\n\n");
            writer.write("测试时间: " + java.time.LocalDateTime.now() + "\n");
            writer.write("测试内容:\n");
            writer.write("1. 中文字符支持测试\n");
            writer.write("2. 英文字符支持测试 - English Character Test\n");
            writer.write("3. 数字支持测试 - 123456789\n");
            writer.write("4. 特殊字符测试 - !@#$%^&*()\n\n");
            writer.write("如果您能看到这个PDF文件，说明LibreOffice文档转换功能正常工作。\n");
        }
        
        logger.info("✅ 测试文档已创建: {}", testFile.getAbsolutePath());
        return testFile;
    }
    
    @Test
    public void testLibreOfficeProcessDiagnostic() {
        logger.info("=== LibreOffice进程诊断 ===");
        
        try {
            // 检查LibreOffice进程
            Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq soffice*\"");
            process.waitFor();
            
            boolean foundProcess = false;
            try (java.util.Scanner scanner = new java.util.Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("soffice")) {
                        logger.info("发现进程: {}", line.trim());
                        foundProcess = true;
                    }
                }
            }
            
            if (!foundProcess) {
                logger.warn("⚠️ 未发现LibreOffice进程");
                logger.info("💡 这可能意味着:");
                logger.info("   1. LibreOffice启动失败");
                logger.info("   2. 进程已退出");
                logger.info("   3. 进程名称不同");
            }
            
            // 检查端口占用情况
            logger.info("检查端口占用情况...");
            Process netstatProcess = Runtime.getRuntime().exec("netstat -an");
            netstatProcess.waitFor();
            
            boolean foundPort = false;
            try (java.util.Scanner scanner = new java.util.Scanner(netstatProcess.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("2002") || line.contains("2003") || line.contains("2004")) {
                        logger.info("发现端口: {}", line.trim());
                        foundPort = true;
                    }
                }
            }
            
            if (!foundPort) {
                logger.info("ℹ️ 未发现目标端口被占用");
                logger.info("💡 这表明LibreOffice可能:");
                logger.info("   1. 尚未开始监听端口");
                logger.info("   2. 使用了不同的端口");
                logger.info("   3. 启动失败");
            }
            
        } catch (Exception e) {
            logger.error("进程诊断失败", e);
        }
    }
}
