package ru.t1.java.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService service;

    @Mock
    private KafkaClientProducer kafkaClientProducer;

    @InjectMocks
    private ClientController clientController;

    @Value("${spring.kafka.topic.client_registration}")
    private String topic;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    void parseSource_shouldSendClientsToKafka() {

        ClientDto client1 = new ClientDto();
        client1.setId(1L);
        client1.setFirstName("John");
        client1.setMiddleName("Middle");
        client1.setLastName("Doe");

        ClientDto client2 = new ClientDto();
        client2.setId(2L);
        client2.setFirstName("Jane");
        client2.setMiddleName("Middle");
        client2.setLastName("Doe");

        List<ClientDto> clientDtos = Arrays.asList(client1, client2);

        when(service.parseJson()).thenReturn(clientDtos);

        clientController.parseSource();

        verify(kafkaClientProducer, times(1)).sendTo(topic, client1);
        verify(kafkaClientProducer, times(1)).sendTo(topic, client2);
        verify(service, times(1)).parseJson();
    }

    @Test
    void register_shouldReturnSavedClient() {

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("John");
        clientDto.setMiddleName("Middle");
        clientDto.setLastName("Doe");

        when(service.registerClient(any(ClientDto.class))).thenReturn(clientDto);

        ResponseEntity<ClientDto> response = clientController.register(clientDto);

        assertEquals(ResponseEntity.ok().body(clientDto), response);
        verify(service, times(1)).registerClient(clientDto);
    }
}
