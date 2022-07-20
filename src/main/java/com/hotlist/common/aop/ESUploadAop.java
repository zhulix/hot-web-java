package com.hotlist.common.aop;

import com.hotlist.utils.HotContext;
import com.hotlist.utils.HotUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
public class ESUploadAop {

    @Autowired
    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private RedissonClient redissonClient;

    @Around("execution(public void com.hotlist.core.schedule.ElasticSearchUpload.doUpload())")
    public Object ESAop(ProceedingJoinPoint joinPoint) {
        String esLockKey = HotUtil.stringJoin("esLock", "upload");
        RLock lock = redissonClient.getLock(esLockKey);
        try {
            if ((lock.tryLock(30, TimeUnit.SECONDS))) {
                log.info("es job start...");
                HotContext.setDefaultUser();
                Object proceed = joinPoint.proceed();
                log.info("es job done...");
                return proceed;
            } else {
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            HotContext.removeUser();
            if (lock.isLocked()) lock.unlock();
        }
    }
}
