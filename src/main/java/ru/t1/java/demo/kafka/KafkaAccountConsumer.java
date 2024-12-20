package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {

    private final AccountService service;

    @KafkaListener(id = "${spring.kafka.consumer.group-id-account}",
            topics = "${spring.kafka.topic.client_account}",
            containerFactory = "accountKafkaListenerContainerFactory")
    public void listener(@Payload List<AccountDto> messageList,
                         Acknowledgment ack) {
        log.debug("Account consumer: processing new messages");

        try {
            service.registerAccounts(messageList);
            ack.acknowledge();

        } catch (Exception ex) {
            log.warn("Message processing error: ", ex);
        }
        log.debug("Account consumer: records have been processed");
    }
}
