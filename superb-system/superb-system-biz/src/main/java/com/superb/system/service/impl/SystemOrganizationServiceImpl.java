package com.superb.system.service.impl;

import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.system.api.entity.Organization;
import com.superb.system.mapper.SystemOrganizationMapper;
import com.superb.system.service.SystemOrganizationService;
import org.noear.solon.annotation.Component;

import java.util.List;

/**
 * 部门机构;(system_organization)表服务实现类
 *
 * @Author: ajie
 * @CreateTime: 2024-6-14
 */
@Component
public class SystemOrganizationServiceImpl extends ServiceImpl<SystemOrganizationMapper, Organization> implements SystemOrganizationService {
    @Override
    public void buildTree(Organization parentOrganization) {
        QueryChain<Organization> queryChain = super.queryChain();
        queryChain.eq(Organization::getParentId, parentOrganization.getId()).orderBy(Organization::getSort).asc();
        List<Organization> organizations = super.list(queryChain);
        if (organizations.size() > 0) {
            parentOrganization.setChildren(organizations);
            for (Organization organization : organizations) {
                buildTree(organization);
            }
        }
    }

    @Override
    public void getIdAndSub(List<String> organizationIds, String organizationId) {
        QueryChain<Organization> lqw = super.queryChain();
        lqw.select("id").eq(Organization::getParentId, organizationId).orderBy(Organization::getSort).asc();
        List<String> ids = super.listAs(lqw, String.class);
        if (ids.size() > 0) {
            organizationIds.addAll(ids);
            for (String id : ids) {
                getIdAndSub(organizationIds, id);
            }
        }
    }

}