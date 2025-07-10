package net.cactus.service;

import net.cactus.FileServerApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统信息服务
 * 提供应用启动时间、运行时间等系统信息
 */
@Service
public class SystemInfoService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 获取系统信息
     * @return 系统信息Map
     */
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        // 获取启动时间
        long startTime = FileServerApplication.APPLICATION_START_TIME;
        LocalDateTime startDateTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(startTime), 
            ZoneId.systemDefault()
        );
        
        // 计算运行时间
        long currentTime = System.currentTimeMillis();
        long uptimeMs = currentTime - startTime;
        String uptime = formatUptime(uptimeMs);
        
        // 获取JVM运行时信息
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        
        systemInfo.put("startTime", startDateTime.format(FORMATTER));
        systemInfo.put("uptime", uptime);
        systemInfo.put("uptimeMs", uptimeMs);
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("javaVendor", System.getProperty("java.vendor"));
        systemInfo.put("springBootVersion", SpringBootVersion.getVersion());
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("osArch", System.getProperty("os.arch"));
        systemInfo.put("jvmName", runtimeMXBean.getVmName());
        systemInfo.put("jvmVersion", runtimeMXBean.getVmVersion());
        systemInfo.put("jvmVendor", runtimeMXBean.getVmVendor());
        
        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        systemInfo.put("totalMemory", formatBytes(totalMemory));
        systemInfo.put("usedMemory", formatBytes(usedMemory));
        systemInfo.put("freeMemory", formatBytes(freeMemory));
        systemInfo.put("maxMemory", formatBytes(maxMemory));
        systemInfo.put("memoryUsagePercent", (int) ((double) usedMemory / totalMemory * 100));
        
        return systemInfo;
    }
    
    /**
     * 格式化运行时间
     * @param uptimeMs 运行时间（毫秒）
     * @return 格式化的运行时间字符串
     */
    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        
        if (days > 0) {
            return String.format("%d天 %d小时 %d分钟", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%d小时 %d分钟", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d分钟 %d秒", minutes, seconds);
        } else {
            return String.format("%d秒", seconds);
        }
    }
    
    /**
     * 格式化字节数
     * @param bytes 字节数
     * @return 格式化的字节数字符串
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
