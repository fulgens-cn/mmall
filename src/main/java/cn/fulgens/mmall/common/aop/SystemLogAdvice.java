package cn.fulgens.mmall.common.aop;

import cn.fulgens.mmall.common.utils.IpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;

/**
 * Advice for logging system request info.
 *
 * @author fulgens
 */
@Slf4j
@Aspect
@Component
public class SystemLogAdvice {

    @Pointcut("within(cn.fulgens.mmall.controller..*) && @annotation(SystemLog)")
    public void systemLogPointcut() {

    }

    @Around(value = "systemLogPointcut()")
    public Object logRequestInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        Object result;
        if (isIgnoreLogRequestInfo(joinPoint)) {
            result = joinPoint.proceed();
        } else {
            log.debug("request client ip addr: {}", IpUtil.getIpAddr(request));
            log.debug("request url: {}, method: {}, params: {}", request.getRequestURI(), request.getMethod(), joinPoint.getArgs());
            log.debug("request class and method: {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            Instant startTime = Instant.now();
            result = joinPoint.proceed();
            Instant endTime = Instant.now();
            log.debug("current request elapsed {} ms, response: {}", Duration.between(startTime, endTime).toMillis(), JSON.toJSON(result));
        }
        return result;
    }

    private boolean isIgnoreLogRequestInfo(ProceedingJoinPoint joinPoint) {
        SystemLog systemLog = joinPoint.getTarget().getClass().getAnnotation(SystemLog.class);
        return systemLog.ignore();
    }
}
