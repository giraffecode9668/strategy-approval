package com.giraffe.utils;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;
    private String msg;

    public BusinessException(String msg) {
        this(500, msg);
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(Integer code, String msg, Object... params) {
        super(msg);
        this.code = code;
        this.msg = params != null && params.length > 0 ? String.format(msg, params) : msg;
    }

}
