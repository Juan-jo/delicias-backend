package com.delivery.app.kafka.producer;

import com.delicias.kafka.core.dto.KafkaTopicKanbanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KafkaTopicKanbanProducer {

    private static final String TOPIC = "kanban";

    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaTopicKanbanProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendMessageTopicKanban(KafkaTopicKanbanDTO message) {
        kafkaTemplate.send(TOPIC, message);
    }

}
