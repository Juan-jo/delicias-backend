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

    // TODO multiple send DTO in TOPIC (Config DTO -> com.delicias.kafka.core.dto)
    // public void sendMessageTodo(KafkaTodoDTO message) { kafkaTemplate.send("test", message); }

}
