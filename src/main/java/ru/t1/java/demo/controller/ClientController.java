package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/clients")
public class ClientController {

    private final ClientService service;
    private final KafkaClientProducer kafkaClientProducer;
    @Value("${spring.kafka.topic.client_registration}")
    private String topic;

    @LogException
    @Metric
    @GetMapping("/parse")
    @HandlingResult
    public void parseSource() {
        List<ClientDto> clientDtos = service.parseJson();
        clientDtos.forEach(dto -> kafkaClientProducer.sendTo(topic, dto));
    }

    @GetMapping("/register")
    public ResponseEntity<ClientDto> register(@RequestBody ClientDto clientDto) {
        log.info("Registering client: {}", clientDto);
        ClientDto savedClient = service.registerClient(clientDto);

        return ResponseEntity.ok().body(savedClient);
    }
}
