package net.qiyuesuo.dao;

import net.qiyuesuo.pojo.FileMeta;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/dao/FileMetaDao.class */
public interface FileMetaDao {
    @Select({"select * from T_SYSTEM_FILE where id = #{uuid}"})
    FileMeta getByKey(String key);

    @Delete({"delete from T_SYSTEM_FILE where id = #{uuid}"})
    int delete(String key);

    @Insert({"insert into T_SYSTEM_FILE(id,filename,filepath,filesize,subfix,createtime) values(#{uuid},#{filename},#{filepath},#{filesize},#{subfix},#{createtime})"})
    int insert(FileMeta fileMeta);

    @Update({"update T_SYSTEM_FILE set filename = #{filename},filepath = #{filepath},filesize = #{filesize},subfix = #{subfix},createtime = #{createtime} where id = #{uuid} "})
    int update(FileMeta fileMeta);
}
