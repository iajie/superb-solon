package com.superb.common.core.exception;

import com.superb.common.core.enums.SuperbCode;
import lombok.Getter;

/**
 * superb自定义异常
 * @Author: ajie
 * @CreateTime: 2024-07-25 15:17
 */
public class SuperbException extends RuntimeException {
    @Getter
    private SuperbCode code;

    public SuperbException(SuperbCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public SuperbException(SuperbCode code, String message) {
        super(message);
        this.code = code;
    }

    public SuperbException(String message) {
        super(message);
    }

    public SuperbException(String message, Throwable cause) {
        super(message, cause);
    }

    public SuperbException(Throwable cause) {
        super(cause);
    }

    public SuperbException(SuperbCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
