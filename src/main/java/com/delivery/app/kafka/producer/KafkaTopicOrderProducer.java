package com.delivery.app.kafka.producer;

import com.delicias.kafka.core.dto.KafkaTopicOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KafkaTopicOrderProducer {

    private static final String TOPIC = "order";

    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTopicOrderProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendMessageTopicOrder(KafkaTopicOrderDTO message) {
        kafkaTemplate.send(TOPIC, message);
    }

}
