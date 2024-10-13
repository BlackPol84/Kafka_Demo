package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.model.dto.TransactionDto;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bank/transactions")
public class TransactionController {

    @Value("${spring.kafka.topic.client_transactions}")
    private String topic;
    private final KafkaTransactionProducer producer;

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> create(@RequestBody TransactionDto dto) {
        producer.sendTo(topic, dto);
        return ResponseEntity.ok("Transaction processed successfully");
    }
}
