package net.cactus.controller;

import net.cactus.service.SystemInfoService;
import net.cactus.service.SystemMonitorService;
import net.cactus.utils.ResultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 系统信息API控制器
 * 提供系统信息相关的API接口
 */
@RestController
@RequestMapping("/api/system")
public class SystemInfoController {

    @Autowired
    private SystemInfoService systemInfoService;

    @Autowired
    private SystemMonitorService systemMonitorService;

    /**
     * 获取系统信息
     * @return 系统信息响应
     */
    @GetMapping("/info")
    public ResultMessage getSystemInfo() {
        try {
            Map<String, Object> systemInfo = systemInfoService.getSystemInfo();
            return ResultMessage.newSuccessMessage(systemInfo);
        } catch (Exception e) {
            return ResultMessage.newErrorMessage("获取系统信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取实时监控数据
     * @return 实时监控数据响应
     */
    @GetMapping("/monitor")
    public ResultMessage getMonitorData() {
        try {
            Map<String, Object> monitorData = systemMonitorService.getMonitorData();
            return ResultMessage.newSuccessMessage(monitorData);
        } catch (Exception e) {
            return ResultMessage.newErrorMessage("获取监控数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统状态概览
     * @return 系统状态概览响应
     */
    @GetMapping("/overview")
    public ResultMessage getSystemOverview() {
        try {
            Map<String, Object> overview = systemMonitorService.getSystemOverview();
            return ResultMessage.newSuccessMessage(overview);
        } catch (Exception e) {
            return ResultMessage.newErrorMessage("获取系统概览失败: " + e.getMessage());
        }
    }
}
