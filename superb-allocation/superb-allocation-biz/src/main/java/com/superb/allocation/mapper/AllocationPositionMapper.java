package com.superb.allocation.mapper;

import com.mybatisflex.core.BaseMapper;
import com.superb.allocation.api.entity.AllocationPosition;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行政区划;(allocation_position)表数据库访问层
 * @Author: ajie
 * @CreateTime: 2024-7-1
 */
@Mapper
public interface AllocationPositionMapper extends BaseMapper<AllocationPosition> {

}