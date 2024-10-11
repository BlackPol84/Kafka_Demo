package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/bank")
public class ClientController {

    private final ClientService service;
    private final KafkaClientProducer kafkaClientProducer;
    @Value("${spring.kafka.topic.client_registration}")
    private String topic;

    @LogException
    @Metric
    @GetMapping(value = "clients/parse")
    @HandlingResult
    public void parseSource() {
        List<ClientDto> clientDtos = service.parseJson();
        clientDtos.forEach(dto -> kafkaClientProducer.sendTo(topic, dto));
    }

    @PostMapping()
    ClientDto create(@RequestBody ClientDto clientDto) {
        return service.create(clientDto);
    }
}
