package com.hotlist.mqlistener;

import com.hotlist.common.to.MessageSiteWrapper;
import com.hotlist.dao.impl.HotSiteDAOImpl;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.service.HotResourceService;
import com.hotlist.utils.HotContext;
import com.hotlist.utils.HotSpringBeanUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

@RabbitListener(queues = {"resource.refresh.queue"})
@Component
@Slf4j
public class HotResourceRefreshListener {

    @Resource
    private HotResourceService hotResourceService;

    @RabbitHandler
    public void resourceRefresh(MessageSiteWrapper siteWrapper, Message message, Channel channel) throws IOException {
        HotSiteEntity site = siteWrapper.getSite();
        String uuid = siteWrapper.getUuid();
        String refreshKey = HotSiteDAOImpl.RedisKey.resourceRefreshKey(site, HotContext.getCurrentUser());
//        String refreshKey = HotUtil.stringJoin("refreshKey", HotSiteEntity.resourceObjKey(site));
        // 分布式情况下，为了保证每个站点刷新次次数，不被多次消费刷新。所以这里需要考虑幂等情况
        // 当前消息跟rds中的uuid不匹配，直接确认消息。
        String refreshVal = HotSpringBeanUtils.stringRedisTemplate.opsForValue().get(refreshKey);
        if (Objects.isNull(refreshVal) || uuid.equals(refreshVal)) {
            try {
                hotResourceService.refreshResource(site);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                log.info("站点刷新失败：{}，error：{}", site.getSaveKey(), e.getMessage());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
                throw e;
            }
        } else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
