package net.cactus.config;

import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JODConverter诊断测试类
 * 用于验证LibreOffice和JODConverter的配置和连接状态
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jodconverter.local.enabled=true",
    "logging.level.net.cactus.config.JodConverterConfig=DEBUG"
})
public class JodConverterDiagnosticTest {

    private static final Logger logger = LoggerFactory.getLogger(JodConverterDiagnosticTest.class);

    @Test
    public void testLibreOfficeInstallation() {
        logger.info("=== LibreOffice安装检测测试 ===");
        
        // 检测LibreOffice安装路径
        String[] possiblePaths = {
            "C:\\Program Files\\LibreOffice",
            "C:\\Program Files (x86)\\LibreOffice",
            "D:\\Program Files\\LibreOffice",
            "D:\\Program Files (x86)\\LibreOffice"
        };

        boolean found = false;
        for (String path : possiblePaths) {
            File officeDir = new File(path);
            if (officeDir.exists() && officeDir.isDirectory()) {
                File programDir = new File(officeDir, "program");
                File sofficeExe = new File(programDir, "soffice.exe");
                
                if (sofficeExe.exists()) {
                    logger.info("✅ 找到LibreOffice安装: {}", path);
                    logger.info("   可执行文件: {}", sofficeExe.getAbsolutePath());
                    found = true;
                    
                    // 尝试获取版本信息
                    try {
                        Process process = Runtime.getRuntime().exec(
                            new String[]{sofficeExe.getAbsolutePath(), "--version"}
                        );
                        process.waitFor();
                        logger.info("   版本检查完成，退出码: {}", process.exitValue());
                    } catch (Exception e) {
                        logger.warn("   版本检查失败: {}", e.getMessage());
                    }
                    break;
                }
            }
        }

        if (!found) {
            logger.error("❌ 未找到LibreOffice安装");
            logger.error("请运行 install-libreoffice.bat 安装LibreOffice");
        }
    }

    @Test
    public void testLibreOfficeProcesses() {
        logger.info("=== LibreOffice进程检测测试 ===");
        
        try {
            Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq soffice*\"");
            process.waitFor();
            
            try (java.util.Scanner scanner = new java.util.Scanner(process.getInputStream())) {
                boolean foundProcess = false;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("soffice.exe") || line.contains("soffice.bin")) {
                        logger.info("✅ 发现LibreOffice进程: {}", line.trim());
                        foundProcess = true;
                    }
                }
                
                if (!foundProcess) {
                    logger.warn("⚠️ 未发现LibreOffice进程");
                }
            }
            
        } catch (Exception e) {
            logger.error("检查LibreOffice进程失败", e);
        }
    }

    @Test
    public void testPortConnectivity() {
        logger.info("=== 端口连接测试 ===");
        
        int[] ports = {2002, 2003, 2004};
        int availablePorts = 0;
        
        for (int port : ports) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("127.0.0.1", port), 2000);
                logger.info("✅ 端口 {} 可连接", port);
                availablePorts++;
            } catch (IOException e) {
                logger.warn("❌ 端口 {} 不可连接: {}", port, e.getMessage());
            }
        }
        
        if (availablePorts > 0) {
            logger.info("✅ 总计 {}/{} 个端口可用", availablePorts, ports.length);
        } else {
            logger.error("❌ 所有端口均不可用，LibreOffice可能未正确启动");
        }
    }

    @Test
    public void testJodConverterBasic() {
        logger.info("=== JODConverter基础测试 ===");
        
        try {
            // 创建简单的Office Manager配置
            LocalOfficeManager officeManager = LocalOfficeManager.builder()
                .officeHome(detectOfficeHome())
                .portNumbers(2002, 2003, 2004)
                .processTimeout(60000L)
                .processRetryInterval(2000L)
                .build();

            logger.info("正在启动Office Manager...");
            officeManager.start();
            logger.info("✅ Office Manager启动成功");

            // 创建转换器
            LocalConverter converter = LocalConverter.builder()
                .officeManager(officeManager)
                .build();

            logger.info("✅ Document Converter创建成功");

            // 清理
            officeManager.stop();
            logger.info("✅ Office Manager已停止");

        } catch (Exception e) {
            logger.error("❌ JODConverter基础测试失败", e);
        }
    }

    @Test
    public void testDocumentConversion() {
        logger.info("=== 文档转换测试 ===");
        
        try {
            // 创建测试文档
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "jodconverter-test");
            Files.createDirectories(tempDir);
            
            Path testDoc = tempDir.resolve("test.txt");
            Files.write(testDoc, "Hello, JODConverter!\nThis is a test document.".getBytes());
            
            Path outputPdf = tempDir.resolve("test.pdf");
            
            // 配置Office Manager
            LocalOfficeManager officeManager = LocalOfficeManager.builder()
                .officeHome(detectOfficeHome())
                .portNumbers(2002, 2003, 2004)
                .processTimeout(60000L)
                .processRetryInterval(2000L)
                .build();

            officeManager.start();
            
            // 创建转换器并执行转换
            LocalConverter converter = LocalConverter.builder()
                .officeManager(officeManager)
                .build();

            logger.info("开始转换文档: {} -> {}", testDoc.getFileName(), outputPdf.getFileName());
            converter.convert(testDoc.toFile()).to(outputPdf.toFile()).execute();
            
            if (Files.exists(outputPdf) && Files.size(outputPdf) > 0) {
                logger.info("✅ 文档转换成功，输出文件大小: {} bytes", Files.size(outputPdf));
            } else {
                logger.error("❌ 文档转换失败，输出文件不存在或为空");
            }
            
            // 清理
            officeManager.stop();
            Files.deleteIfExists(testDoc);
            Files.deleteIfExists(outputPdf);
            
        } catch (Exception e) {
            logger.error("❌ 文档转换测试失败", e);
        }
    }

    private String detectOfficeHome() {
        String[] possiblePaths = {
            "C:\\Program Files\\LibreOffice",
            "C:\\Program Files (x86)\\LibreOffice",
            "D:\\Program Files\\LibreOffice",
            "D:\\Program Files (x86)\\LibreOffice"
        };

        for (String path : possiblePaths) {
            File officeDir = new File(path);
            if (officeDir.exists() && officeDir.isDirectory()) {
                File programDir = new File(officeDir, "program");
                File sofficeExe = new File(programDir, "soffice.exe");
                if (sofficeExe.exists()) {
                    return path;
                }
            }
        }
        
        return null;
    }
}
