package net.cactus.controller;

import net.cactus.utils.ResultMessage;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * JODConverter诊断控制器
 * 提供LibreOffice和JODConverter状态检查的REST接口
 */
@RestController
@RequestMapping("/api/diagnostic/jodconverter")
public class JodConverterDiagnosticController {

    private static final Logger logger = LoggerFactory.getLogger(JodConverterDiagnosticController.class);

    @Autowired(required = false)
    private DocumentConverter documentConverter;

    @Autowired(required = false)
    private LocalOfficeManager localOfficeManager;

    /**
     * 获取JODConverter整体状态
     */
    @GetMapping("/status")
    public ResultMessage getStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 基础配置状态
            status.put("jodConverterEnabled", documentConverter != null);
            status.put("officeManagerAvailable", localOfficeManager != null);
            
            // LibreOffice安装状态
            Map<String, Object> installation = checkLibreOfficeInstallation();
            status.put("installation", installation);
            
            // 进程状态
            Map<String, Object> processes = checkLibreOfficeProcesses();
            status.put("processes", processes);
            
            // 端口状态
            Map<String, Object> ports = checkPortConnectivity();
            status.put("ports", ports);
            
            // 整体健康状态
            boolean healthy = (boolean) installation.get("installed") && 
                             (int) ports.get("availableCount") > 0;
            status.put("healthy", healthy);
            status.put("message", healthy ? "JODConverter服务运行正常" : "JODConverter服务存在问题");
            
            return ResultMessage.newSuccessMessage(status);
            
        } catch (Exception e) {
            logger.error("获取JODConverter状态失败", e);
            return ResultMessage.newErrorMessage("状态检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查LibreOffice安装状态
     */
    @GetMapping("/installation")
    public ResultMessage checkInstallation() {
        try {
            Map<String, Object> result = checkLibreOfficeInstallation();
            return ResultMessage.newSuccessMessage(result);
        } catch (Exception e) {
            logger.error("检查LibreOffice安装状态失败", e);
            return ResultMessage.newErrorMessage("检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查LibreOffice进程状态
     */
    @GetMapping("/processes")
    public ResultMessage checkProcesses() {
        try {
            Map<String, Object> result = checkLibreOfficeProcesses();
            return ResultMessage.newSuccessMessage(result);
        } catch (Exception e) {
            logger.error("检查LibreOffice进程状态失败", e);
            return ResultMessage.newErrorMessage("检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查端口连接状态
     */
    @GetMapping("/ports")
    public ResultMessage checkPorts() {
        try {
            Map<String, Object> result = checkPortConnectivity();
            return ResultMessage.newSuccessMessage(result);
        } catch (Exception e) {
            logger.error("检查端口连接状态失败", e);
            return ResultMessage.newErrorMessage("检查失败: " + e.getMessage());
        }
    }

    /**
     * 执行简单的转换测试
     */
    @GetMapping("/test-conversion")
    public ResultMessage testConversion() {
        if (documentConverter == null) {
            return ResultMessage.newErrorMessage("JODConverter未配置或已禁用");
        }

        try {
            // 创建临时测试文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "jodconverter-test");
            Files.createDirectories(tempDir);
            
            Path testDoc = tempDir.resolve("test_" + System.currentTimeMillis() + ".txt");
            Path outputPdf = tempDir.resolve("test_" + System.currentTimeMillis() + ".pdf");
            
            // 创建测试内容
            String testContent = "JODConverter转换测试\n" +
                               "测试时间: " + new java.util.Date() + "\n" +
                               "这是一个简单的转换测试文档。";
            Files.write(testDoc, testContent.getBytes("UTF-8"));
            
            // 执行转换
            long startTime = System.currentTimeMillis();
            documentConverter.convert(testDoc.toFile()).to(outputPdf.toFile()).execute();
            long endTime = System.currentTimeMillis();
            
            // 检查结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", Files.exists(outputPdf) && Files.size(outputPdf) > 0);
            result.put("conversionTime", endTime - startTime);
            result.put("inputSize", Files.size(testDoc));
            result.put("outputSize", Files.exists(outputPdf) ? Files.size(outputPdf) : 0);
            result.put("message", "转换测试完成");
            
            // 清理临时文件
            Files.deleteIfExists(testDoc);
            Files.deleteIfExists(outputPdf);
            
            return ResultMessage.newSuccessMessage(result);
            
        } catch (Exception e) {
            logger.error("转换测试失败", e);
            return ResultMessage.newErrorMessage("转换测试失败: " + e.getMessage());
        }
    }

    private Map<String, Object> checkLibreOfficeInstallation() {
        Map<String, Object> result = new HashMap<>();
        
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
                    result.put("installed", true);
                    result.put("path", path);
                    result.put("executable", sofficeExe.getAbsolutePath());
                    result.put("message", "LibreOffice安装正常");
                    return result;
                }
            }
        }
        
        result.put("installed", false);
        result.put("path", null);
        result.put("message", "未找到LibreOffice安装");
        return result;
    }

    private Map<String, Object> checkLibreOfficeProcesses() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Process process = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq soffice*\"");
            process.waitFor();
            
            java.util.List<String> processes = new java.util.ArrayList<>();
            try (java.util.Scanner scanner = new java.util.Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.contains("soffice.exe") || line.contains("soffice.bin")) {
                        processes.add(line.trim());
                    }
                }
            }
            
            result.put("running", !processes.isEmpty());
            result.put("processCount", processes.size());
            result.put("processes", processes);
            result.put("message", processes.isEmpty() ? "未发现LibreOffice进程" : "发现 " + processes.size() + " 个LibreOffice进程");
            
        } catch (Exception e) {
            result.put("running", false);
            result.put("error", e.getMessage());
            result.put("message", "检查进程失败");
        }
        
        return result;
    }

    private Map<String, Object> checkPortConnectivity() {
        Map<String, Object> result = new HashMap<>();
        int[] ports = {2002, 2003, 2004};
        java.util.List<Integer> availablePorts = new java.util.ArrayList<>();
        java.util.List<Integer> unavailablePorts = new java.util.ArrayList<>();
        
        for (int port : ports) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("127.0.0.1", port), 1000);
                availablePorts.add(port);
            } catch (IOException e) {
                unavailablePorts.add(port);
            }
        }
        
        result.put("availablePorts", availablePorts);
        result.put("unavailablePorts", unavailablePorts);
        result.put("availableCount", availablePorts.size());
        result.put("totalCount", ports.length);
        result.put("allPortsAvailable", availablePorts.size() == ports.length);
        result.put("message", String.format("%d/%d 端口可用", availablePorts.size(), ports.length));
        
        return result;
    }
}
