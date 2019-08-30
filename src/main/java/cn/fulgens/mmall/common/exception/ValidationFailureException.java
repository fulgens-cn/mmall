package cn.fulgens.mmall.common.exception;

import cn.fulgens.mmall.common.ResponseCode;
import lombok.Getter;

/**
 * 数据校验异常
 *
 * @author fulgens
 */
@Getter
public class ValidationFailureException extends RuntimeException {

    private Integer errCode = ResponseCode.ILLEGAL_ARGUMENT.getCode();

    public ValidationFailureException(Integer errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

    public ValidationFailureException(String message) {
        super(message);
    }
}
