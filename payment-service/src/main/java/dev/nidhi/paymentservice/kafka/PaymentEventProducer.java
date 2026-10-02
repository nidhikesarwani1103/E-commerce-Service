package dev.nidhi.paymentservice.kafka;

import dev.nidhi.paymentservice.events.PaymentCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {
    private static final String TOPIC = "payment-events";
    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event){
        kafkaTemplate.send(
                TOPIC,
                event.getOrderId().toString(),
                event
        );
    }
}
