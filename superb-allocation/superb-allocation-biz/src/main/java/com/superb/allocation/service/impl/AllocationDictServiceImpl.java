package com.superb.allocation.service.impl;

import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.allocation.api.entity.AllocationDict;
import com.superb.allocation.mapper.AllocationDictMapper;
import com.superb.allocation.service.AllocationDictService;
import org.noear.solon.annotation.Component;

/**
 * 数据字典;(allocation_dict)表服务实现类
 * @Author: ajie
 * @CreateTime: 2024-6-18
 */
@Component
public class AllocationDictServiceImpl extends ServiceImpl<AllocationDictMapper, AllocationDict> implements AllocationDictService {

}