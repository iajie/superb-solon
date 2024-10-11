package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.mask.MaskManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.superb.common.core.model.Result;
import com.superb.common.database.vo.BatchId;
import com.superb.common.database.vo.PageParams;
import com.superb.common.redis.key.KeyType;
import com.superb.common.redis.key.RedisKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.utils.PasswordUtils;
import com.superb.common.utils.StringUtils;
import com.superb.system.api.dto.Options;
import com.superb.system.api.entity.SystemUser;
import com.superb.system.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统用户表;(system_user)表控制层
 *
 * @Author: ajie
 * @CreateTime: 2024-5-7
 */
@Valid
@Controller
@Api("系统用户表接口")
@Mapping("/systemUser")
public class SystemUserController {

    @Inject
    private SystemUserService systemUserService;

    @Mapping(value = "pageQuery", method = MethodType.POST)
    @ApiOperation(value = "分页查询", notes = "权限预留<br>")
    public Result<Page<SystemUser>> pageQuery(@Body PageParams<SystemUser> params) {
        SystemUser user = params.getParams();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like(SystemUser::getNickname, user.getNickname(), StringUtils.isNotBlank(user.getNickname()))
                .eq(SystemUser::getOrganId, user.getOrganId(), StringUtils.isNotBlank(user.getOrganId()))
                .like(SystemUser::getPhoneNumber, user.getPhoneNumber(), StringUtils.isNotBlank(user.getPhoneNumber()));
        Page<SystemUser> result = systemUserService.page(params.getPage(), queryWrapper);
        return Result.success(result);
    }

    @Mapping(value = "options", method = MethodType.GET)
    @ApiOperation(value = "用户下拉列表", notes = "权限预留<br>")
    public Result<List<Options>> options(@Param String organId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("id AS `value`", "nickname AS `label`");
        queryWrapper.eq(SystemUser::getOrganId, organId, StringUtils.isNotBlank(organId));
        return Result.success(systemUserService.listAs(queryWrapper, Options.class));
    }

    @Mapping(value = "insert", method = MethodType.POST)
    @ApiOperation(value = "新增数据", notes = "权限预留<br>")
    public Result<Boolean> insert(@Body @Validated SystemUser systemUser) {
        if (systemUserService.checkUsername(systemUser.getUsername())) {
            return Result.error("当前登录名已重复！");
        }
        if (systemUserService.checkPhoneNumber(systemUser.getPhoneNumber())) {
            return Result.error("当前手机号已注册！");
        }
        if (systemUserService.checkEmail(systemUser.getEmail())) {
            return Result.error("当前手机号已注册！");
        }
        // 16位随机数，作为登录密码的秘钥
        String salt = RandomUtil.randomString(PasswordUtils.BASE_SALT, 16);
        systemUser.setPassword(PasswordUtils.encrypt(salt, systemUser.getPassword()));
        systemUser.setSalt(salt);
        if (systemUserService.save(systemUser)) {
            return Result.success("用户新增成功！");
        }
        return Result.error("用户添加失败！");
    }

    @Mapping(value = "update", method = MethodType.POST)
    @ApiOperation(value = "根据id修改数据", notes = "权限预留<br>")
    public Result<SystemUser> update(@Body SystemUser systemUser) {
        systemUser.setPassword(null);
        systemUser.setSalt(null);
        if (systemUserService.updateById(systemUser)) {
            RedisKey key = new RedisKey(KeyType.PER, "userInfo:" + systemUser.getId());
            RedisKey key2 = new RedisKey(KeyType.TIME, "user:" + systemUser.getId());
            RedisKey key3 = new RedisKey(KeyType.TIME, "userCache:" + systemUser.getId());
            RedisUtils.build().hash().del(key, "info");
            RedisUtils.build().del(key2, key3);
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delete/{id}", method = MethodType.GET)
    @ApiOperation(value = "根据id删除", notes = "权限预留<br>")
    public Result<Boolean> deleteById(@Path String id) {
        if (systemUserService.removeById(id)) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delQuery", method = MethodType.GET)
    @SaCheckPermission({"system:user:select", "system:user:delete"})
    @ApiOperation(value = "回收站", notes = "权限: <br>同时拥有system:user:select、system:user:delete")
    public Result<List<SystemUser>> delQuery() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("del", 1);
        List<SystemUser> result = LogicDeleteManager.execWithoutLogicDelete(() -> systemUserService.list(queryWrapper));
        return Result.success(result);
    }

    @Mapping(value = "recovery", method = MethodType.POST)
    @SaCheckPermission({"system:user:update", "system:user:delete"})
    @ApiOperation(value = "根据id恢复删除的数据", notes = "权限: <br>同时拥有system:user:update、system:user:delete")
    public Result<Boolean> recovery(@Body @Validated BatchId batchId) {
        List<SystemUser> list = new ArrayList<>();
        batchId.getId().forEach(i -> list.add(new SystemUser(i, 0)));
        if (LogicDeleteManager.execWithoutLogicDelete(() -> systemUserService.updateBatch(list))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "delete", method = MethodType.POST)
    @SaCheckPermission("system:user:delete")
    @ApiOperation(value = "根据id彻底删除", notes = "权限: system:user:delete<br><font style='color: red'>真删除，数据无法恢复</font>")
    public Result<Boolean> delete(@Body @Validated BatchId batchId) {
        if (LogicDeleteManager.execWithoutLogicDelete(() -> systemUserService.removeByIds(batchId.getId()))) {
            return Result.success();
        }
        return Result.error();
    }

    @Mapping(value = "info/{id}", method = MethodType.GET)
    @ApiOperation(value = "根据id获取用户信息")
    @ApiImplicitParam(name = "id", value = "用户id", required = true)
    public Result<SystemUser> info(@Path String id) {
        SystemUser user = MaskManager.execWithoutMask(() -> systemUserService.getInfoById(id));
        return Result.success(user);
    }

    @Mapping(value = "updatePassword", method = MethodType.POST)
    @SaCheckPermission({"system:user:update"})
    @ApiOperation(value = "根据id修改密码", notes = "权限： system:user:update<br>")
    public Result<SystemUser> updatePassword(@Body SystemUser systemUser) {
        // 16位随机数，作为登录密码的秘钥
        String salt = RandomUtil.randomString(PasswordUtils.BASE_SALT, 16);
        systemUser.setPassword(PasswordUtils.encrypt(salt, systemUser.getPassword()));
        systemUser.setSalt(salt);
        if (systemUserService.updateById(systemUser)) {
            return Result.success();
        }
        return Result.error();
    }
}