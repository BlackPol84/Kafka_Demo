package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientConsumer {

    private final ClientService clientService;
    private final ru.t1.java.demo.mapper.ClientMapper mapper;

    @Metric
    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.client_registration}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<ClientDto> messageList,
                         Acknowledgment ack) {
        log.debug("Client consumer: Обработка новых сообщений");

        try {
            clientService.registerClients(messageList);
            ack.acknowledge();

        } catch (Exception ex) {
            log.warn("Ошибка обработки сообщений: ", ex);
        }
        log.debug("Client consumer: записи обработаны");
    }
}
