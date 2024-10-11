package com.superb.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.superb.system.api.dto.Options;
import com.superb.system.api.entity.SystemPermission;
import com.superb.system.api.vo.Distribution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统菜单权限表;(system_permission)表数据库访问层
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
@Mapper
public interface SystemPermissionMapper extends BaseMapper<SystemPermission> {

    List<String> listByUserId(@Param("roleIds") List<String> roleIds);

    /**
     * 菜单列表
     * @param roleIds 用户id
     * @param menuType 菜单类型
     * @return
     */
    List<SystemPermission> menuList(@Param("roleIds") List<String> roleIds, @Param("menuType") Integer menuType);

    int deletePermsByRoleId(@Param("roleId") String roleId, @Param("permissionId") String permissionId);

    int distribution(Distribution distribution);

    List<String> getRolePermission(@Param("roleId") String roleId, @Param("type") Integer type);

    int distributionMenus(Distribution distribution);
}