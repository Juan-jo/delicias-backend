package com.delivery.app.kafka.producer;

import com.delivery.app.kafka.dto.KafkaRestaurantKanbanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderProducer {

    private static final String TOPIC = "order";

    @Autowired
    private final KafkaTemplate<String, KafkaRestaurantKanbanDTO> kafkaTemplate;

    public KafkaOrderProducer(KafkaTemplate<String, KafkaRestaurantKanbanDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(KafkaRestaurantKanbanDTO message) {
        kafkaTemplate.send(TOPIC, message);
    }

    /*
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Async
    public void newOrder(String orderId) {
        kafkaTemplate.send(TOPIC, orderId);
    }*/
}
