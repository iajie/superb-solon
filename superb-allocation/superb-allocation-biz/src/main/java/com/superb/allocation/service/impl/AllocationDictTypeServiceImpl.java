package com.superb.allocation.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.solon.service.impl.ServiceImpl;
import com.superb.allocation.api.dto.DictTree;
import com.superb.allocation.api.entity.AllocationDictType;
import com.superb.allocation.mapper.AllocationDictTypeMapper;
import com.superb.allocation.service.AllocationDictTypeService;
import org.noear.solon.annotation.Component;

import java.util.List;

/**
 * 数据字典类型;(allocation_dict_type)表服务实现类
 * @Author: ajie
 * @CreateTime: 2024-6-27
 */
@Component
public class AllocationDictTypeServiceImpl extends ServiceImpl<AllocationDictTypeMapper, AllocationDictType> implements AllocationDictTypeService {
    @Override
    public List<AllocationDictType> getChildren(List<AllocationDictType> list) {
        for (AllocationDictType dictType : list) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq(AllocationDictType::getParentId, dictType.getId());
            queryWrapper.orderBy(AllocationDictType::getSort).asc();
            List<AllocationDictType> dictTypes = super.list(queryWrapper);
            if (dictTypes.size() > 0) {
                dictType.setChildren(dictTypes);
                getChildren(dictTypes);
            }
        }
        return list;
    }

    @Override
    public List<DictTree> levelDict(String parentId) {
        List<DictTree> list = this.mapper.treeTranslate(parentId);
        return buildTree(list);
    }

    private List<DictTree> buildTree(List<DictTree> list) {
        for (DictTree tree : list) {
            List<DictTree> trees = this.mapper.treeTranslate(tree.getId());
            if (trees.size() > 0) {
                tree.setChildren(trees);
                buildTree(trees);
            }
        }
        return list;
    }
}