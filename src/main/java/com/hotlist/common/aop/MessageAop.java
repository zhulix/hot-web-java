package com.hotlist.common.aop;

import com.hotlist.common.to.MessageSiteWrapper;
import com.hotlist.utils.HotContext;
import com.hotlist.utils.HotRabbitUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class MessageAop {

    @Around("execution(public void com.hotlist.mqlistener.HotResourceRefreshListener.resourceRefresh(..))")
    public Object resourceRefreshAround(ProceedingJoinPoint joinPoint) {
        // 放个默认值
        HotContext.setDefaultUser();
        Object[] args = joinPoint.getArgs();
        try {
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            MessageSiteWrapper arg = (MessageSiteWrapper) args[0];
            HotRabbitUtils.sendLongerDelayQueue(arg);
            log.error("资源刷新失败：{}", arg.getSite().getSaveKey());
            return null;
        } finally {
            HotContext.removeUser();
        }
    }

}
