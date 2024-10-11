package com.superb.common.core.enums;

import com.superb.common.core.exception.SuperbException;
import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * @Author: ajie
 * @CreateTime: 2024-06-19 12:34
 */
@AllArgsConstructor
public enum DeviceType {

    PC_ADMIN,
    PC_GATHER,
    WX,
    PAY,
    APP;

    public static DeviceType of(String deviceType) {
        try {
            return DeviceType.valueOf(deviceType);
        } catch (Exception e) {
            throw new SuperbException(SuperbCode.REQUEST_HEADERS_ERROR, "非法客户端: " + Arrays.toString(DeviceType.values()));
        }
    }
}
