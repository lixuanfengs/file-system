package net.cactus.service;

import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OSProcess;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * OSHI系统监控服务
 * 使用OSHI库获取详细的系统硬件和操作系统信息
 */
@Service
public class OshiSystemMonitorService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final OperatingSystem operatingSystem;

    // 缓存上一次的网络和磁盘统计数据，用于计算速率
    private Map<String, Long> previousNetworkStats = new HashMap<>();
    private Map<String, Long> previousDiskStats = new HashMap<>();
    private long lastNetworkUpdateTime = System.currentTimeMillis();
    private long lastDiskUpdateTime = System.currentTimeMillis();

    public OshiSystemMonitorService() {
        this.systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.operatingSystem = systemInfo.getOperatingSystem();
    }

    /**
     * 获取完整的系统监控数据
     */
    public Map<String, Object> getCompleteSystemData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("timestamp", System.currentTimeMillis());
        data.put("currentTime", LocalDateTime.now().format(FORMATTER));
        
        // CPU信息
        data.put("cpu", getCpuInfo());
        
        // 内存信息
        data.put("memory", getMemoryInfo());
        
        // 磁盘信息
        data.put("disk", getDiskInfo());
        
        // 网络信息
        data.put("network", getNetworkInfo());

        // 操作系统信息
        data.put("os", getOperatingSystemInfo());
        
        return data;
    }

    /**
     * 获取CPU信息
     */
    private Map<String, Object> getCpuInfo() {
        Map<String, Object> cpuData = new HashMap<>();
        
        CentralProcessor processor = hardware.getProcessor();
        
        // CPU基本信息
        cpuData.put("name", processor.getProcessorIdentifier().getName());
        cpuData.put("vendor", processor.getProcessorIdentifier().getVendor());
        cpuData.put("family", processor.getProcessorIdentifier().getFamily());
        cpuData.put("model", processor.getProcessorIdentifier().getModel());
        cpuData.put("stepping", processor.getProcessorIdentifier().getStepping());
        cpuData.put("physicalCores", processor.getPhysicalProcessorCount());
        cpuData.put("logicalCores", processor.getLogicalProcessorCount());
        cpuData.put("maxFreq", formatFrequency(processor.getMaxFreq()));
        
        // CPU使用率
        double[] cpuLoad = processor.getProcessorCpuLoad(1000);
        double totalCpuUsage = Arrays.stream(cpuLoad).average().orElse(0.0) * 100;
        cpuData.put("usage", Math.round(totalCpuUsage * 100.0) / 100.0);
        
        // 各核心使用率
        List<Double> coreUsages = new ArrayList<>();
        for (double load : cpuLoad) {
            coreUsages.add(Math.round(load * 100 * 100.0) / 100.0);
        }
        cpuData.put("coreUsages", coreUsages);
        
        // 系统和用户CPU时间
        long[] systemCpuLoadTicks = processor.getSystemCpuLoadTicks();
        cpuData.put("systemLoad", processor.getSystemCpuLoadBetweenTicks(systemCpuLoadTicks) * 100);
        
        return cpuData;
    }

    /**
     * 获取内存信息
     */
    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> memoryData = new HashMap<>();
        
        GlobalMemory memory = hardware.getMemory();
        
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        
        memoryData.put("total", totalMemory);
        memoryData.put("used", usedMemory);
        memoryData.put("available", availableMemory);
        memoryData.put("usagePercent", Math.round((double) usedMemory / totalMemory * 100 * 100.0) / 100.0);
        
        // 格式化显示
        memoryData.put("totalFormatted", formatBytes(totalMemory));
        memoryData.put("usedFormatted", formatBytes(usedMemory));
        memoryData.put("availableFormatted", formatBytes(availableMemory));
        
        // 虚拟内存信息
        VirtualMemory virtualMemory = memory.getVirtualMemory();
        Map<String, Object> virtualMemoryData = new HashMap<>();
        virtualMemoryData.put("swapTotal", virtualMemory.getSwapTotal());
        virtualMemoryData.put("swapUsed", virtualMemory.getSwapUsed());
        virtualMemoryData.put("swapTotalFormatted", formatBytes(virtualMemory.getSwapTotal()));
        virtualMemoryData.put("swapUsedFormatted", formatBytes(virtualMemory.getSwapUsed()));
        
        memoryData.put("virtual", virtualMemoryData);
        
        return memoryData;
    }

    /**
     * 获取磁盘信息
     */
    private Map<String, Object> getDiskInfo() {
        Map<String, Object> diskData = new HashMap<>();

        // 获取文件系统信息（用于磁盘使用量）
        List<oshi.software.os.OSFileStore> fileStores = operatingSystem.getFileSystem().getFileStores();
        List<Map<String, Object>> disks = new ArrayList<>();

        long totalReads = 0;
        long totalWrites = 0;
        long totalReadBytes = 0;
        long totalWriteBytes = 0;
        long totalSpace = 0;
        long totalUsed = 0;
        long totalFree = 0;

        // 获取硬件磁盘信息（用于I/O统计）
        List<HWDiskStore> diskStores = hardware.getDiskStores();
        long currentTime = System.currentTimeMillis();
        double timeDelta = (currentTime - lastDiskUpdateTime) / 1000.0; // 转换为秒

        for (HWDiskStore disk : diskStores) {
            String diskName = disk.getName();
            long currentReadBytes = disk.getReadBytes();
            long currentWriteBytes = disk.getWriteBytes();

            totalReads += disk.getReads();
            totalWrites += disk.getWrites();
            totalReadBytes += currentReadBytes;
            totalWriteBytes += currentWriteBytes;

            // 计算实时读写速率
            if (previousDiskStats.containsKey(diskName + "_read") && timeDelta > 0) {
                long previousReadBytes = previousDiskStats.get(diskName + "_read");
                long previousWriteBytes = previousDiskStats.get(diskName + "_write");

                long readBytesPerSec = (long) ((currentReadBytes - previousReadBytes) / timeDelta);
                long writeBytesPerSec = (long) ((currentWriteBytes - previousWriteBytes) / timeDelta);

                // 存储实时速率（避免负值）
                previousDiskStats.put(diskName + "_readSpeed", Math.max(0, readBytesPerSec));
                previousDiskStats.put(diskName + "_writeSpeed", Math.max(0, writeBytesPerSec));
            }

            // 更新缓存数据
            previousDiskStats.put(diskName + "_read", currentReadBytes);
            previousDiskStats.put(diskName + "_write", currentWriteBytes);
        }

        lastDiskUpdateTime = currentTime;

        // 获取文件系统信息（用于空间使用情况）
        for (oshi.software.os.OSFileStore fileStore : fileStores) {
            if (fileStore.getTotalSpace() > 0) {
                Map<String, Object> diskInfo = new HashMap<>();

                long total = fileStore.getTotalSpace();
                long free = fileStore.getFreeSpace();
                long used = total - free;
                double usagePercent = total > 0 ? (double) used / total * 100 : 0;

                diskInfo.put("name", fileStore.getName());
                diskInfo.put("mount", fileStore.getMount());
                diskInfo.put("type", fileStore.getType());
                diskInfo.put("totalSpace", total);
                diskInfo.put("usedSpace", used);
                diskInfo.put("freeSpace", free);
                diskInfo.put("usagePercent", Math.round(usagePercent * 100.0) / 100.0);

                // 格式化显示
                diskInfo.put("totalSpaceFormatted", formatBytes(total));
                diskInfo.put("usedSpaceFormatted", formatBytes(used));
                diskInfo.put("freeSpaceFormatted", formatBytes(free));

                totalSpace += total;
                totalUsed += used;
                totalFree += free;

                disks.add(diskInfo);
            }
        }

        diskData.put("disks", disks);
        diskData.put("totalReads", totalReads);
        diskData.put("totalWrites", totalWrites);
        diskData.put("totalReadBytes", totalReadBytes);
        diskData.put("totalWriteBytes", totalWriteBytes);
        diskData.put("totalReadBytesFormatted", formatBytes(totalReadBytes));
        diskData.put("totalWriteBytesFormatted", formatBytes(totalWriteBytes));

        // 计算总体实时I/O速率
        long totalReadSpeed = 0;
        long totalWriteSpeed = 0;
        for (String key : previousDiskStats.keySet()) {
            if (key.endsWith("_readSpeed")) {
                totalReadSpeed += previousDiskStats.get(key);
            } else if (key.endsWith("_writeSpeed")) {
                totalWriteSpeed += previousDiskStats.get(key);
            }
        }

        diskData.put("totalReadSpeed", totalReadSpeed);
        diskData.put("totalWriteSpeed", totalWriteSpeed);
        diskData.put("totalReadSpeedFormatted", formatBytes(totalReadSpeed) + "/s");
        diskData.put("totalWriteSpeedFormatted", formatBytes(totalWriteSpeed) + "/s");

        // 总体磁盘空间信息
        diskData.put("totalSpace", totalSpace);
        diskData.put("totalUsed", totalUsed);
        diskData.put("totalFree", totalFree);
        diskData.put("totalSpaceFormatted", formatBytes(totalSpace));
        diskData.put("totalUsedFormatted", formatBytes(totalUsed));
        diskData.put("totalFreeFormatted", formatBytes(totalFree));
        diskData.put("totalUsagePercent", totalSpace > 0 ? Math.round((double) totalUsed / totalSpace * 100 * 100.0) / 100.0 : 0);

        return diskData;
    }

    /**
     * 获取网络信息
     */
    private Map<String, Object> getNetworkInfo() {
        Map<String, Object> networkData = new HashMap<>();

        List<NetworkIF> networkIFs = hardware.getNetworkIFs();
        List<Map<String, Object>> interfaces = new ArrayList<>();

        long totalBytesReceived = 0;
        long totalBytesSent = 0;
        long totalPacketsReceived = 0;
        long totalPacketsSent = 0;

        long currentTime = System.currentTimeMillis();
        double timeDelta = (currentTime - lastNetworkUpdateTime) / 1000.0; // 转换为秒

        long totalReceiveSpeed = 0;
        long totalSendSpeed = 0;

        for (NetworkIF networkIF : networkIFs) {
            if (networkIF.getBytesRecv() > 0 || networkIF.getBytesSent() > 0) {
                String interfaceName = networkIF.getName();
                long currentBytesReceived = networkIF.getBytesRecv();
                long currentBytesSent = networkIF.getBytesSent();

                Map<String, Object> interfaceInfo = new HashMap<>();
                interfaceInfo.put("name", interfaceName);
                interfaceInfo.put("displayName", networkIF.getDisplayName());
                interfaceInfo.put("macAddress", networkIF.getMacaddr());
                interfaceInfo.put("mtu", networkIF.getMTU());
                interfaceInfo.put("speed", networkIF.getSpeed());
                interfaceInfo.put("speedFormatted", formatBitsPerSecond(networkIF.getSpeed()));

                // 网络I/O统计
                interfaceInfo.put("bytesReceived", currentBytesReceived);
                interfaceInfo.put("bytesSent", currentBytesSent);
                interfaceInfo.put("packetsReceived", networkIF.getPacketsRecv());
                interfaceInfo.put("packetsSent", networkIF.getPacketsSent());
                interfaceInfo.put("bytesReceivedFormatted", formatBytes(currentBytesReceived));
                interfaceInfo.put("bytesSentFormatted", formatBytes(currentBytesSent));

                // 计算实时网络速率
                if (previousNetworkStats.containsKey(interfaceName + "_recv") && timeDelta > 0) {
                    long previousBytesReceived = previousNetworkStats.get(interfaceName + "_recv");
                    long previousBytesSent = previousNetworkStats.get(interfaceName + "_sent");

                    long receiveSpeed = (long) ((currentBytesReceived - previousBytesReceived) / timeDelta);
                    long sendSpeed = (long) ((currentBytesSent - previousBytesSent) / timeDelta);

                    // 存储实时速率（避免负值）
                    receiveSpeed = Math.max(0, receiveSpeed);
                    sendSpeed = Math.max(0, sendSpeed);

                    interfaceInfo.put("receiveSpeed", receiveSpeed);
                    interfaceInfo.put("sendSpeed", sendSpeed);
                    interfaceInfo.put("receiveSpeedFormatted", formatBytes(receiveSpeed) + "/s");
                    interfaceInfo.put("sendSpeedFormatted", formatBytes(sendSpeed) + "/s");

                    totalReceiveSpeed += receiveSpeed;
                    totalSendSpeed += sendSpeed;
                } else {
                    interfaceInfo.put("receiveSpeed", 0L);
                    interfaceInfo.put("sendSpeed", 0L);
                    interfaceInfo.put("receiveSpeedFormatted", "0 B/s");
                    interfaceInfo.put("sendSpeedFormatted", "0 B/s");
                }

                // 更新缓存数据
                previousNetworkStats.put(interfaceName + "_recv", currentBytesReceived);
                previousNetworkStats.put(interfaceName + "_sent", currentBytesSent);

                totalBytesReceived += currentBytesReceived;
                totalBytesSent += currentBytesSent;
                totalPacketsReceived += networkIF.getPacketsRecv();
                totalPacketsSent += networkIF.getPacketsSent();

                interfaces.add(interfaceInfo);
            }
        }

        lastNetworkUpdateTime = currentTime;

        networkData.put("interfaces", interfaces);
        networkData.put("totalBytesReceived", totalBytesReceived);
        networkData.put("totalBytesSent", totalBytesSent);
        networkData.put("totalPacketsReceived", totalPacketsReceived);
        networkData.put("totalPacketsSent", totalPacketsSent);
        networkData.put("totalBytesReceivedFormatted", formatBytes(totalBytesReceived));
        networkData.put("totalBytesSentFormatted", formatBytes(totalBytesSent));

        // 总体实时网络速率
        networkData.put("totalReceiveSpeed", totalReceiveSpeed);
        networkData.put("totalSendSpeed", totalSendSpeed);
        networkData.put("totalReceiveSpeedFormatted", formatBytes(totalReceiveSpeed) + "/s");
        networkData.put("totalSendSpeedFormatted", formatBytes(totalSendSpeed) + "/s");

        return networkData;
    }



    /**
     * 获取操作系统信息
     */
    private Map<String, Object> getOperatingSystemInfo() {
        Map<String, Object> osData = new HashMap<>();
        
        osData.put("family", operatingSystem.getFamily());
        osData.put("manufacturer", operatingSystem.getManufacturer());
        osData.put("version", operatingSystem.getVersionInfo().toString());
        osData.put("buildNumber", operatingSystem.getVersionInfo().getBuildNumber());
        osData.put("bitness", operatingSystem.getBitness());
        osData.put("processCount", operatingSystem.getProcessCount());
        osData.put("threadCount", operatingSystem.getThreadCount());
        
        // 系统启动时间
        long bootTime = operatingSystem.getSystemBootTime() * 1000;
        osData.put("bootTime", bootTime);
        osData.put("bootTimeFormatted", LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(bootTime), 
            java.time.ZoneId.systemDefault()
        ).format(FORMATTER));
        
        // 系统运行时间
        long uptime = System.currentTimeMillis() - bootTime;
        osData.put("uptime", uptime);
        osData.put("uptimeFormatted", formatUptime(uptime));
        
        return osData;
    }



    // 工具方法
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private String formatFrequency(long hz) {
        if (hz < 1000) return hz + " Hz";
        if (hz < 1000000) return String.format("%.1f KHz", hz / 1000.0);
        if (hz < 1000000000) return String.format("%.1f MHz", hz / 1000000.0);
        return String.format("%.1f GHz", hz / 1000000000.0);
    }

    private String formatBitsPerSecond(long bps) {
        if (bps < 1000) return bps + " bps";
        if (bps < 1000000) return String.format("%.1f Kbps", bps / 1000.0);
        if (bps < 1000000000) return String.format("%.1f Mbps", bps / 1000000.0);
        return String.format("%.1f Gbps", bps / 1000000000.0);
    }

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
}
