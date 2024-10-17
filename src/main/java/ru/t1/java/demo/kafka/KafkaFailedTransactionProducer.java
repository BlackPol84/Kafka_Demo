package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.FailedTransactionDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaFailedTransactionProducer {

    private final KafkaTemplate<String, FailedTransactionDto> kafkaTemplate;

    public void sendTo(String topic, FailedTransactionDto message) {
        try {
            kafkaTemplate.send(topic, message).get();
            kafkaTemplate.flush();
            log.debug("Transaction has been sent: {}", message);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
