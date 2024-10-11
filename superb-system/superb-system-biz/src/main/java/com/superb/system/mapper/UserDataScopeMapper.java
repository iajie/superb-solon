package com.superb.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.superb.system.api.dto.DataScope;
import com.superb.system.api.entity.UserDataScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户数据权限范围;(system_user_data_scope)表数据库访问层
 * @Author: ajie
 * @CreateTime: 2024-7-3
 */
@Mapper
public interface UserDataScopeMapper extends BaseMapper<UserDataScope> {

    /**
     * 获取数据权限列表
     * @param userId 用户id
     * @return
     */
    @Select("SELECT o.id AS `organ_id`, o.`name` AS organ_name, uds.is_main, uds.data_scope_type FROM system_organization AS o INNER JOIN system_user_data_scope AS uds ON o.id=uds.organ_id WHERE uds.user_id=#{userId} AND uds.enable=0 ORDER BY uds.is_main DESC")
    List<DataScope> dataScopes(@Param("userId") String userId);
}