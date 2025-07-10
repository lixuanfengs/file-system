package net.cactus.service;

import net.cactus.FileServerApplication;
import net.cactus.pojo.User;
import net.cactus.service.FileMetaService;
import net.cactus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统监控服务
 * 提供实时系统监控数据
 */
@Service
public class SystemMonitorService {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private FileMetaService fileMetaService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取完整的监控数据
     * @return 监控数据Map
     */
    public Map<String, Object> getMonitorData() {
        Map<String, Object> data = new HashMap<>();
        
        // 基础系统信息
        data.putAll(getBasicSystemInfo());
        
        // 内存信息
        data.putAll(getMemoryInfo());
        
        // 运行时信息
        data.putAll(getRuntimeInfo());
        
        // 业务统计信息
        data.putAll(getBusinessStats());
        
        // 当前时间
        data.put("currentTime", LocalDateTime.now().format(FORMATTER));
        data.put("timestamp", System.currentTimeMillis());
        
        return data;
    }
    
    /**
     * 获取系统概览数据
     * @return 系统概览Map
     */
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // 服务状态
            overview.put("serviceStatus", "运行中");
            overview.put("serviceStatusCode", 1);
            
            // 用户统计
            List<User> allUsers = userService.findAllUsers();
            int totalUsers = allUsers != null ? allUsers.size() : 0;
            long activeUsers = allUsers != null ? allUsers.stream()
                    .filter(u -> u != null && u.getStatus() != null && u.getStatus() == 1)
                    .count() : 0;
            
            overview.put("totalUsers", totalUsers);
            overview.put("activeUsers", activeUsers);
            overview.put("inactiveUsers", totalUsers - activeUsers);
            
            // 文件统计
            int totalFiles = fileMetaService.getTotalCount();
            overview.put("totalFiles", totalFiles);
            
            // 内存使用
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            int memoryUsagePercent = (int) ((double) usedMemory / totalMemory * 100);
            
            overview.put("memoryUsagePercent", memoryUsagePercent);
            overview.put("usedMemory", formatBytes(usedMemory));
            overview.put("totalMemory", formatBytes(totalMemory));
            
        } catch (Exception e) {
            // 如果获取数据失败，返回默认值
            overview.put("serviceStatus", "异常");
            overview.put("serviceStatusCode", 0);
            overview.put("totalUsers", 0);
            overview.put("activeUsers", 0);
            overview.put("inactiveUsers", 0);
            overview.put("totalFiles", 0);
            overview.put("memoryUsagePercent", 0);
            overview.put("usedMemory", "0 MB");
            overview.put("totalMemory", "0 MB");
            overview.put("error", e.getMessage());
        }
        
        return overview;
    }
    
    /**
     * 获取基础系统信息
     */
    private Map<String, Object> getBasicSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("osArch", System.getProperty("os.arch"));
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("javaVendor", System.getProperty("java.vendor"));
        
        return info;
    }
    
    /**
     * 获取内存信息
     */
    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> info = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        info.put("totalMemory", totalMemory);
        info.put("freeMemory", freeMemory);
        info.put("usedMemory", usedMemory);
        info.put("maxMemory", maxMemory);
        info.put("totalMemoryFormatted", formatBytes(totalMemory));
        info.put("freeMemoryFormatted", formatBytes(freeMemory));
        info.put("usedMemoryFormatted", formatBytes(usedMemory));
        info.put("maxMemoryFormatted", formatBytes(maxMemory));
        info.put("memoryUsagePercent", (int) ((double) usedMemory / totalMemory * 100));
        
        // 堆内存信息
        long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryBean.getHeapMemoryUsage().getMax();
        info.put("heapUsed", formatBytes(heapUsed));
        info.put("heapMax", formatBytes(heapMax));
        info.put("heapUsagePercent", heapMax > 0 ? (int) ((double) heapUsed / heapMax * 100) : 0);
        
        return info;
    }
    
    /**
     * 获取运行时信息
     */
    private Map<String, Object> getRuntimeInfo() {
        Map<String, Object> info = new HashMap<>();
        
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        
        // 启动时间和运行时间
        long startTime = FileServerApplication.APPLICATION_START_TIME;
        long currentTime = System.currentTimeMillis();
        long uptimeMs = currentTime - startTime;
        
        LocalDateTime startDateTime = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(startTime), 
            ZoneId.systemDefault()
        );
        
        info.put("startTime", startDateTime.format(FORMATTER));
        info.put("uptime", formatUptime(uptimeMs));
        info.put("uptimeMs", uptimeMs);
        
        // JVM信息
        info.put("jvmName", runtimeBean.getVmName());
        info.put("jvmVersion", runtimeBean.getVmVersion());
        info.put("jvmVendor", runtimeBean.getVmVendor());
        
        // 系统负载（如果可用）
        double systemLoad = osBean.getSystemLoadAverage();
        info.put("systemLoad", systemLoad >= 0 ? String.format("%.2f", systemLoad) : "不可用");
        
        // 可用处理器数
        info.put("availableProcessors", osBean.getAvailableProcessors());
        
        return info;
    }
    
    /**
     * 获取业务统计信息
     */
    private Map<String, Object> getBusinessStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 用户统计
            List<User> allUsers = userService.findAllUsers();
            int totalUsers = allUsers != null ? allUsers.size() : 0;
            long activeUsers = allUsers != null ? allUsers.stream()
                    .filter(u -> u != null && u.getStatus() != null && u.getStatus() == 1)
                    .count() : 0;

            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("inactiveUsers", totalUsers - activeUsers);

            // 文件统计
            int totalFiles = fileMetaService.getTotalCount();
            stats.put("totalFiles", totalFiles);

        } catch (Exception e) {
            stats.put("totalUsers", 0);
            stats.put("activeUsers", 0);
            stats.put("inactiveUsers", 0);
            stats.put("totalFiles", 0);
            stats.put("statsError", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 格式化运行时间
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
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
