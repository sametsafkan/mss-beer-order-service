package com.sametsafkan.beer.order.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import static org.springframework.jms.support.converter.MessageType.TEXT;

@Configuration
public class JmsConfig {

    public static final String MY_QUEUE = "my-hello-world";
    public static final String SEND_AND_RECEIVE_QUEUE = "send-and-receive-queue";
    public static final String VALIDATE_ORDER_QUEUE = "validate-order";
    public static final String VALIDATE_ORDER_RESULT_QUEUE = "validate-order-result";

    @Bean
    public MessageConverter converter(){
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
