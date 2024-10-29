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

    private final KafkaTemplate<String, ClientDto> template;

    public void sendTo(String topic, ClientDto dto) {

        validateInputs(topic, dto);

        try {
            template.send(topic, dto).get();
            template.flush();
            log.debug("dto send: {}", dto.getId());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void validateInputs(String topic, ClientDto dto) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic must not be null or empty");
        }
        if (dto == null) {
            throw new IllegalArgumentException("ClientDto must not be null");
        }
    }
}
