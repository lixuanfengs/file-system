package net.cactus.service;

import net.cactus.pojo.FileMeta;

import java.util.List;

public interface FileMetaService {

    int saveInfo(FileMeta fileMeta);

    int deleteInfo(String fileKey);

    FileMeta getInfo(String fileKey);

    List<FileMeta> findByPage();

    int getTotalCount();

    List<FileMeta> searchByPage(String keyword);

    int getSearchCount(String keyword);

}
