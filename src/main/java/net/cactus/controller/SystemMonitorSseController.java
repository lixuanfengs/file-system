package net.cactus.controller;

import net.cactus.service.OshiSystemMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

/**
 * 系统监控SSE控制器
 * 提供Server-Sent Events实时推送系统监控数据
 */
@RestController
@RequestMapping("/api/system/monitor")
public class SystemMonitorSseController {

    @Autowired
    private OshiSystemMonitorService oshiSystemMonitorService;

    /**
     * 实时系统监控数据流
     * 每5秒推送一次完整的系统监控数据，添加背压处理
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getSystemMonitorStream() {
        return Flux.interval(Duration.ofSeconds(5))
                .onBackpressureDrop()
                .map(sequence -> {
                    try {
                        Map<String, Object> data = oshiSystemMonitorService.getCompleteSystemData();
                        data.put("sequence", sequence);
                        return data;
                    } catch (Exception e) {
                        // 如果获取数据失败，返回错误信息
                        Map<String, Object> errorMap = Map.of(
                            "error", true,
                            "message", "获取系统监控数据失败: " + e.getMessage(),
                            "timestamp", System.currentTimeMillis(),
                            "sequence", sequence
                        );
                        return errorMap;
                    }
                })
                .onErrorResume(throwable -> {
                    // 处理流中的错误
                    Map<String, Object> errorMap = Map.of(
                        "error", true,
                        "message", "系统监控流发生错误: " + throwable.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    );
                    return Flux.just(errorMap);
                });
    }

    /**
     * CPU监控数据流
     * 每2秒推送一次CPU使用率数据，添加背压处理
     */
    @GetMapping(value = "/cpu/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getCpuMonitorStream() {
        return Flux.interval(Duration.ofSeconds(2))
                .onBackpressureDrop()  // 处理背压，丢弃过多的数据
                .map(sequence -> {
                    try {
                        Map<String, Object> systemData = oshiSystemMonitorService.getCompleteSystemData();
                        Map<String, Object> cpuData = (Map<String, Object>) systemData.get("cpu");
                        cpuData.put("timestamp", systemData.get("timestamp"));
                        cpuData.put("sequence", sequence);
                        return cpuData;
                    } catch (Exception e) {
                        Map<String, Object> errorMap = Map.of(
                            "error", true,
                            "message", "获取CPU监控数据失败: " + e.getMessage(),
                            "timestamp", System.currentTimeMillis(),
                            "sequence", sequence
                        );
                        return errorMap;
                    }
                })
                .onErrorResume(throwable -> {
                    Map<String, Object> errorMap = Map.of(
                        "error", true,
                        "message", "CPU监控流发生错误: " + throwable.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    );
                    return Flux.just(errorMap);
                });
    }

    /**
     * 内存监控数据流
     * 每3秒推送一次内存使用数据，添加背压处理
     */
    @GetMapping(value = "/memory/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getMemoryMonitorStream() {
        return Flux.interval(Duration.ofSeconds(3))
                .onBackpressureDrop()
                .map(sequence -> {
                    try {
                        Map<String, Object> systemData = oshiSystemMonitorService.getCompleteSystemData();
                        Map<String, Object> memoryData = (Map<String, Object>) systemData.get("memory");
                        memoryData.put("timestamp", systemData.get("timestamp"));
                        memoryData.put("sequence", sequence);
                        return memoryData;
                    } catch (Exception e) {
                        Map<String, Object> errorMap = Map.of(
                            "error", true,
                            "message", "获取内存监控数据失败: " + e.getMessage(),
                            "timestamp", System.currentTimeMillis(),
                            "sequence", sequence
                        );
                        return errorMap;
                    }
                })
                .onErrorResume(throwable -> {
                    Map<String, Object> errorMap = Map.of(
                        "error", true,
                        "message", "内存监控流发生错误: " + throwable.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    );
                    return Flux.just(errorMap);
                });
    }

    /**
     * 磁盘I/O监控数据流
     * 每5秒推送一次磁盘I/O数据，添加背压处理
     */
    @GetMapping(value = "/disk/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getDiskMonitorStream() {
        return Flux.interval(Duration.ofSeconds(5))
                .onBackpressureDrop()
                .map(sequence -> {
                    try {
                        Map<String, Object> systemData = oshiSystemMonitorService.getCompleteSystemData();
                        Map<String, Object> diskData = (Map<String, Object>) systemData.get("disk");
                        diskData.put("timestamp", systemData.get("timestamp"));
                        diskData.put("sequence", sequence);
                        return diskData;
                    } catch (Exception e) {
                        Map<String, Object> errorMap = Map.of(
                            "error", true,
                            "message", "获取磁盘监控数据失败: " + e.getMessage(),
                            "timestamp", System.currentTimeMillis(),
                            "sequence", sequence
                        );
                        return errorMap;
                    }
                })
                .onErrorResume(throwable -> {
                    Map<String, Object> errorMap = Map.of(
                        "error", true,
                        "message", "磁盘监控流发生错误: " + throwable.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    );
                    return Flux.just(errorMap);
                });
    }

    /**
     * 网络I/O监控数据流
     * 每5秒推送一次网络I/O数据，添加背压处理
     */
    @GetMapping(value = "/network/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getNetworkMonitorStream() {
        return Flux.interval(Duration.ofSeconds(5))
                .onBackpressureDrop()
                .map(sequence -> {
                    try {
                        Map<String, Object> systemData = oshiSystemMonitorService.getCompleteSystemData();
                        Map<String, Object> networkData = (Map<String, Object>) systemData.get("network");
                        networkData.put("timestamp", systemData.get("timestamp"));
                        networkData.put("sequence", sequence);
                        return networkData;
                    } catch (Exception e) {
                        Map<String, Object> errorMap = Map.of(
                            "error", true,
                            "message", "获取网络监控数据失败: " + e.getMessage(),
                            "timestamp", System.currentTimeMillis(),
                            "sequence", sequence
                        );
                        return errorMap;
                    }
                })
                .onErrorResume(throwable -> {
                    Map<String, Object> errorMap = Map.of(
                        "error", true,
                        "message", "网络监控流发生错误: " + throwable.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    );
                    return Flux.just(errorMap);
                });
    }



    /**
     * 获取一次性的完整系统信息（非流式）
     */
    @GetMapping("/snapshot")
    public Map<String, Object> getSystemSnapshot() {
        try {
            return oshiSystemMonitorService.getCompleteSystemData();
        } catch (Exception e) {
            Map<String, Object> errorMap = Map.of(
                "error", true,
                "message", "获取系统快照失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
            return errorMap;
        }
    }
}
