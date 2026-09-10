
package com.ghost.frame.common;

import com.ghost.frame.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 全局异常处理
     *
     * @param e Exception异常
     * @return 响应
     */
    @ExceptionHandler(Exception.class)
    public Response<?> handleException(Exception e) {
        log.error("全局异常处理程序: ", e);
        return Response.error(e.getMessage());
    }

    /**
     * 参数校验异常处理
     * @param e MethodArgumentNotValidException异常
     * @return 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常: ", e);
        return Response.error(e.getBindingResult().getFieldError().getDefaultMessage());
    }
    
    /**
     * 业务异常处理
     * @param e BusinessException异常
     * @return 响应
     */
    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: ", e);
        return Response.error(e.getCode(), e.getMessage());
    }

}
