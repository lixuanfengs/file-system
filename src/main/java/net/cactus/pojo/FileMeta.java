package net.cactus.pojo;

import net.cactus.annotation.TableColumn;
import java.util.Date;

public class FileMeta {

    @TableColumn(key = "uuid", label = "文件ID", order = 2)
    private String id;

    @TableColumn(key = "filename", label = "文件名", order = 1)
    private String filename;

    // This field is not exposed in the table
    private String filepath;

    @TableColumn(key = "filesize", label = "大小", type = "size", order = 3)
    private Long filesize;

    @TableColumn(key = "subfix", label = "类型", type = "badge", order = 4)
    private String subfix;

    @TableColumn(key = "createtime", label = "上传时间", type = "date", order = 5)
    private Date createtime;

    public FileMeta() {
    }

    public FileMeta(String uuid, String filename, String filepath, Long filesize, String subfix, Date createtime) {
        this.id = uuid;
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
        this.subfix = subfix;
        this.createtime = createtime;
    }

    public String getUuid() {
        return this.id;
    }

    public void setUuid(String uuid) {
        this.id = uuid;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return this.filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Long getFilesize() {
        return this.filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public String getSubfix() {
        return this.subfix;
    }

    public void setSubfix(String subfix) {
        this.subfix = subfix;
    }

    public Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}