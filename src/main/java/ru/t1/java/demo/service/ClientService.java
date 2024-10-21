package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;

import java.util.List;

public interface ClientService {
    List<ClientDto> registerClients(List<ClientDto> clientDtos);

    ClientDto registerClient(ClientDto clientDto);

    boolean isClientBlocked(Client client);

    List<ClientDto> parseJson();

}
