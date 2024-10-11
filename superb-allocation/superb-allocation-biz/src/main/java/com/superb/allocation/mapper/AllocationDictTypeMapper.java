package com.superb.allocation.mapper;

import com.mybatisflex.core.BaseMapper;
import com.superb.allocation.api.dto.DictTree;
import com.superb.allocation.api.entity.AllocationDictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据字典类型;(allocation_dict_type)表数据库访问层
 * @Author: ajie
 * @CreateTime: 2024-6-27
 */
@Mapper
public interface AllocationDictTypeMapper extends BaseMapper<AllocationDictType> {

    @Select("SELECT id, name AS label, code AS `value`, status AS disabled FROM allocation_dict_type WHERE parent_id=#{parentId} ORDER BY sort")
    List<DictTree> treeTranslate(@Param("parentId") String parentId);

}