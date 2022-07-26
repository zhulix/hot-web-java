package com.hotlist.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class HotRabbitConfig {

    @Value("${mq-resource.timeout-minute}")
    private Long timeout;

    // 任务调度job
    public static String RESOURCE_EXCHANGE = "resource-event-exchange";
    public static String resource_refresh_queue = "resource.refresh.queue";
    public static String resource_refresh_delay_queue = "resource.refresh.delay.queue";

    // 当资源解析失败后，把消息丢到这个更长时间的队列里面（有的站点8分钟一次请求可能会频繁访问）
    public static String resource_refresh_longer_delay_queue = "resource.refresh.longer.delay.queue";
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Exchange resourceEventExchange() {
        return new TopicExchange(RESOURCE_EXCHANGE, true, false);
    }
    @Bean
    public Queue resourceRefreshQueue() {
        return new Queue(resource_refresh_queue, true, false, false);
    }
    @Bean
    public Queue resourceRefreshDelayQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-routing-key", "resource.refresh");
        args.put("x-dead-letter-exchange", RESOURCE_EXCHANGE);
        // 8分钟刷新一次资源
        if (Objects.isNull(timeout)) args.put("x-message-ttl", 1000 * 3);
        else args.put("x-message-ttl", 1000 * 60 * timeout);
        return new Queue(resource_refresh_delay_queue, true, false, false, args);
    }
    @Bean
    public Queue resourceRefreshLongerDelayQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-routing-key", "resource.refresh");
        args.put("x-dead-letter-exchange", RESOURCE_EXCHANGE);
        args.put("x-message-ttl", 1000 * 60 * 20);
        return new Queue(resource_refresh_longer_delay_queue, true, false, false, args);
    }
    @Bean
    public Binding resourceRefreshQueueBinding() {
        return new Binding(resource_refresh_queue, Binding.DestinationType.QUEUE, RESOURCE_EXCHANGE, "resource.refresh", null);
    }
    @Bean
    public Binding resourceRefreshDelayQueueBinding() {
        return new Binding(resource_refresh_delay_queue, Binding.DestinationType.QUEUE, RESOURCE_EXCHANGE, "resource.create", null);
    }
    @Bean
    public Binding resourceRefreshLongerDelayQueueBinding() {
        return new Binding(resource_refresh_longer_delay_queue, Binding.DestinationType.QUEUE, RESOURCE_EXCHANGE, "resource.longer.delay", null);
    }
    // 任务调度job完成
}
