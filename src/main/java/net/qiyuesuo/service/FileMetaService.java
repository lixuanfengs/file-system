package net.qiyuesuo.service;

import net.qiyuesuo.pojo.FileMeta;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/service/FileMetaService.class */
public interface FileMetaService {
    int saveInfo(FileMeta fileMeta);

    int deleteInfo(String fileKey);

    FileMeta getInfo(String fileKey);
}
