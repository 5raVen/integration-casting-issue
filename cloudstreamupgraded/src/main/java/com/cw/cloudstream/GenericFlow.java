package com.cw.cloudstream;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class GenericFlow {
    public static final String ERROR_CHANNEL = "errorChannel";

    public class AmqpUniqueId {
        private String amqpConsumerTag;
        private Long amqpDeliveryTag;

        public AmqpUniqueId(String amqpConsumerTag, Long amqpDeliveryTag) {
            this.amqpConsumerTag = amqpConsumerTag;
            this.amqpDeliveryTag = amqpDeliveryTag;
        }

        public String getAmqpConsumerTag() {
            return amqpConsumerTag;
        }

        public void setAmqpConsumerTag(String amqpConsumerTag) {
            this.amqpConsumerTag = amqpConsumerTag;
        }

        public Long getAmqpDeliveryTag() {
            return amqpDeliveryTag;
        }

        public void setAmqpDeliveryTag(Long amqpDeliveryTag) {
            this.amqpDeliveryTag = amqpDeliveryTag;
        }
    }

    public enum EAbleToOrder {
        FAILURE_ACCOUNT_NUMBER_INCORRECT,
        FAILURE_ORDER_SIZE_ZERO,
        SUCCESS
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlow.from(ERROR_CHANNEL)
                .log(LoggingHandler.Level.TRACE, this.getClass().getName() + ".errorFlow")
                // Filter multiple messages for the same input messages so that the error is only handled once
                .filter(Message.class, message -> {
                    Message<?> failedMessage = message;

                    if (failedMessage instanceof ErrorMessage &&
                            ((ErrorMessage) failedMessage).getPayload() instanceof MessagingException) {
                        failedMessage = ((MessagingException) ((ErrorMessage) failedMessage).getPayload()).getFailedMessage();
                    }

                    AmqpUniqueId amqpUniqueId = new AmqpUniqueId(
                            (String) failedMessage.getHeaders().get("amqp_consumerTag"),
                            (Long) failedMessage.getHeaders().get("amqp_deliveryTag"));

                    return true;//this.messageIdStore.putIfAbsent(amqpUniqueId, ZonedDateTime.now(ZoneId.of("Z"))) == null;
                })
                .handle(message -> {
                    Message<?> failedMessage = message;

                    if (failedMessage instanceof ErrorMessage &&
                            ((ErrorMessage) failedMessage).getPayload() instanceof MessagingException) {
                        failedMessage = ((MessagingException) ((ErrorMessage) failedMessage).getPayload()).getFailedMessage();
                    }
                    Channel channel = (Channel) failedMessage.getHeaders().get(AmqpHeaders.CHANNEL);
                    Long deliveryTag = (Long) failedMessage.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
                    EOrderStatus orderStatus = EOrderStatus.FAILED;

                    if (channel != null) {
                        orderStatus = EOrderStatus.RETRYING;

                        try {
                            channel.basicNack(deliveryTag, false, false);
                        } catch (IOException exception) {
                            System.out.printf("channel operation failed", exception);
                        }
                    } else {
                        System.out.printf("can't access channel: " + message.toString());
                    }

                    Long orderId = 1L;//getOrderIdFromFailedMessage(failedMessage);
                    if (orderId != null) {
//                        try {
//                            //this.database.setOrderStatus(orderId, orderStatus.getId(), null);
//                        } catch (SQLException exception) {
//                            log.error("An Error occurred while updating order status", exception);
//                        }
                    } else {
                        System.out.printf("unable to obtain order ID: " + message.toString());
                    }
                })
                .get();
    }

}
