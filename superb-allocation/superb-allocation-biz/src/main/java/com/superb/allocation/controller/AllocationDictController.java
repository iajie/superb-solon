package com.superb.allocation.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.allocation.api.dto.Dict;
import com.superb.allocation.api.dto.DictTree;
import com.superb.allocation.api.entity.AllocationDict;
import com.superb.allocation.api.entity.AllocationDictType;
import com.superb.allocation.service.AllocationDictService;
import com.superb.allocation.service.AllocationDictTypeService;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import com.superb.common.database.vo.PageParams;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 数据字典;(allocation_dict)表控制层
 * @Author: ajie
 * @CreateTime: 2024-6-18
 */
@Valid
@Controller
@SuperbDataScope
@Api("数据字典接口")
@Mapping("/dict")
public class AllocationDictController {

    @Inject
    private AllocationDictService dictService;
    @Inject
    private AllocationDictTypeService dictTypeService;

    @Mapping(value = "type/pageQuery", method = MethodType.POST)
    @SaCheckPermission("allocation:dict:manage")
    @ApiOperation(value = "字典分类分页查询", notes = "权限: allocation:dict:manage<br>")
    public Result<Page<AllocationDictType>> pageQuery(@Body PageParams<AllocationDictType> pageParams) {
        AllocationDictType params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(AllocationDictType::getName, params.getName(), StringUtils.isNotBlank(params.getName()));
        queryWrapper.like(AllocationDictType::getCode, params.getCode(), StringUtils.isNotBlank(params.getCode()));
        if (StringUtils.isNotEmpty(params.getType())) {
            if (params.getType() == 1) {
                queryWrapper.isNull(AllocationDictType::getParentId);
            }
            queryWrapper.eq(AllocationDictType::getType, params.getType(), StringUtils.isNotBlank(params.getType()));
        }
        queryWrapper.orderBy(AllocationDictType::getSort).asc();
        Page<AllocationDictType> result = dictTypeService.page(pageParams.getPage(), queryWrapper);
        if (params.getType() == 1) {
            result.setRecords(dictTypeService.getChildren(result.getRecords()));
        }
        return Result.success(result);
    }

    @Mapping(value = "type/dictQuery", method = MethodType.POST)
    @ApiOperation(value = "根据字典分类类型获取字典分类列表", notes = "权限: allocation:dict:manage<br>")
    @SaCheckPermission("allocation:dict:manage")
    public Result<Page<AllocationDict>> dictQuery(@Body PageParams<AllocationDict> pageParams) {
        AllocationDict params = pageParams.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(AllocationDict::getDictType, params.getDictType())
                .like(AllocationDict::getName, params.getName(), StringUtils.isNotBlank(params.getName()))
                .eq(AllocationDict::getStatus, params.getStatus(), StringUtils.isNotBlank(params.getStatus()))
                .orderBy(AllocationDict::getSort).asc();
        return Result.success(dictService.page(pageParams.getPage(), queryWrapper));
    }

    @Mapping(value = "type/insert", method = MethodType.POST)
    @SaCheckPermission("allocation:dict:manage")
    @ApiOperation(value = "新增字典分类信息", notes = "权限: allocation:dict:manage<br>")
    public Result<Boolean> insertType(@Body @Validated AllocationDictType allocationDict) {
        long count = dictTypeService.count(QueryWrapper.create()
                .eq(AllocationDictType::getCode, allocationDict.getCode()));
        if (count > 0) {
            return Result.error("该字典类型已存在相同配置值");
        }
        if (dictTypeService.save(allocationDict)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "type/update", method = MethodType.POST)
    @SaCheckPermission("allocation:dict:manage")
    @ApiOperation(value = "修改字典分类信息", notes = "权限: allocation:dict:manage<br>")
    public Result<Boolean> updateType(@Body @Validated AllocationDictType allocationDict) {
        if (dictTypeService.updateById(allocationDict)) {
            RedisKey redisKey = new RedisKey(KeyType.PER, "allocation:TREE_DICT");
            RedisUtils.build().hash().del(redisKey, allocationDict.getCode());
            RedisKey redisKey2 = new RedisKey(KeyType.PER, "allocation:DICT");
            RedisUtils.build().hash().del(redisKey2, allocationDict.getCode());
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @SaCheckPermission("allocation:dict:manage")
    @ApiOperation(value = "新增字典", notes = "权限: allocation:dict:manage<br>")
    public Result<Boolean> insert(@Body @Validated AllocationDict allocationDict) {
        long count = dictService.count(QueryWrapper.create()
                .eq(AllocationDict::getDictType, allocationDict.getDictType())
                .eq(AllocationDict::getCode, allocationDict.getCode()));
        if (count > 0) {
            return Result.error("该字典类型已存在相同配置值");
        }
        if (dictService.save(allocationDict)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "update", method = MethodType.POST)
    @SaCheckPermission("allocation:dict:manage")
    @ApiOperation(value = "修改字典", notes = "权限: allocation:dict:manage<br>")
    public Result<Boolean> update(@Body @Validated AllocationDict allocationDict) {
        if (dictService.updateById(allocationDict)) {
            RedisKey redisKey = new RedisKey(KeyType.PER, "allocation:DICT");
            RedisUtils.build().hash().del(redisKey, allocationDict.getDictType());
            return Result.success();
        }
        return Result.error();
    }


    @Mapping(value = "translate/{dictType}", method = MethodType.GET)
    @ApiOperation(value = "普通字典翻译")
    public Result<List<Dict>> translate(@Path String dictType) {
        RedisKey redisKey = new RedisKey(KeyType.PER, "allocation:DICT");
        List<Dict> list = RedisUtils.build().hash().getNullSet(redisKey, dictType, () -> {
            QueryWrapper queryWrapper = QueryWrapper.create().eq(AllocationDict::getDictType, dictType);
            queryWrapper.orderBy(AllocationDict::getSort).asc();
            queryWrapper.select("name AS label", "code AS value", "type", "status AS disabled");
            return dictService.listAs(queryWrapper, Dict.class);
        }, util -> util.hash().getArray(redisKey, dictType, Dict.class));
        return Result.success(list);
    }

    @Mapping(value = "translate/tree/{dictType}", method = MethodType.GET)
    @ApiOperation(value = "树型字典翻译")
    public Result<List<DictTree>> treeTranslate(@Path String dictType) {
        RedisKey redisKey = new RedisKey(KeyType.PER, "allocation:TREE_DICT");
        List<DictTree> list = RedisUtils.build().hash().getNullSet(redisKey, dictType, () -> {
            // 获得本级字典
            AllocationDictType info = dictTypeService.getOne(QueryWrapper.create().eq(AllocationDictType::getCode, dictType));
            // 得到子级字典
            return dictTypeService.levelDict(info.getId());
        }, util -> util.hash().getArray(redisKey, dictType, DictTree.class));
        return Result.success(list);
    }
}