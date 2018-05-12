package cn.fulgens.mmall.aspect;

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

@Aspect
@Component
public class RequestAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequestAspect.class);

    // 定义切点
    @Pointcut("execution(* cn.fulgens.mmall.controller.*(..))")
    public void log() {}

    /**
     * 记录每次请求信息到日志
     */
    @Before("log()")
    public void logRequestInfo(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // request url
        logger.info("request url = {}", request.getRequestURL());
        // request method
        logger.info("request method = {}", request.getMethod());
        // request server ip
        logger.info("request server ip = {}", request.getRemoteAddr());
        // request class method
        logger.info("request class method = {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        // request args
        logger.info("request args = {}", joinPoint.getArgs());
    }

    /**
     * 记录每次响应信息到日志
     */
    @AfterReturning(returning = "object", value = "log()")
    public void logServerResponse(Object object) {
        logger.info("response = {}", object.toString());
    }

}
