package cn.fulgens.mmall.common.exception;

import cn.fulgens.mmall.common.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author fulgens
 */
@Slf4j
@Component
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ServerResponse handleResourceNotFoundException(Exception e) {
        ResourceNotFoundException exception = (ResourceNotFoundException) e;
        return ServerResponse.errorWithMsg(exception.getErrCode(), exception.getMessage());
    }

    @ExceptionHandler(ValidationFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerResponse handleValidationFailureException(Exception e) {
        ValidationFailureException exception = (ValidationFailureException) e;
        return ServerResponse.errorWithMsg(exception.getErrCode(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServerResponse handleUnExceptedException(Exception e) {
        log.error("GlobalExceptionHandler catch unExcepted exception, msg: {}", e);
        return ServerResponse.errorWithMsg("服务器内部异常");
    }
}
