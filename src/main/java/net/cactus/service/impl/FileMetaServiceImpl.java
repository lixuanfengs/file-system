package net.cactus.service.impl;

import net.cactus.dao.FileMetaDao;
import net.cactus.pojo.FileMeta;
import net.cactus.service.FileMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileMetaServiceImpl implements FileMetaService {

    @Autowired
    private FileMetaDao fileMetaDao;

    @Override
    public int saveInfo(FileMeta fileMeta) {
        return this.fileMetaDao.insert(fileMeta);
    }

    @Override
    public int deleteInfo(String fileKey) {
        return this.fileMetaDao.delete(fileKey);
    }

    @Override
    public FileMeta getInfo(String fileKey) {
        return this.fileMetaDao.getByKey(fileKey);
    }
}
