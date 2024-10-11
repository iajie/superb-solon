package com.superb.system.service;

import com.mybatisflex.core.service.IService;
import com.superb.system.api.entity.Organization;

import java.util.List;

/**
 * 部门机构;(system_organization)表服务接口
 * @Author: ajie
 * @CreateTime: 2024-6-14
 */
public interface SystemOrganizationService extends IService<Organization> {

    void buildTree(Organization organization);

    /**
     * 获取本部门及子部门id
     * @param organizetionIds 初始集合，包含用户权限所在部门
     * @param organizetionId 所在部门id
     */
    void getIdAndSub(List<String> organizetionIds, String organizetionId);

}