package com.hotlist.common.aop;

import com.hotlist.utils.HotContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ESUploadAop {

    @Around("execution(public void com.hotlist.core.schedule.ElasticSearchUpload.doUpload())")
    public void ESAop(ProceedingJoinPoint joinPoint) {
        try {
            HotContext.setDefaultUser();
            joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            HotContext.removeUser();
        }
    }
}
