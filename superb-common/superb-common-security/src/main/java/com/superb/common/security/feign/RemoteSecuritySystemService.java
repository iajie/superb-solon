package com.superb.common.security.feign;

import com.superb.common.core.model.Result;
import com.superb.common.security.entity.Organization;
import com.superb.common.security.entity.SuperbTenant;
import com.superb.common.security.entity.SuperbUser;
import com.superb.common.security.entity.SuperbUserDataScope;
import com.superb.common.security.filter.NamiFilter;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.cloud.annotation.CloudBreaker;

import java.util.List;

/**
 * 使用令牌桶限流，在全局中捕获
 * @Author: ajie
 * @CreateTime: 2024-07-26 09:19
 */
@CloudBreaker("remoteSecuritySystemService")
@NamiClient(name = "superb-system-biz", path = "/rpc/security/")
public interface RemoteSecuritySystemService extends NamiFilter {

    /**
     * 获取租户信息
     *
     * @return
     */
    Result<SuperbTenant> getTenantInfo();

    /**
     * 通过用户ID获取用户信息
     *
     * @param id
     * @return
     */
    Result<SuperbUser> getUserById(String id);

    /**
     * 通用用户ID获取用户角色列表
     *
     * @param id
     * @return
     */
    Result<List<String>> getUserRoleList(String id);

    /**
     * 通用用户ID获取用户权限列表
     *
     * @param userId
     * @return
     */
    Result<List<String>> getUserPermissionList(String userId);

    /**
     * 获取当前用户部门
     *
     * @return
     */
    Result<Organization> getOrganization();

    /**
     * 获取当前用户部门
     *
     * @return
     */
    Result<List<String>> getOrganizationIds(String organId);

    /**
     * 获取当前用户的数据权限范围
     *
     * @return
     */
    Result<SuperbUserDataScope> getUserDataScope();

    /**
     * 远程获取表字段是否存在索引
     *
     * @param table
     * @param column
     * @return
     */
    Result<String> getDatabaseIndex(String table, String column);

    /**
     * 远程获取表字段是否存在字段
     *
     * @param table
     * @param column
     * @return
     */
    Result<Boolean> getExistColumn(String table, String column);

}
