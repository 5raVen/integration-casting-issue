package com.cw.cloudstream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;


import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableBinding( OrderProcessor.class )
public class IntegrationConfig {

    private static final String PROCESS_ORDER_REQUEST_CHANNEL = "processOrderRequestChannel";

    @StreamListener(OrderProcessor.ORDER_REQUEST_INPUT_CHANNEL)
    @Output(PROCESS_ORDER_REQUEST_CHANNEL)
    public Message<OrderMessage> processOrderRequestListener(final Message<OrderMessage> orderMessage) throws IOException, SQLException {
        return orderMessage;
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
        return IntegrationFlows.from(PROCESS_ORDER_REQUEST_CHANNEL)
               .<OrderMessage>handle((payload, headers) -> {
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
