package ru.t1.java.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.mapper.ClientMapper;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.impl.ClientServiceImpl;
import ru.t1.java.demo.web.CheckWebClient;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository repository;

    @Mock
    private ClientMapper mapper;

    @Mock
    private CheckWebClient checkWebClient;

    @Test
    void registerClients_ShouldReturnEmptyList_WhenClientDtoListIsNull() {
        List<ClientDto> result = clientService.registerClients(null);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void registerClients_ShouldReturnEmptyList_WhenClientDtoListIsEmpty() {
        List<ClientDto> result = clientService.registerClients(Collections.emptyList());
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void registerClients_ShouldSaveClients_ReturnListClientDtos() {

        ClientDto clientDto1 = new ClientDto();
        clientDto1.setFirstName("John");
        clientDto1.setLastName("Doe");
        clientDto1.setMiddleName("Middle");

        ClientDto clientDto2 = new ClientDto();
        clientDto2.setFirstName("Jane");
        clientDto2.setLastName("Doe");
        clientDto2.setMiddleName("Middle");

        List<ClientDto> clientDtos = Arrays.asList(clientDto1, clientDto2);

        Client client1 = new Client();
        client1.setFirstName("John");
        client1.setLastName("Doe");
        client1.setMiddleName("Middle");

        Client client2 = new Client();
        client2.setFirstName("Jane");
        client2.setLastName("Doe");
        client2.setMiddleName("Middle");

        when(mapper.toEntity(clientDto1)).thenReturn(client1);
        when(mapper.toEntity(clientDto2)).thenReturn(client2);
        when(repository.saveAll(Arrays.asList(client1, client2)))
                .thenReturn(Arrays.asList(client1, client2));
        when(mapper.toDto(client1)).thenReturn(clientDto1);
        when(mapper.toDto(client2)).thenReturn(clientDto2);

        List<ClientDto> result = clientService.registerClients(clientDtos);

        ArgumentCaptor<List<Client>> captor = forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals("John", captor.getValue().get(0).getFirstName());
        assertEquals("Jane", captor.getValue().get(1).getFirstName());

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void registerClients_ShouldIgnoreNullEntities_WhenMappingFails() {
        ClientDto clientDto1 = new ClientDto();
        clientDto1.setFirstName("John");
        clientDto1.setLastName("Doe");
        clientDto1.setMiddleName("Middle");

        ClientDto clientDto2 = null;

        List<ClientDto> clientDtos = Arrays.asList(clientDto1, clientDto2);

        Client client1 = new Client();
        client1.setFirstName("John");
        client1.setLastName("Doe");
        client1.setMiddleName("Middle");

        when(mapper.toEntity(clientDto1)).thenReturn(client1);
        when(mapper.toEntity(clientDto2)).thenReturn(null);
        when(repository.saveAll(Collections.singletonList(client1))).thenReturn(Collections.singletonList(client1));
        when(mapper.toDto(client1)).thenReturn(clientDto1);

        List<ClientDto> result = clientService.registerClients(clientDtos);

        ArgumentCaptor<List<Client>> captor = forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("John", captor.getValue().get(0).getFirstName());

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void registerClient_ShouldSaveClient_ReturnDto() {

        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName("John");
        clientDto.setLastName("Doe");
        clientDto.setMiddleName("Middle");

        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setMiddleName("Middle");

        Client savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setFirstName("John");
        savedClient.setLastName("Doe");
        savedClient.setMiddleName("Middle");

        ClientDto savedClientDto = new ClientDto();
        savedClientDto.setId(1L);
        savedClientDto.setFirstName("John");
        savedClientDto.setLastName("Doe");
        savedClientDto.setMiddleName("Middle");

        when(mapper.toEntity(clientDto)).thenReturn(client);
        when(repository.save(client)).thenReturn(savedClient);
        when(mapper.toDto(savedClient)).thenReturn(savedClientDto);

        ClientDto result = clientService.registerClient(clientDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        verify(repository, times(1)).save(client);
        verify(mapper).toEntity(clientDto);
        verify(mapper).toDto(savedClient);
    }


}
