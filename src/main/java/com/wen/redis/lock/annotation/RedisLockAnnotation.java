package com.wen.redis.lock.annotation;

import com.wen.redis.lock.enumeration.RedisLockTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Redis 分布式锁注解
 * </p>
 *
 * @author wenjun
 * @since 2022-07-01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RedisLockAnnotation {

    /**
     * 特定参数识别，默认取第 0 个下标
     */
    int lockFiled() default 0;

    /**
     * 加锁类型
     */
    RedisLockTypeEnum typeEnum();

    /**
     * 释放时间（秒）
     */
    long lockTime() default 30;
}
