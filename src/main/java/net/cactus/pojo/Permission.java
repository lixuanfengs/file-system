package net.cactus.pojo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 * 
 * @author FileServer
 * @version 1.0
 */
public class Permission {
    
    private Long id;
    private String permissionName;
    private String permissionCode;
    private Integer permissionType; // 1-菜单, 2-按钮, 3-API
    private Long parentId;
    private String path;
    private String description;
    private Integer status; // 0-禁用, 1-启用
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    
    // 子权限列表（用于树形结构）
    private List<Permission> children;
    
    // 构造函数
    public Permission() {}
    
    public Permission(String permissionName, String permissionCode, Integer permissionType) {
        this.permissionName = permissionName;
        this.permissionCode = permissionCode;
        this.permissionType = permissionType;
        this.status = 1;
        this.parentId = 0L;
        this.sortOrder = 0;
    }
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPermissionName() {
        return permissionName;
    }
    
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
    
    public String getPermissionCode() {
        return permissionCode;
    }
    
    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }
    
    public Integer getPermissionType() {
        return permissionType;
    }
    
    public void setPermissionType(Integer permissionType) {
        this.permissionType = permissionType;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    
    public String getUpdateBy() {
        return updateBy;
    }
    
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    
    public List<Permission> getChildren() {
        return children;
    }
    
    public void setChildren(List<Permission> children) {
        this.children = children;
    }
    
    // 便利方法
    public String getPermissionTypeName() {
        switch (permissionType) {
            case 1: return "菜单";
            case 2: return "按钮";
            case 3: return "API";
            default: return "未知";
        }
    }
    
    public boolean isMenu() {
        return permissionType != null && permissionType == 1;
    }
    
    public boolean isButton() {
        return permissionType != null && permissionType == 2;
    }
    
    public boolean isApi() {
        return permissionType != null && permissionType == 3;
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", permissionName='" + permissionName + '\'' +
                ", permissionCode='" + permissionCode + '\'' +
                ", permissionType=" + permissionType +
                ", status=" + status +
                '}';
    }
}
