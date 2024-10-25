package ru.t1.java.demo.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.util.AbstractIntegrationTestInitializer;
import ru.t1.java.demo.service.impl.ClientServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class  ClientServiceImplTest extends AbstractIntegrationTestInitializer {

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "client");
    }

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

        List<ClientDto> result = clientService.registerClients(clientDtos);

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());

        Set<Long> ids = result.stream().mapToLong(ClientDto::getId)
                .boxed().collect(Collectors.toSet());

        assertEquals(2, ids.size());
    }

    @Test
    void registerClients_ShouldIgnoreNullEntities_WhenMappingFails() {

        ClientDto clientDto1 = new ClientDto();
        clientDto1.setFirstName("John");
        clientDto1.setLastName("Doe");
        clientDto1.setMiddleName("Middle");

        List<ClientDto> clientDtos = Arrays.asList(clientDto1, null);

        List<ClientDto> result = clientService.registerClients(clientDtos);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
    }

    @Test
    void registerClient_ShouldSaveClient_ReturnDto() {

        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName("John");
        clientDto.setLastName("Doe");
        clientDto.setMiddleName("Middle");

        ClientDto result = clientService.registerClient(clientDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Middle", result.getMiddleName());
    }
}
