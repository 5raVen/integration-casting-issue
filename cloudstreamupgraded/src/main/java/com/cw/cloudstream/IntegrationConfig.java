package com.cw.cloudstream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;


import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Configuration
//@EnableBinding( OrderProcessor.class )
public class IntegrationConfig {

    //private static final String PROCESS_ORDER_REQUEST_CHANNEL = "processOrderRequestChannel";
    private static final String PROCESS_ORDER_REQUEST_CHANNEL = "processOrderRequestListener-out-0";

//    @StreamListener(OrderProcessor.ORDER_REQUEST_INPUT_CHANNEL)
//    @Output(PROCESS_ORDER_REQUEST_CHANNEL)
//    public Message<String> processOrderRequestListener(final Message<String> orderMessage) throws IOException, SQLException {
//        return orderMessage;
//    }

    @Bean
    public Function<Message<OrderMessage>, Message<OrderMessage>> processOrderRequestListener() {
        return orderMessage -> {return orderMessage;};
    }

    @Bean(name = PROCESS_ORDER_REQUEST_CHANNEL)
    public MessageChannel processOrderRequestChannel() {
        final Executor executor = new ThreadPoolExecutor(10,
                10,
                10,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>());
        return new ExecutorChannel(executor);
    }


    @Bean
    public IntegrationFlow processOrderRequestFlow() {
        return IntegrationFlow.from(PROCESS_ORDER_REQUEST_CHANNEL)
              //java.lang.ClassCastException: class [B cannot be cast to class com.cw.cloudstream.OrderMessage ([B is in module java.base of loader 'bootstrap'; com.cw.cloudstream.OrderMessage is in unnamed module of loader 'app')
              // .<OrderMessage>handle((payload, headers) -> {
                .<byte[]>handle((payload, headers) -> {
                    System.out.println("Processing order: " + payload);
                    if(!payload.equals("Test message after retries"))
                        throw new RuntimeException("Failed to process order");
                    else
                        return MessageBuilder.withPayload(payload).setHeader("source", "ProcessFlow").build();
                })
                .channel(GenericFlow.ERROR_CHANNEL)
                .get();

    }
}
