package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer {

    private final TransactionService service;

    @KafkaListener(id = "${spring.kafka.consumer.group-id-transaction}",
            topics = "${spring.kafka.topic.client_transactions}",
            containerFactory = "transactionKafkaListenerContainerFactory")
    public void listener(@Payload List<TransactionDto> messageList,
                         Acknowledgment ack) {
        log.debug("Transaction consumer: Обработка новых сообщений");

        try {
            service.registerTransaction(messageList);
            ack.acknowledge();

        } catch (Exception ex) {
            log.error("Ошибка обработки сообщений: ", ex);
        }
        log.debug("Transaction consumer: записи обработаны");
    }
}
