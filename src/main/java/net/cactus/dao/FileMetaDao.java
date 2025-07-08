package net.cactus.dao;

import net.cactus.pojo.FileMeta;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface FileMetaDao {

    @Select({"select * from T_SYSTEM_FILE where id = #{uuid}"})
    FileMeta getByKey(String key);

    @Delete({"delete from T_SYSTEM_FILE where id = #{uuid}"})
    int delete(String key);

    @Insert({"insert into T_SYSTEM_FILE(id,filename,filepath,filesize,subfix,createtime) values(#{uuid},#{filename},#{filepath},#{filesize},#{subfix},#{createtime})"})
    int insert(FileMeta fileMeta);

    @Update({"update T_SYSTEM_FILE set filename = #{filename},filepath = #{filepath},filesize = #{filesize},subfix = #{subfix},createtime = #{createtime} where id = #{uuid} "})
    int update(FileMeta fileMeta);

    @Select({"select * from T_SYSTEM_FILE order by createtime desc limit #{offset}, #{limit}"})
    List<FileMeta> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(*) from T_SYSTEM_FILE"})
    int count();

    @Select({"select * from T_SYSTEM_FILE where filename like concat('%', #{keyword}, '%') order by createtime desc limit #{offset}, #{limit}"})
    List<FileMeta> searchByPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(*) from T_SYSTEM_FILE where filename like concat('%', #{keyword}, '%')"})
    int countByKeyword(@Param("keyword") String keyword);
}
