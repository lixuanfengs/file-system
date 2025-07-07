package net.cactus.service;

import net.cactus.pojo.FileMeta;


public interface FileMetaService {
    int saveInfo(FileMeta fileMeta);

    int deleteInfo(String fileKey);

    FileMeta getInfo(String fileKey);
}
