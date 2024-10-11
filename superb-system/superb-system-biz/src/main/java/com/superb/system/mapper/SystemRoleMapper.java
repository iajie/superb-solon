package com.superb.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.superb.system.api.entity.SystemRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统角色表;(system_role)表数据库访问层
 * @Author: ajie
 * @CreateTime: 2024-5-15
 */
@Mapper
public interface SystemRoleMapper extends BaseMapper<SystemRole> {

    String listByUserId(@Param("userId") String userId, @Param("organId") String organId);
}