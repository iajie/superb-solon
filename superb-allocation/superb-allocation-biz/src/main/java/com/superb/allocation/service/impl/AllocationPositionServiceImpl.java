package com.superb.allocation.service.impl;

import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.allocation.api.entity.AllocationPosition;
import com.superb.allocation.mapper.AllocationPositionMapper;
import com.superb.allocation.service.AllocationPositionService;
import org.noear.solon.annotation.Component;

/**
 * 行政区划;(allocation_position)表服务实现类
 * @Author: ajie
 * @CreateTime: 2024-7-1
 */
@Component
public class AllocationPositionServiceImpl extends ServiceImpl<AllocationPositionMapper, AllocationPosition> implements AllocationPositionService {

}