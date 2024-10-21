package ru.t1.java.demo.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientMapperTest {

    private final ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);

    @Test
    public void shouldMapClientDtoToClient() {

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("John");
        clientDto.setMiddleName("Doe");
        clientDto.setLastName("Smith");

        Client client = clientMapper.toEntity(clientDto);

        assertEquals(clientDto.getId(), client.getId());
        assertEquals(clientDto.getFirstName(), client.getFirstName());
        assertEquals(clientDto.getLastName(), client.getLastName());
        assertEquals(clientDto.getMiddleName(), client.getMiddleName());
    }

    @Test
    public void shouldMapClientToClientDto() {

        Client client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setMiddleName("Doe");
        client.setLastName("Smith");

        ClientDto clientDto = clientMapper.toDto(client);

        assertEquals(client.getId(), clientDto.getId());
        assertEquals(client.getFirstName(), clientDto.getFirstName());
        assertEquals(client.getLastName(), clientDto.getLastName());
        assertEquals(client.getMiddleName(), clientDto.getMiddleName());
    }

    @Test
    public void testPartialUpdate() {

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("Jane");
        clientDto.setMiddleName("Middle");
        clientDto.setLastName("null");

        Client client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setMiddleName("Doe");
        client.setLastName("Middle");

        clientMapper.partialUpdate(clientDto, client);

        assertEquals(client.getId(), clientDto.getId());
        assertEquals(client.getFirstName(), "Jane");
        assertEquals(client.getLastName(), "null");
        assertEquals(client.getMiddleName(), "Middle");
    }
}
