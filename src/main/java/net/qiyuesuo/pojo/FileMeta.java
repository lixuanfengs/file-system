package net.qiyuesuo.pojo;

import java.util.Date;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/pojo/FileMeta.class */
public class FileMeta {
    private String id;
    private String filename;
    private String filepath;
    private Long filesize;
    private String subfix;
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
