package com.superb.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.superb.system.api.entity.SystemTenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统租户管理表;(system_tenant)表数据库访问层
 * @Author: ajie
 * @CreateTime: 2024-5-11
 */
@Mapper
public interface SystemTenantMapper extends BaseMapper<SystemTenant> {

    /**
     * 获取索引
     * @param table 表
     * @param column 字段
     * @return
     */
    String getIndex(@Param("table") String table, @Param("column") String column);

    /**
     * 获取是否存在字段
     * @param table 表
     * @param column 字段
     * @return
     */
    Boolean getExistColumn(@Param("table") String table, @Param("column") String column);
}