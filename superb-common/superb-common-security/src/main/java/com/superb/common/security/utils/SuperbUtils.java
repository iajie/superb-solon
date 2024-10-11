package com.superb.common.security.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.core.model.Result;
import com.superb.common.security.entity.Organization;
import com.superb.common.security.entity.SuperbTenant;
import com.superb.common.security.entity.SuperbUser;
import com.superb.common.security.entity.SuperbUserDataScope;
import com.superb.common.security.feign.RemoteSecuritySystemService;
import com.superb.common.utils.StringUtils;
import org.noear.nami.Nami;
import org.noear.solon.Solon;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-26 09:17
 */
public class SuperbUtils {

    private static RemoteSecuritySystemService remoteSecuritySystemService = Solon.context().getBean(RemoteSecuritySystemService.class);

    static {
        if (remoteSecuritySystemService == null) {
            remoteSecuritySystemService = Nami.builder().create(RemoteSecuritySystemService.class);
            Solon.context().beanMake(RemoteSecuritySystemService.class);
        }
    }
    /**
     * 通过租户ID获取租户信息
     * @return
     */
    public static SuperbTenant getTenantInfo() {
        Result<SuperbTenant> result = remoteSecuritySystemService.getTenantInfo();
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.TENANT_NULL, result.getMessage());
        }
    }

    /**
     * 通过登录id获取用户信息
     * @return
     */
    public static SuperbUser current() {
        Result<SuperbUser> result = remoteSecuritySystemService.getUserById(StpUtil.getLoginId().toString());
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.TENANT_NULL, result.getMessage());
        }
    }

    /**
     * 获取当前用户部门
     * @return
     */
    public static Organization getOrganization() {
        Result<Organization> result = remoteSecuritySystemService.getOrganization();
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.ORGANIZ_NULL, result.getMessage());
        }
    }

    /**
     * 获取当前用户部门及子部门id
     * @return
     */
    public static List<String> getOrganizationIds(String organId) {
        Result<List<String>> result = remoteSecuritySystemService.getOrganizationIds(organId);
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.ORGANIZ_NULL, result.getMessage());
        }
    }

    /**
     * 获取当前用户数据权限
     * @return
     */
    public static SuperbUserDataScope getUserDataScope() {
        Result<SuperbUserDataScope> result = remoteSecuritySystemService.getUserDataScope();
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.DATA_SCOPE_ORGAN, result.getMessage());
        }
    }

    /**
     * 当前登录用户权限列表
     * @return
     */
    public static List<String> permissionList() {
        Result<List<String>> result = remoteSecuritySystemService.getUserPermissionList(StpUtil.getLoginIdAsString());
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.USER_NULL, result.getMessage());
        }
    }

    /**
     * 当前登录用户角色列表
     * @return
     */
    public static List<String> roleList() {
        Result<List<String>> result = remoteSecuritySystemService.getUserRoleList(StpUtil.getLoginIdAsString());
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.USER_NULL, result.getMessage());
        }
    }

    /**
     * 字段是否存在
     * @return
     */
    public static boolean existColumn(String table, String column) {
        Result<Boolean> result = remoteSecuritySystemService.getExistColumn(table, column);
        if (result.getCode() == 200 && result.isSuccess()) {
            return result.getResult();
        } else {
            throw new SuperbException(SuperbCode.USER_NULL, result.getMessage());
        }
    }

    /**
     * 检查索引
     * @return
     */
    public static boolean checkIndex(String table, String column) {
        Result<String> result = remoteSecuritySystemService.getDatabaseIndex(table, column);
        if (result.getCode() == 200 && result.isSuccess()) {
            return StringUtils.isBlank(result.getResult());
        } else {
            throw new SuperbException(SuperbCode.USER_NULL, result.getMessage());
        }
    }

}
