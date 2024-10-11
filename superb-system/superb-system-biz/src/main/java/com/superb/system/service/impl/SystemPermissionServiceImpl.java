package com.superb.system.service.impl;

import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.database.vo.BatchId;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.dto.Options;
import com.superb.system.api.dto.RolePerms;
import com.superb.system.api.entity.SystemPermission;
import com.superb.system.api.enums.MenuType;
import com.superb.system.api.vo.Distribution;
import com.superb.system.mapper.SystemPermissionMapper;
import com.superb.system.service.SystemPermissionService;
import com.superb.system.service.SystemRoleService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Tran;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单权限表;(system_permission)表服务实现类
 *
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
@Component
public class SystemPermissionServiceImpl extends ServiceImpl<SystemPermissionMapper, SystemPermission> implements SystemPermissionService {

    @Inject
    private SystemRoleService roleService;

    @Override
    public List<String> listByUserId(String userId) {
        // 获取用户角色列表
        List<String> roleIds = roleService.listByUserId(userId);
        return this.mapper.listByUserId(roleIds);
    }

    @Override
    public List<SystemPermission> topMenuList(String userId) {
        List<String> roleIds = roleService.listByUserId(userId);
        List<SystemPermission> menuItems = this.mapper.menuList(roleIds, 0);
        return treeMenu(menuItems);
    }

    @Override
    public List<SystemPermission> sideMenuList(String userId) {
        List<String> roleIds = roleService.listByUserId(userId);
        List<SystemPermission> menuItems = this.mapper.menuList(roleIds, 1);
        return treeMenu(menuItems);
    }

    @Override
    public List<SystemPermission> productMenuList(String userId) {
        List<String> roleIds = roleService.listByUserId(userId);
        List<SystemPermission> menuItems = this.mapper.menuList(roleIds, 2);
        return treeMenu(menuItems);
    }

    @Override
    public List<SystemPermission> menuList(String userId, MenuType type) {
        return switch (type) {
            case top -> this.topMenuList(userId);
            case side -> this.sideMenuList(userId);
            default -> this.productMenuList(userId);
        };
    }

    @Override
    public List<SystemPermission> menuTree(String type, boolean isPerms) {
        QueryChain<SystemPermission> queryChain = super.queryChain();
        queryChain.eq(SystemPermission::getType, type);
        queryChain.eq(SystemPermission::getTenantId, HeadersUtils.getTenantId());
        queryChain.in(SystemPermission::getMenuType, isPerms ? Arrays.asList(0, 1, 2) : Arrays.asList(0, 1));
        queryChain.orderBy(SystemPermission::getSort).asc();
        List<SystemPermission> list = super.list(queryChain);
        return treeMenu(list);
    }

    @Override
    public List<Options> parentMenu(String type) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("id AS value", "name AS label");
        queryWrapper.eq(SystemPermission::getTenantId, HeadersUtils.getTenantId());
        queryWrapper.eq(SystemPermission::getMenuType, 0);
        queryWrapper.eq(SystemPermission::getType, type);
        return super.listAs(queryWrapper, Options.class);
    }

    /**
     * 根据父级菜单递归构建树
     *
     * @param id        父级id
     * @param menuItems 子级菜单
     * @return
     */
    private List<SystemPermission> buildTree(String id, List<SystemPermission> menuItems) {
        return menuItems.stream()
                .filter(item -> StringUtils.isNotBlank(item.getParentId()) && item.getParentId().equals(id))
                .peek(item -> {
                    if (item.getMenuType() == 1) {
                        item.setPermissions(buildTree(item.getId(), menuItems));
                    } else {
                        item.setChildren(buildTree(item.getId(), menuItems));
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取树：先获取父级菜单，然后根据父级菜单构建树
     *
     * @param items 原始数据
     * @return
     */
    public List<SystemPermission> treeMenu(List<SystemPermission> items) {
        return items.stream()
                .filter(item -> item.getMenuType() == 0)
                .peek(item -> item.setChildren(buildTree(item.getId(), items)))
                .collect(Collectors.toList());
    }

    @Override
    @Tran
    public boolean deletePermsByRoleId(String roleId) {
        this.mapper.deletePermsByRoleId(roleId, null);
        return true;
    }

    @Tran
    @Override
    public boolean deletePermsByRoleId(String roleId, String permissionId) {
        return this.mapper.deletePermsByRoleId(roleId, permissionId) > 0;
    }

    @Tran
    @Override
    public boolean distribution(Distribution distribution) {
        // 权限集合>0才会操作sql
        if (distribution.getPermissionIds().size() > 0) {
            this.mapper.distribution(distribution);
        }
        if (distribution.getMenuIds().size() > 0) {
            this.mapper.distributionMenus(distribution);
        }
        return true;
    }

    @Override
    public RolePerms getRolePermission(String roleId) {
        RolePerms rolePerms = new RolePerms();
        rolePerms.setMenuIds(this.mapper.getRolePermission(roleId, 0));
        rolePerms.setPermissionIds(this.mapper.getRolePermission(roleId, 1));
        return rolePerms;
    }

    @Override
    public void buildTree(List<SystemPermission> list) {
        for (SystemPermission permission : list) {
            QueryChain<SystemPermission> queryChain = super.queryChain();
            queryChain.eq(SystemPermission::getParentId, permission.getId());
            List<SystemPermission> permissions = super.list(queryChain);
            if (!permissions.isEmpty()) {
                permission.setChildren(permissions);
                buildTree(permissions);
            }
        }
    }

    @Tran
    @Override
    public boolean synchronization(BatchId batchId, String tenantId) {
        // 删除租户所有菜单数据
        LogicDeleteManager.execWithoutLogicDelete(() -> super.remove(super.query().eq(SystemPermission::getTenantId, tenantId)));
        // 迭代选中的parentId进行同步
        for (String id : batchId.getId()) {
            SystemPermission permission = super.getById(id);
            if (permission.getMenuType() != 0) {
                throw new SuperbException("请选择父级菜单进行同步");
            }
            this.synchronization(permission, tenantId, null);
        }
        return true;
    }

    /**
     * 同步数据
     * @param permission
     */
    private void synchronization(SystemPermission permission, String tenantId, String parentId) {
        // 获取需要同步的子级菜单和权限
        QueryChain<SystemPermission> queryChain = super.queryChain();
        queryChain.eq(SystemPermission::getParentId, permission.getId());
        List<SystemPermission> list = super.list(queryChain);
        // 设置id为null，新增
        permission.setId(null);
        // 主动设置租户标识
        permission.setTenantId(tenantId);
        // 如果父级id不为null，则说明是子级
        if (parentId != null) {
            permission.setParentId(parentId);
        }
        // 保存新租户数据
        super.save(permission);
        // 如果子级不为空进行同步
        if (!list.isEmpty()) {
            // 将同步的子级迭代
            for (SystemPermission systemPermission : list) {
                synchronization(systemPermission, tenantId, permission.getId());
            }
        }
    }

}