package com.sohan.easy4j.exception;


import com.sohan.easy4j.enums.Easy4jHttpStatusEnum;
import com.sohan.easy4j.enums.HttpStatusEnum;
import com.sohan.enums.ExceptionCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 *
 * @author yangzongmin
 * @date 2019-07-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;
    private String msg;

    public BusinessException() {
        this(HttpStatusEnum.BAD_REQUEST.code(), HttpStatusEnum.BAD_REQUEST.msg());
    }

    public BusinessException(String msg) {
        this(HttpStatusEnum.BAD_REQUEST.code(), msg);
    }

    public BusinessException(Easy4jHttpStatusEnum httpStatusEnum) {
        this(httpStatusEnum.code(), httpStatusEnum.msg());
    }

    public BusinessException(ExceptionCodeEnum exceptionCodeEnum) {
        this(exceptionCodeEnum.getErrorCode(), exceptionCodeEnum.getMessage());
    }

    public BusinessException(Easy4jHttpStatusEnum easy4jHttpStatusEnum, Object... params) {
        this(easy4jHttpStatusEnum.code(), easy4jHttpStatusEnum.msg(), params);
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getCode(), errorCodeEnum.getMsg());
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
