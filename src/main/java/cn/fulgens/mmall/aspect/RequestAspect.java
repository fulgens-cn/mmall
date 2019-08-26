package cn.fulgens.mmall.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class RequestAspect {

    /**
     * 定义切点
     */
    @Pointcut("execution(* cn.fulgens.mmall.controller..*.*(..))")
    public void logRequestInfo() {}

    /**
     * 记录每次请求信息到日志
     */
    @Before("logRequestInfo()")
    public void logRequestInfo(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // request url
        log.info("request url = {}", request.getRequestURL());
        // request method
        log.info("request method = {}", request.getMethod());
        // request server ip
        log.info("request server ip = {}", request.getRemoteAddr());
        // request class method
        log.info("request class method = {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        // request args
        log.info("request args = {}", joinPoint.getArgs());
    }

    /**
     * 记录每次响应信息到日志
     */
    @AfterReturning(returning = "object", value = "logRequestInfo()")
    public void logServerResponse(Object object) {
        log.info("response = {}", object.toString());
    }

}
