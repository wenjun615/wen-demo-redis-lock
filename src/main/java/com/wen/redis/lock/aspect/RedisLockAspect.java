package com.wen.redis.lock.aspect;

import com.wen.redis.lock.annotation.RedisLockAnnotation;
import com.wen.redis.lock.enumeration.RedisLockTypeEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Redis 分布式锁切面
 * </p>
 *
 * @author wenjun
 * @since 2022-07-01
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class RedisLockAspect {

    private final RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.wen.redis.lock.annotation.RedisLockAnnotation)")
    public void redisLockPointcut() {
    }

    @Around("redisLockPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = resolveMethod(joinPoint);
        RedisLockAnnotation annotation = method.getAnnotation(RedisLockAnnotation.class);
        if (Objects.isNull(annotation)) {
            return joinPoint.proceed();
        }
        Object[] params = joinPoint.getArgs();
        String uniqueKey = params[annotation.lockFiled()].toString();
        RedisLockTypeEnum typeEnum = annotation.typeEnum();
        String businessKey = typeEnum.getUniqueKey(uniqueKey);
        String uniqueValue = UUID.randomUUID().toString();
        Object result = null;
        try {
            boolean success = redisTemplate.opsForValue().setIfAbsent(businessKey, uniqueValue);
            if (!success) {
                throw new Exception("当前存在其他成员正在操作");
            }
            redisTemplate.expire(businessKey, annotation.lockTime(), TimeUnit.SECONDS);
            log.info("添加锁：" + businessKey);
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            redisTemplate.delete(businessKey);
            log.info("释放锁：" + businessKey);
        }
        return result;
    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
