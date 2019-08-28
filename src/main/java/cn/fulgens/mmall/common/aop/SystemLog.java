package cn.fulgens.mmall.common.aop;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author fulgens
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {

    String value();

    boolean ignore() default false;
}
