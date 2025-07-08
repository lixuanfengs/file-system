package net.cactus.controller;

import net.cactus.pojo.FileMeta;
import net.cactus.service.FileMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 页面控制器 - 处理页面请求
 *
 * @author FileServer
 * @version 1.0
 */
@Controller
public class PageController {

    @Value("${server.port:9101}")
    private String serverPort;

    @Value("${server.address:localhost}")
    private String serverAddress;

    @Autowired
    private FileMetaService fileMetaService;

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

        List<FileMeta> files;
        int totalCount;

        if (keyword != null && !keyword.trim().isEmpty()) {
            files = fileMetaService.searchByPage(keyword.trim(), page, size);
            totalCount = fileMetaService.getSearchCount(keyword.trim());
            model.addAttribute("keyword", keyword.trim());
        } else {
            files = fileMetaService.findByPage(page, size);
            totalCount = fileMetaService.getTotalCount();
        }

        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("files", files);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasKeyword", keyword != null && !keyword.trim().isEmpty());

        return "pages/file-list";
    }

    /**
     * 文件管理页面
     *
     * @param model 模型对象
     * @return 模板名称
     */
    @GetMapping("/page/file-manage")
    public String fileManage(Model model) {
        addCommonAttributes(model);
        return "pages/file-manage";
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
}
