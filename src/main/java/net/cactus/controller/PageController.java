package net.cactus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.cactus.pojo.FileMeta;
import net.cactus.pojo.User;
import net.cactus.service.FileMetaService;
import net.cactus.service.TableDataService;
import net.cactus.service.UserService;
import net.cactus.utils.TableUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 页面控制器 - 处理页面请求
 *
 * @author FileServer
 * @version 1.0
 */
@Controller
public class PageController {

    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @Value("${server.port:9101}")
    private String serverPort;

    @Value("${server.address:localhost}")
    private String serverAddress;

    @Autowired
    private FileMetaService fileMetaService;

    @Autowired
    private UserService userService;

    @Autowired
    private TableDataService tableDataService;

    /**
     * 根路径重定向到文件上传页面
     *
     * @return 重定向路径
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/page/";
    }

    /**
     * 首页 - 文件上传页面
     *
     * @param model 模型对象
     * @return 模板名称
     */
    @GetMapping("/page/")
    public String index(Model model) {
        addCommonAttributes(model);
        return "pages/upload";
    }

    /**
     * 文件测试页面
     *
     * @param model 模型对象
     * @return 模板名称
     */
    @GetMapping("/page/file-test")
    public String fileTest(Model model) {
        addCommonAttributes(model);
        return "pages/upload";
    }

    /**
     * 文件预览测试页面
     *
     * @param model 模型对象
     * @return 模板名称
     */
    @GetMapping("/page/preview-test")
    public String previewTest(Model model) {
        addCommonAttributes(model);
        return "pages/preview-test";
    }

    /**
     * 文件列表页面
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param model 模型对象
     * @return 模板名称
     */
    @GetMapping("/page/file-list")
    public String fileList(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String keyword,
                           Model model) {
        addCommonAttributes(model);

        try {
            // 参数验证
            if (page < 1) page = 1;
            if (size < 1) size = 10;
            if (size > 100) size = 100; // 限制最大页面大小

            // 启动分页
            PageHelper.startPage(page, size);

            // 查询数据
            List<FileMeta> files;
            if (keyword != null && !keyword.trim().isEmpty()) {
                files = fileMetaService.searchByPage(keyword.trim());
            } else {
                files = fileMetaService.findByPage();
            }

            // 创建分页信息
            PageInfo<FileMeta> originalPageInfo = new PageInfo<>(files);

            // 调试日志
            logger.debug("Pagination info - Page: {}, Size: {}, Total: {}, Pages: {}, HasNext: {}, HasPrev: {}",
                originalPageInfo.getPageNum(), originalPageInfo.getPageSize(), originalPageInfo.getTotal(),
                originalPageInfo.getPages(), originalPageInfo.isHasNextPage(), originalPageInfo.isHasPreviousPage());

            // 转换为表格数据
            PageInfo<Map<String, Object>> tablePageData = tableDataService.convertToTablePageData(originalPageInfo, FileMeta.class);

            // 设置模型属性
            model.addAttribute("pageData", tablePageData);
            model.addAttribute("keyword", keyword);
            model.addAttribute("hasKeyword", !StringUtils.isBlank(keyword));
            model.addAttribute("columns", TableUtils.generateColumns(FileMeta.class));

        } catch (Exception e) {
            logger.error("Error loading file list: page={}, size={}, keyword={}", page, size, keyword, e);

            // 创建空的分页数据
            PageInfo<Map<String, Object>> emptyPageData = new PageInfo<>(new ArrayList<>());
            emptyPageData.setPageNum(page);
            emptyPageData.setPageSize(size);
            emptyPageData.setTotal(0);
            emptyPageData.setPages(0);

            model.addAttribute("pageData", emptyPageData);
            model.addAttribute("keyword", keyword);
            model.addAttribute("hasKeyword", !StringUtils.isBlank(keyword));
            model.addAttribute("columns", TableUtils.generateColumns(FileMeta.class));
            model.addAttribute("error", "加载文件列表失败，请稍后重试");
        }

        return "pages/file-list";
    }



    /**
     * 系统状态页面
     *
     * @param model 模型对象
     * @return 模板名称
     */
    @GetMapping("/page/system-status")
    public String systemStatus(Model model) {
        addCommonAttributes(model);

        // 添加系统统计信息
        addSystemStatistics(model);

        return "pages/system-status";
    }

    /**
     * 添加通用属性
     *
     * @param model 模型对象
     */
    private void addCommonAttributes(Model model) {
        String baseUrl = "http://" + serverAddress + ":" + serverPort;
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("serverInfo", "FileServer v1.0 - Spring Boot 3.x + JDK 17 + Jetty");
        model.addAttribute("serverAddress", serverAddress);
        model.addAttribute("serverPort", serverPort);
    }

    /**
     * 添加系统统计信息
     *
     * @param model 模型对象
     */
    private void addSystemStatistics(Model model) {
        try {
            // 文件统计
            int totalFiles = fileMetaService.getTotalCount();
            model.addAttribute("totalFiles", totalFiles);

            // 用户统计
            List<User> allUsers = userService.findAllUsers();
            long totalUsers = allUsers.size();
            long activeUsers = allUsers.stream().filter(u -> u.getStatus() == 1).count();

            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("activeUsers", activeUsers);
            model.addAttribute("inactiveUsers", totalUsers - activeUsers);

            // 系统运行时间
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();

            model.addAttribute("totalMemory", formatBytes(totalMemory));
            model.addAttribute("usedMemory", formatBytes(usedMemory));
            model.addAttribute("freeMemory", formatBytes(freeMemory));
            model.addAttribute("maxMemory", formatBytes(maxMemory));
            model.addAttribute("memoryUsagePercent", (int) ((double) usedMemory / totalMemory * 100));

            // JVM信息
            model.addAttribute("javaVersion", System.getProperty("java.version"));
            model.addAttribute("javaVendor", System.getProperty("java.vendor"));
            model.addAttribute("osName", System.getProperty("os.name"));
            model.addAttribute("osVersion", System.getProperty("os.version"));
            model.addAttribute("osArch", System.getProperty("os.arch"));

        } catch (Exception e) {
            // 如果获取统计信息失败，设置默认值
            model.addAttribute("totalFiles", 0);
            model.addAttribute("totalUsers", 0);
            model.addAttribute("activeUsers", 0);
            model.addAttribute("inactiveUsers", 0);

            // 设置内存相关的默认值
            model.addAttribute("totalMemory", "0 MB");
            model.addAttribute("usedMemory", "0 MB");
            model.addAttribute("freeMemory", "0 MB");
            model.addAttribute("maxMemory", "0 MB");
            model.addAttribute("memoryUsagePercent", 0);

            // 设置JVM信息的默认值
            model.addAttribute("javaVersion", "Unknown");
            model.addAttribute("javaVendor", "Unknown");
            model.addAttribute("osName", "Unknown");
            model.addAttribute("osVersion", "Unknown");
            model.addAttribute("osArch", "Unknown");

            // 记录错误日志
            logger.error("Failed to load system statistics: {}", e.getMessage(), e);
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