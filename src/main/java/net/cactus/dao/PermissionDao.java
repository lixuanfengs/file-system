package net.cactus.dao;

import net.cactus.pojo.Permission;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 权限数据访问层
 * 
 * @author FileServer
 * @version 1.0
 */
public interface PermissionDao {
    
    /**
     * 根据ID查询权限
     * 
     * @param id 权限ID
     * @return 权限信息
     */
    @Select("SELECT * FROM T_SYSTEM_PERMISSION WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "permissionName", column = "permission_name"),
        @Result(property = "permissionCode", column = "permission_code"),
        @Result(property = "permissionType", column = "permission_type"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "path", column = "path"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    Permission findById(@Param("id") Long id);
    
    /**
     * 根据权限编码查询权限
     * 
     * @param permissionCode 权限编码
     * @return 权限信息
     */
    @Select("SELECT * FROM T_SYSTEM_PERMISSION WHERE permission_code = #{permissionCode}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "permissionName", column = "permission_name"),
        @Result(property = "permissionCode", column = "permission_code"),
        @Result(property = "permissionType", column = "permission_type"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "path", column = "path"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    Permission findByPermissionCode(@Param("permissionCode") String permissionCode);
    
    /**
     * 查询所有权限
     * 
     * @return 权限列表
     */
    @Select("SELECT * FROM T_SYSTEM_PERMISSION ORDER BY sort_order ASC, create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "permissionName", column = "permission_name"),
        @Result(property = "permissionCode", column = "permission_code"),
        @Result(property = "permissionType", column = "permission_type"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "path", column = "path"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Permission> findAll();
    
    /**
     * 根据父权限ID查询子权限
     * 
     * @param parentId 父权限ID
     * @return 权限列表
     */
    @Select("SELECT * FROM T_SYSTEM_PERMISSION WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order ASC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "permissionName", column = "permission_name"),
        @Result(property = "permissionCode", column = "permission_code"),
        @Result(property = "permissionType", column = "permission_type"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "path", column = "path"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Permission> findByParentId(@Param("parentId") Long parentId);
    
    /**
     * 根据权限类型查询权限
     * 
     * @param permissionType 权限类型
     * @return 权限列表
     */
    @Select("SELECT * FROM T_SYSTEM_PERMISSION WHERE permission_type = #{permissionType} AND status = 1 ORDER BY sort_order ASC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "permissionName", column = "permission_name"),
        @Result(property = "permissionCode", column = "permission_code"),
        @Result(property = "permissionType", column = "permission_type"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "path", column = "path"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Permission> findByPermissionType(@Param("permissionType") Integer permissionType);
    
    /**
     * 插入权限
     * 
     * @param permission 权限信息
     * @return 影响行数
     */
    @Insert({
        "INSERT INTO T_SYSTEM_PERMISSION(",
        "permission_name, permission_code, permission_type, parent_id, path, description, ",
        "status, sort_order, create_time, create_by) VALUES(",
        "#{permissionName}, #{permissionCode}, #{permissionType}, #{parentId}, #{path}, #{description}, ",
        "#{status}, #{sortOrder}, #{createTime}, #{createBy})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Permission permission);
    
    /**
     * 更新权限信息
     * 
     * @param permission 权限信息
     * @return 影响行数
     */
    @Update({
        "UPDATE T_SYSTEM_PERMISSION SET ",
        "permission_name = #{permissionName}, ",
        "permission_code = #{permissionCode}, ",
        "permission_type = #{permissionType}, ",
        "parent_id = #{parentId}, ",
        "path = #{path}, ",
        "description = #{description}, ",
        "status = #{status}, ",
        "sort_order = #{sortOrder}, ",
        "update_time = NOW(), ",
        "update_by = #{updateBy} ",
        "WHERE id = #{id}"
    })
    int update(Permission permission);
    
    /**
     * 根据ID删除权限
     * 
     * @param id 权限ID
     * @return 影响行数
     */
    @Delete("DELETE FROM T_SYSTEM_PERMISSION WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据角色ID查询角色权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select({
        "SELECT p.* FROM T_SYSTEM_PERMISSION p ",
        "INNER JOIN T_SYSTEM_ROLE_PERMISSION rp ON p.id = rp.permission_id ",
        "WHERE rp.role_id = #{roleId} AND p.status = 1 ",
        "ORDER BY p.sort_order ASC"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "permissionName", column = "permission_name"),
        @Result(property = "permissionCode", column = "permission_code"),
        @Result(property = "permissionType", column = "permission_type"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "path", column = "path"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查询用户权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select({
        "SELECT DISTINCT p.* FROM T_SYSTEM_PERMISSION p ",
        "INNER JOIN T_SYSTEM_ROLE_PERMISSION rp ON p.id = rp.permission_id ",
        "INNER JOIN T_SYSTEM_USER_ROLE ur ON rp.role_id = ur.role_id ",
        "WHERE ur.user_id = #{userId} AND p.status = 1 ",
        "ORDER BY p.sort_order ASC"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "permissionName", column = "permission_name"),
        @Result(property = "permissionCode", column = "permission_code"),
        @Result(property = "permissionType", column = "permission_type"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "path", column = "path"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);
}
