package net.cactus.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.cactus.pojo.FileMeta;
import net.cactus.service.FileService;
import net.cactus.utils.ResultMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping({"/file"})
@RestController
/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/controller/FileController.class */
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping(path = {"/getInfo"}, method = {RequestMethod.GET})
    public ResultMessage getFileMeta(@RequestParam String fileKey, HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (fileKey == null) {
            return ResultMessage.newErrorMessage("请输入文件uuid!");
        }
        FileMeta meta = this.fileService.getFileInfo(fileKey);
        return ResultMessage.newSuccessMessage(meta);
    }

    @RequestMapping(path = {"/{fileKey}"}, method = {RequestMethod.GET})
    public ResultMessage downFile(@PathVariable String fileKey, HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (fileKey == null) {
            return ResultMessage.newErrorMessage("请输入文件uuid！");
        }
        FileMeta meta = this.fileService.getFileInfo(fileKey);
        OutputStream out = null;
        FileInputStream in = null;
        try {
            try {
                out = res.getOutputStream();
                res.setStatus(200);
                res.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(meta.getFilename(), "UTF-8"));
                res.setContentType("text/plain; charset=utf-8");
                in = FileUtils.openInputStream(Paths.get(meta.getFilepath(), new String[0]).toFile());
                FileChannel channel = in.getChannel();
                ByteBuffer bf = ByteBuffer.allocate(1024);
                while (true) {
                    int length = channel.read(bf);
                    if (length == -1) {
                        break;
                    }
                    byte[] bytes = bf.array();
                    out.write(bytes, 0, length);
                    bf.clear();
                }
                out.flush();
                bf.clear();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                if (out != null) {
                    out.close();
                }
            }
            return ResultMessage.SUCCESS;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                    throw th;
                }
            }
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    @RequestMapping(path = {"/viewFile/{fileKey}"}, method = {RequestMethod.GET})
    public ResultMessage viewFile(@PathVariable String fileKey, HttpServletResponse response) throws Exception {
        return this.fileService.viewFile(fileKey, "1", response);
    }

    @RequestMapping(path = {"/viewSmallFile/{fileKey}"}, method = {RequestMethod.GET})
    public ResultMessage viewSmallFile(@PathVariable String fileKey, HttpServletResponse response) throws Exception {
        return this.fileService.viewFile(fileKey, "2", response);
    }

    @RequestMapping(path = {"/uploadOne"}, method = {RequestMethod.POST})
    public ResultMessage uploadOne(@RequestParam MultipartFile file, HttpServletRequest req, HttpServletResponse res) throws IOException {
        String fileKey = this.fileService.saveFile(file);
        if (fileKey == null) {
            return ResultMessage.newErrorMessage("上传文件失败!");
        }
        return ResultMessage.newSuccessMessage(fileKey);
    }

    @RequestMapping(path = {"/uploadFileMore"}, method = {RequestMethod.POST})
    public ResultMessage uploadFileMore(@RequestParam MultipartFile[] file, HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (file == null || file.length == 0) {
            return ResultMessage.newErrorMessage("请选择文件!");
        }
        StringBuffer sb = new StringBuffer();
        for (MultipartFile f : file) {
            String fileKey = this.fileService.saveFile(f);
            if (!StringUtils.isBlank(fileKey)) {
                sb.append(fileKey).append(",");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return ResultMessage.newSuccessMessage(sb.toString());
    }

    @RequestMapping(path = {"/delete"}, method = {RequestMethod.POST})
    public ResultMessage deleteFile(@RequestParam String fileKey, HttpServletRequest req, HttpServletResponse res) throws Exception {
        FileMeta meta = this.fileService.getFileInfo(fileKey);
        int result = this.fileService.deleteFile(meta);
        if (result < 1) {
            return ResultMessage.newErrorMessage("删除文件失败！");
        }
        return ResultMessage.newSuccessMessage("删除文件: " + fileKey + " 成功!");
    }
}
