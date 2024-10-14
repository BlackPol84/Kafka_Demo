package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaIdTransactionProducer {

    private final KafkaTemplate<String, Long> idKafkaTemplate;

    public void sendTo(String topic, Long idTransaction) {
        try {
            idKafkaTemplate.send(topic, idTransaction).get();
            idKafkaTemplate.flush();
            log.debug("transaction ID has been sent: {}", idTransaction);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
