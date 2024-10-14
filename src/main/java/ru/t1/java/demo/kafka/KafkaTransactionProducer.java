package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, TransactionDto> template;

    public void sendTo(String topic, TransactionDto dto) {
        try {
            template.send(topic, dto).get();
            template.flush();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}