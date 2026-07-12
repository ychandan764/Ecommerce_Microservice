package com.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class OrderEventConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleOrderEvent(Map<String, Object> event) {

        if (event == null) {
            log.warn("Received null order event");
            return;
        }

        try {
            log.info("Received order event: {}", event);


            Object orderId = event.get("OrderId");
            Object status = event.get("Status");

            log.info("OrderId: {}, Status: {}", orderId, status);



        } catch (Exception ex) {
            log.error("Error processing order event: {}", event, ex);

        }
    }
}

