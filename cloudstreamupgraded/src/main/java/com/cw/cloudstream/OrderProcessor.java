package com.cw.cloudstream;


import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface OrderProcessor {
    String SEND_ORDER_REQUEST_OUTPUT_CHANNEL = "sendOrderRequestOutputChannel-out-0";//""send-order-request-output-channel";
    String ORDER_REQUEST_INPUT_CHANNEL = "processOrderRequestListener-in-0" ;//"order-request-input-channel";

//    @Output( OrderProcessor.SEND_ORDER_REQUEST_OUTPUT_CHANNEL)
//    MessageChannel sendOrderRequestOutputChannel();
//
//    @Input( OrderProcessor.ORDER_REQUEST_INPUT_CHANNEL)
//    SubscribableChannel orderRequestInputChannel();
}
