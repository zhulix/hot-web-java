package com.hotlist.utils;

import com.hotlist.common.to.MessageSiteWrapper;
import com.hotlist.config.HotRabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HotRabbitUtils {

    public static RabbitTemplate rabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        HotRabbitUtils.rabbitTemplate = rabbitTemplate;
    }

    public static void sendLongerDelayQueue(Object object) {
        rabbitTemplate.convertAndSend(HotRabbitConfig.RESOURCE_EXCHANGE, "resource.longer.delay", object);
    }

    public static void createResource(MessageSiteWrapper messageSiteWrapper) {
        rabbitTemplate.convertAndSend(HotRabbitConfig.RESOURCE_EXCHANGE, "resource.create", messageSiteWrapper);
    }



}
