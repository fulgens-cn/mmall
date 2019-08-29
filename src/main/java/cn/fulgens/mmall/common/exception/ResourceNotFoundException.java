package cn.fulgens.mmall.common.exception;

import cn.fulgens.mmall.common.ResponseCode;
import lombok.Getter;

/**
 * 请求资源不存在异常
 *
 * @author fulgens
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private Integer errCode = ResponseCode.RESOURCE_NOT_FOUND.getCode();

    public ResourceNotFoundException(Integer errCode, String message) {
        super(message);
        this.errCode = errCode;
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
