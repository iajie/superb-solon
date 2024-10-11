package com.superb.common.database.listener;

import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.annotation.InsertListener;
import com.superb.common.database.entity.BaseEntity;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;

import java.util.Date;

/**
 * 新增数据自动填充监听器
 * @Author: ajie
 * @CreateTime: 2024-07-26 11:18
 */
public class SuperbInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        if(entity instanceof BaseEntity baseEntity) {
            if (StringUtils.isNotBlank(HeadersUtils.getAuthentication())) {
                baseEntity.setCreateBy(StpUtil.getLoginIdAsString());
                baseEntity.setOrganId(HeadersUtils.getOrganizationId());
            } else {
                baseEntity.setCreateBy("NOT_LOGIN");
                baseEntity.setOrganId("NOT_LOGIN");
            }
            baseEntity.setDel(0);
            baseEntity.setCreateTime(new Date());
            if (StringUtils.isBlank(baseEntity.getTenantId())) {
                baseEntity.setTenantId(HeadersUtils.getTenantId());
            }
        }
    }
}
