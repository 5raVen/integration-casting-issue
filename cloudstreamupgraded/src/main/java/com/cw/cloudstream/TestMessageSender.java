package com.cw.cloudstream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class TestMessageSender {

    @Autowired
    private ApplicationContext context;
}
