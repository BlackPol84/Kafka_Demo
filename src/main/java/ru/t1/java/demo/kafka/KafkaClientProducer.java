package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.ClientDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientProducer {

    private final KafkaTemplate<String, ClientDto> clientKafkaTemplate;

    public void sendTo(String topic, ClientDto dto) {
        try {
            clientKafkaTemplate.send(topic, dto).get();
            clientKafkaTemplate.flush();
            log.debug("dto send: {}", dto.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
