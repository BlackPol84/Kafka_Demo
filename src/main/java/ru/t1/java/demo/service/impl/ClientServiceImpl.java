package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.mapper.ClientMapper;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.web.CheckWebClient;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final CheckWebClient checkWebClient;

    @Override
    public List<ClientDto> registerClients(List<ClientDto> clientDtos) {

        if (clientDtos == null || clientDtos.isEmpty()) {
            log.warn("ClientDto list is null or empty");
            return Collections.emptyList();
        }

        List<Client> clients = clientDtos.stream()
                .map(mapper::toEntity)
                .filter(Objects::nonNull).toList();

        List<Client> savedClients = repository.saveAll(clients);

        log.debug("Successfully registered {} clients", savedClients.size());

        return clients.stream().map(mapper::toDto).toList();
    }

    @Override
    public ClientDto registerClient(ClientDto clientDto) {

        Client client = mapper.toEntity(clientDto);
        Client savedClient = repository.save(client);

        log.debug("Client is registered: {}", savedClient);

        return mapper.toDto(savedClient);
    }

    public boolean isClientBlocked(Client client) {
        Optional<CheckResponse> check = checkWebClient.check(client.getId());

        return check.map(CheckResponse::getBlocked).orElse(false);
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
