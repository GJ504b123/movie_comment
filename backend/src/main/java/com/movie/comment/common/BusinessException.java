package com.movie.comment.common;

/**
 * 业务异常——在 service 层抛出，全局异常处理器统一转为 Result
 */
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}
