package com.wen.redis.lock.controller;

import com.wen.redis.lock.annotation.RedisLockAnnotation;
import com.wen.redis.lock.enumeration.RedisLockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * null.
 * </p>
 *
 * @author wenjun
 * @since 2022-07-01
 */
@Slf4j
@RestController
public class RedisLockController {

    @GetMapping("/test")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.ONE, lockTime = 3)
    public void test(@RequestParam Long userId) {
        try {
            log.info("睡眠执行前");
            Thread.sleep(10000);
            log.info("睡眠执行后");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
