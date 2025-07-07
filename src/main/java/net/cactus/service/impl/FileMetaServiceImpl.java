package net.cactus.service.impl;

import net.cactus.dao.FileMetaDao;
import net.cactus.pojo.FileMeta;
import net.cactus.service.FileMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/service/impl/FileMetaServiceImpl.class */
public class FileMetaServiceImpl implements FileMetaService {

    @Autowired
    private FileMetaDao fileMetaDao;

    @Override // net.qiyuesuo.service.FileMetaService
    public int saveInfo(FileMeta fileMeta) {
        return this.fileMetaDao.insert(fileMeta);
    }

    @Override // net.qiyuesuo.service.FileMetaService
    public int deleteInfo(String fileKey) {
        return this.fileMetaDao.delete(fileKey);
    }

    @Override // net.qiyuesuo.service.FileMetaService
    public FileMeta getInfo(String fileKey) {
        return this.fileMetaDao.getByKey(fileKey);
    }
}
