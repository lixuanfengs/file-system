package net.cactus.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 角色权限关联数据访问层
 * 
 * @author FileServer
 * @version 1.0
 */
public interface RolePermissionDao {
    
    /**
     * 为角色分配权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @param createBy 创建人
     * @return 影响行数
     */
    @Insert({
        "INSERT INTO T_SYSTEM_ROLE_PERMISSION(role_id, permission_id, create_time, create_by) ",
        "VALUES(#{roleId}, #{permissionId}, NOW(), #{createBy})"
    })
    int assignPermissionToRole(@Param("roleId") Long roleId, 
                              @Param("permissionId") Long permissionId, 
                              @Param("createBy") String createBy);
    
    /**
     * 移除角色权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 影响行数
     */
    @Delete("DELETE FROM T_SYSTEM_ROLE_PERMISSION WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int removePermissionFromRole(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    /**
     * 清空角色的所有权限
     * 
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM T_SYSTEM_ROLE_PERMISSION WHERE role_id = #{roleId}")
    int clearRolePermissions(@Param("roleId") Long roleId);
    
    /**
     * 查询角色的权限ID列表
     * 
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Select("SELECT permission_id FROM T_SYSTEM_ROLE_PERMISSION WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 查询权限的角色ID列表
     * 
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM T_SYSTEM_ROLE_PERMISSION WHERE permission_id = #{permissionId}")
    List<Long> findRoleIdsByPermissionId(@Param("permissionId") Long permissionId);
    
    /**
     * 检查角色是否拥有指定权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否拥有权限
     */
    @Select("SELECT COUNT(*) FROM T_SYSTEM_ROLE_PERMISSION WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    int checkRoleHasPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    /**
     * 批量为角色分配权限
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @param createBy 创建人
     * @return 影响行数
     */
    @Insert({
        "<script>",
        "INSERT INTO T_SYSTEM_ROLE_PERMISSION(role_id, permission_id, create_time, create_by) VALUES ",
        "<foreach collection='permissionIds' item='permissionId' separator=','>",
        "(#{roleId}, #{permissionId}, NOW(), #{createBy})",
        "</foreach>",
        "</script>"
    })
    int batchAssignPermissionsToRole(@Param("roleId") Long roleId, 
                                   @Param("permissionIds") List<Long> permissionIds, 
                                   @Param("createBy") String createBy);
}
