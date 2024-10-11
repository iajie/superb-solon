package com.superb.allocation.mapper;

import com.mybatisflex.core.BaseMapper;
import com.superb.allocation.api.entity.AllocationDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典;(allocation_dict)表数据库访问层
 * @Author: ajie
 * @CreateTime: 2024-6-18
 */
@Mapper
public interface AllocationDictMapper extends BaseMapper<AllocationDict> {

}