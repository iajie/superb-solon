package com.superb.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson2.JSONObject;
import com.superb.common.core.model.Result;
import com.superb.common.redis.annotation.SuperbLock;
import com.superb.common.redis.command.RedisLockCommands;
import com.superb.common.redis.key.RedisLockKey;
import com.superb.common.redis.utils.RedisUtils;
import com.superb.common.security.filter.GlobalFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import org.noear.solon.Solon;
import org.noear.solon.annotation.*;

import java.time.Duration;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-26 15:36
 */
@SaIgnore
@Api("压力测试")
@Controller
@Mapping("test")
public class TestController {

    private static volatile int test1 = 99;
    private static volatile int test2 = 99;
    private static volatile int test3 = 99;

    @Get
    @Mapping("test1")
    @ApiOperation(value = "第一个测试方法")
    public Result<String> test() {
        RedisLockKey lockKey = new RedisLockKey("test1");
        RedisUtils.build().lock().lock(lockKey);
        if (test1 > 0) {
            test1--;
        }
        RedisUtils.build().lock().unlock(lockKey);
        System.out.println("======减完库存后,当前库存===" + test1);
        return Result.success();
    }

    @Get
    @Mapping("test3")
    @ApiOperation(value = "第一个测试方法")
    @SuperbLock("test3")
    public Result<String> test3() {
        if (test3 > 0) {
            test3--;
        }
        System.out.println("===注解模式=== 减完库存后,当前库存===" + test3);
        return Result.success();
    }

    @Get
    @Mapping("test2")
    @ApiOperation(value = "第一个测试方法2")
    public Result<JSONObject> test2() {
        RedisLockKey lockKey = new RedisLockKey("test2", Duration.ofSeconds(10), Duration.ofSeconds(30));
        RedisUtils.build().lock().tryLock(lockKey);
        if (RedisUtils.build().lock().tryLock(lockKey)) {
            if (test2 > 0) {
                test2--;
            }
            RedisUtils.build().lock().unlock(lockKey);
            System.out.println("====tryLock===减完库存后,当前库存===" + test2);
        }
        return Result.success();
    }

}
