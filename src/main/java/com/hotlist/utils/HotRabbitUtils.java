package com.hotlist.utils;

import com.hotlist.common.to.MessageSiteWrapper;
import com.hotlist.config.HotRabbitConfig;
import com.hotlist.dao.impl.HotSiteDAOImpl;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class HotRabbitUtils {

    public static RabbitTemplate rabbitTemplate;

    public static StringRedisTemplate stringRedisTemplate;

    public HotRabbitUtils(RabbitTemplate rabbitTemplate,
                          StringRedisTemplate stringRedisTemplate) {
        HotRabbitUtils.rabbitTemplate = rabbitTemplate;
        HotRabbitUtils.stringRedisTemplate = stringRedisTemplate;
    }

    public static void sendLongerDelayQueue(Object object) {
        rabbitTemplate.convertAndSend(HotRabbitConfig.RESOURCE_EXCHANGE, "resource.longer.delay", object);
    }

    /**
     * 发送立即刷新的队列
     */
    public static void sendRefreshResourceNow(MessageSiteWrapper messageSiteWrapper) {
        rabbitTemplate.convertAndSend(HotRabbitConfig.RESOURCE_EXCHANGE, "resource.refresh", messageSiteWrapper);
    }

    /**
     * 发送给延时8分钟的队列
     */
    public static void createResource(MessageSiteWrapper messageSiteWrapper, UserEntity user) {
        HotSiteEntity site = messageSiteWrapper.getSite();
        String refreshKey = HotSiteDAOImpl.RedisKey.resourceRefreshKey(site, user);

        stringRedisTemplate.opsForValue()
                .set(refreshKey, messageSiteWrapper.getUuid(), 10, TimeUnit.MINUTES);
        rabbitTemplate.convertAndSend(HotRabbitConfig.RESOURCE_EXCHANGE, "resource.create", messageSiteWrapper);
    }


}
