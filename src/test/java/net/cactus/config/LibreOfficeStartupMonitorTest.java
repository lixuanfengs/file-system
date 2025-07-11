package net.cactus.config;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LibreOffice启动过程监控测试
 * 实时监控LibreOffice从启动到端口可用的完整过程
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jodconverter.local.enabled=true",
    "logging.level.net.cactus.config.JodConverterConfig=DEBUG"
})
public class LibreOfficeStartupMonitorTest {

    private static final Logger logger = LoggerFactory.getLogger(LibreOfficeStartupMonitorTest.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Test
    public void monitorLibreOfficeStartup() {
        logger.info("=== LibreOffice启动过程实时监控 ===");
        
        int[] ports = {2002, 2003, 2004};
        int maxMonitorTime = 120; // 监控2分钟
        int checkInterval = 5; // 每5秒检查一次
        
        logger.info("开始监控时间: {}", LocalDateTime.now().format(TIME_FORMAT));
        logger.info("监控端口: {}", java.util.Arrays.toString(ports));
        logger.info("最大监控时间: {} 秒", maxMonitorTime);
        logger.info("检查间隔: {} 秒", checkInterval);
        logger.info("----------------------------------------");
        
        boolean allPortsReady = false;
        int elapsedTime = 0;
        
        while (elapsedTime < maxMonitorTime && !allPortsReady) {
            try {
                Thread.sleep(checkInterval * 1000);
                elapsedTime += checkInterval;
                
                String currentTime = LocalDateTime.now().format(TIME_FORMAT);
                logger.info("[{}] 第 {} 秒检查:", currentTime, elapsedTime);
                
                // 检查进程状态
                int processCount = checkProcessCount();
                logger.info("  📊 LibreOffice进程数量: {}", processCount);
                
                // 检查端口状态
                int availablePorts = 0;
                for (int port : ports) {
                    boolean isAvailable = testPortQuick(port);
                    if (isAvailable) {
                        logger.info("  ✅ 端口 {} 可用", port);
                        availablePorts++;
                    } else {
                        logger.info("  ⏳ 端口 {} 尚未就绪", port);
                    }
                }
                
                logger.info("  📈 可用端口: {}/{}", availablePorts, ports.length);
                
                if (availablePorts == ports.length) {
                    allPortsReady = true;
                    logger.info("🎉 所有端口已就绪！总耗时: {} 秒", elapsedTime);
                } else if (availablePorts > 0) {
                    logger.info("⚡ 部分端口已就绪，继续等待...");
                } else {
                    logger.info("⏳ 端口尚未就绪，继续监控...");
                }
                
                logger.info("----------------------------------------");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("监控被中断");
                break;
            }
        }
        
        if (allPortsReady) {
            logger.info("✅ 监控完成：LibreOffice在 {} 秒内完全启动", elapsedTime);
        } else {
            logger.warn("⚠️ 监控超时：{} 秒内LibreOffice未完全启动", maxMonitorTime);
            logger.info("💡 这可能表明:");
            logger.info("   1. LibreOffice需要更长时间初始化");
            logger.info("   2. 系统资源不足");
            logger.info("   3. 端口被其他程序占用");
            logger.info("   4. LibreOffice配置问题");
        }
    }
    
    private boolean testPortQuick(int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    private int checkProcessCount() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq soffice*\"");
                process.waitFor();
                
                int count = 0;
                try (java.util.Scanner scanner = new java.util.Scanner(process.getInputStream())) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.contains("soffice.exe") || line.contains("soffice.bin")) {
                            count++;
                        }
                    }
                }
                return count;
            }
        } catch (Exception e) {
            logger.debug("检查进程数量失败", e);
        }
        return 0;
    }
    
    @Test
    public void testLibreOfficeReadiness() {
        logger.info("=== LibreOffice就绪状态测试 ===");
        
        // 等待足够长的时间确保LibreOffice完全启动
        int waitTime = 60; // 等待60秒
        logger.info("等待 {} 秒让LibreOffice完全启动...", waitTime);
        
        try {
            Thread.sleep(waitTime * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        
        int[] ports = {2002, 2003, 2004};
        int availablePorts = 0;
        
        logger.info("检查端口可用性:");
        for (int port : ports) {
            boolean isAvailable = testPortWithRetry(port, 3, 2000);
            if (isAvailable) {
                logger.info("✅ 端口 {} 可用", port);
                availablePorts++;
            } else {
                logger.warn("❌ 端口 {} 不可用", port);
            }
        }
        
        logger.info("结果: {}/{} 端口可用", availablePorts, ports.length);
        
        if (availablePorts > 0) {
            logger.info("🎉 LibreOffice部分或完全就绪，文档转换功能应该可用");
        } else {
            logger.warn("⚠️ 所有端口均不可用，但这可能仍然是正常现象");
            logger.info("💡 建议:");
            logger.info("   1. 检查LibreOffice是否正确安装");
            logger.info("   2. 尝试重启应用程序");
            logger.info("   3. 检查系统资源使用情况");
            logger.info("   4. 验证端口是否被其他程序占用");
        }
    }
    
    private boolean testPortWithRetry(int port, int maxRetries, int delayMs) {
        for (int i = 0; i < maxRetries; i++) {
            if (testPortQuick(port)) {
                return true;
            }
            if (i < maxRetries - 1) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return false;
    }
}
