package net.cactus.dao;

import net.cactus.pojo.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 角色数据访问层
 * 
 * @author FileServer
 * @version 1.0
 */
public interface RoleDao {
    
    /**
     * 根据ID查询角色
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    @Select("SELECT * FROM T_SYSTEM_ROLE WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roleName", column = "role_name"),
        @Result(property = "roleCode", column = "role_code"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    Role findById(@Param("id") Long id);
    
    /**
     * 根据角色名查询角色
     * 
     * @param roleName 角色名
     * @return 角色信息
     */
    @Select("SELECT * FROM T_SYSTEM_ROLE WHERE role_name = #{roleName}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roleName", column = "role_name"),
        @Result(property = "roleCode", column = "role_code"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    Role findByRoleName(@Param("roleName") String roleName);
    
    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    @Select("SELECT * FROM T_SYSTEM_ROLE ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roleName", column = "role_name"),
        @Result(property = "roleCode", column = "role_code"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Role> findAll();
    
    /**
     * 插入角色
     * 
     * @param role 角色信息
     * @return 影响行数
     */
    @Insert({
        "INSERT INTO T_SYSTEM_ROLE(",
        "role_name, role_code, description, status, create_time, create_by) VALUES(",
        "#{roleName}, #{roleCode}, #{description}, #{status}, #{createTime}, #{createBy})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);
    
    /**
     * 更新角色信息
     * 
     * @param role 角色信息
     * @return 影响行数
     */
    @Update({
        "UPDATE T_SYSTEM_ROLE SET ",
        "role_name = #{roleName}, ",
        "role_code = #{roleCode}, ",
        "description = #{description}, ",
        "status = #{status}, ",
        "update_time = NOW(), ",
        "update_by = #{updateBy} ",
        "WHERE id = #{id}"
    })
    int update(Role role);
    
    /**
     * 根据ID删除角色
     * 
     * @param id 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM T_SYSTEM_ROLE WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询用户角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select({
        "SELECT r.* FROM T_SYSTEM_ROLE r ",
        "INNER JOIN T_SYSTEM_USER_ROLE ur ON r.id = ur.role_id ",
        "WHERE ur.user_id = #{userId} AND r.status = 1"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roleName", column = "role_name"),
        @Result(property = "roleCode", column = "role_code"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}
