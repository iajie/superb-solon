package com.superb.allocation.service;

import com.mybatisflex.core.service.IService;
import com.superb.allocation.api.dto.DictTree;
import com.superb.allocation.api.entity.AllocationDictType;

import java.util.List;

/**
 * 数据字典类型;(allocation_dict_type)表服务接口
 * @Author: ajie
 * @CreateTime: 2024-6-27
 */
public interface AllocationDictTypeService extends IService<AllocationDictType> {

    List<AllocationDictType> getChildren(List<AllocationDictType> list);

    List<DictTree> levelDict(String parentId);
}