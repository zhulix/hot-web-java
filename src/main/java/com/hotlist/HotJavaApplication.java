package com.hotlist;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRabbit
@EnableRetry
@ConfigurationPropertiesScan({"com.hotlist.config"})
public class HotJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotJavaApplication.class, args);
    }

}
