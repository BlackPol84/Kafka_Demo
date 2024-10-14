package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.mapper.ClientMapper;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    public void registerClients(List<ClientDto> clientDtos) {

        List<Client> clients = clientDtos.stream()
                .map(mapper::toEntity).toList();
        repository.saveAll(clients);
    }

    @LogException
    @Override
    public List<ClientDto> parseJson() {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients;
        try {
            clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"),
                    ClientDto[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.asList(clients);
    }
}
