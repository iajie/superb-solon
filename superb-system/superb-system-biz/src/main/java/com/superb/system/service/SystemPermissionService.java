package com.superb.system.service;

import com.mybatisflex.core.service.IService;
import com.superb.common.database.vo.BatchId;
import com.superb.system.api.dto.Options;
import com.superb.system.api.dto.RolePerms;
import com.superb.system.api.entity.SystemPermission;
import com.superb.system.api.enums.MenuType;
import com.superb.system.api.vo.Distribution;

import java.util.List;

/**
 * 系统菜单权限表;(system_permission)表服务接口
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
public interface SystemPermissionService extends IService<SystemPermission> {

    List<String> listByUserId(String roleId);

    List<SystemPermission> topMenuList(String userId);

    List<SystemPermission> menuList(String userId, MenuType type);

    List<SystemPermission> sideMenuList(String userId);

    List<SystemPermission> productMenuList(String userId);

    /**
     * 获得菜单树
     * @param type 菜单类型
     * @param isPerms 是否获取权限
     * @return
     */
    List<SystemPermission> menuTree(String type, boolean isPerms);

    List<Options> parentMenu(String type);

    /**
     * 根据角色删除权限关系
     * @param roleId 角色id
     * @return
     */
    boolean deletePermsByRoleId(String roleId);

    /**
     * 根据角色删除权限关系
     * @param roleId 角色id
     * @param permissionId 权限id
     * @return
     */
    boolean deletePermsByRoleId(String roleId, String permissionId);

    /**
     * 分配权限
     * @param distribution
     */
    boolean distribution(Distribution distribution);

    /**
     * 获取角色权限列表
     * @param roleId
     * @return
     */
    RolePerms getRolePermission(String roleId);

    /**
     * 构建菜单树
     * @param list
     */
    void buildTree(List<SystemPermission> list);

    boolean synchronization(BatchId batchId, String tenantId);

}