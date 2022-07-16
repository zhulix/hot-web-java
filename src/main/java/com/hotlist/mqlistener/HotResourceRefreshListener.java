package com.hotlist.mqlistener;

import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.HotResourceService;
import com.hotlist.utils.HotContext;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@RabbitListener(queues = {"resource.refresh.queue"})
@Component
@Slf4j
public class HotResourceRefreshListener {

    @Resource
    private HotResourceService hotResourceService;

    @RabbitHandler
    public void resourceRefresh(HotSiteEntity site, Message message, Channel channel) throws IOException {
        try {
            log.info("站点刷新：{}", site.getSaveKey());
            HotContext.setDefaultUser();
            hotResourceService.refreshResource(site);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.info("站点刷新失败：{}，error：{}", site.getSaveKey(), e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

}
