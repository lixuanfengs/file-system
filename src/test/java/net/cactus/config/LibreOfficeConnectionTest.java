package net.cactus.config;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * LibreOffice连接测试类
 * 用于测试和诊断LibreOffice端口连接问题
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jodconverter.local.enabled=true",
    "logging.level.net.cactus.config.JodConverterConfig=DEBUG"
})
public class LibreOfficeConnectionTest {

    private static final Logger logger = LoggerFactory.getLogger(LibreOfficeConnectionTest.class);

    @Test
    public void testLibreOfficeConnectionWithRetry() {
        logger.info("=== LibreOffice连接测试（带重试机制） ===");
        
        int[] ports = {2002, 2003, 2004};
        int maxRetries = 5;
        int baseDelay = 2000; // 2秒基础延迟
        
        for (int retry = 1; retry <= maxRetries; retry++) {
            logger.info("第 {}/{} 次连接测试", retry, maxRetries);
            
            int availablePorts = 0;
            
            for (int port : ports) {
                if (testPortConnection(port, 3000)) { // 3秒超时
                    availablePorts++;
                }
            }
            
            if (availablePorts > 0) {
                logger.info("✅ 连接成功！{}/{} 个端口可用", availablePorts, ports.length);
                return;
            }
            
            if (retry < maxRetries) {
                int delay = baseDelay * retry; // 递增延迟
                logger.info("⏳ 所有端口暂不可用，等待 {} 秒后重试...", delay / 1000);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        logger.warn("❌ 经过 {} 次重试，所有端口仍不可用", maxRetries);
        logger.info("💡 这可能是正常现象，LibreOffice可能需要更多时间初始化");
        
        // 检查进程状态
        checkLibreOfficeProcess();
    }
    
    private boolean testPortConnection(int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), timeoutMs);
            logger.info("✅ 端口 {} 连接成功", port);
            return true;
        } catch (IOException e) {
            logger.debug("端口 {} 连接失败: {}", port, e.getMessage());
            return false;
        }
    }
    
    private void checkLibreOfficeProcess() {
        logger.info("=== 检查LibreOffice进程状态 ===");
        
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
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
                        logger.info("💡 可能的原因:");
                        logger.info("   1. LibreOffice尚未启动");
                        logger.info("   2. 进程以不同名称运行");
                        logger.info("   3. 启动失败");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("检查进程失败", e);
        }
    }
    
    @Test
    public void testLibreOfficeInstallation() {
        logger.info("=== LibreOffice安装检测 ===");
        
        String[] possiblePaths = {
            "C:\\Program Files\\LibreOffice",
            "C:\\Program Files (x86)\\LibreOffice"
        };
        
        boolean found = false;
        for (String path : possiblePaths) {
            java.io.File officeDir = new java.io.File(path);
            if (officeDir.exists() && officeDir.isDirectory()) {
                java.io.File programDir = new java.io.File(officeDir, "program");
                java.io.File sofficeExe = new java.io.File(programDir, "soffice.exe");
                
                if (sofficeExe.exists()) {
                    logger.info("✅ 找到LibreOffice安装: {}", path);
                    logger.info("   可执行文件: {}", sofficeExe.getAbsolutePath());
                    found = true;
                    break;
                }
            }
        }
        
        if (!found) {
            logger.error("❌ 未找到LibreOffice安装");
            logger.info("💡 请确保LibreOffice已正确安装到标准路径");
        }
    }
    
    @Test
    public void testPortAvailability() {
        logger.info("=== 端口可用性测试 ===");
        
        int[] ports = {2002, 2003, 2004};
        
        for (int port : ports) {
            try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(port)) {
                logger.info("✅ 端口 {} 可用（未被占用）", port);
            } catch (IOException e) {
                if (e.getMessage().contains("Address already in use")) {
                    logger.info("⚠️ 端口 {} 已被占用（可能是LibreOffice正在使用）", port);
                } else {
                    logger.warn("❌ 端口 {} 测试失败: {}", port, e.getMessage());
                }
            }
        }
    }
}
