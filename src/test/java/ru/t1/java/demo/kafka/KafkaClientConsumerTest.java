package ru.t1.java.demo.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaClientConsumerTest {

    @InjectMocks
    private KafkaClientConsumer consumer;

    @Mock
    private ClientService service;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    public void listener_WithValidMessageList_ShouldAcknowledge() {

        ClientDto client1 = new ClientDto();
        client1.setFirstName("John");
        client1.setMiddleName("Middle");
        client1.setLastName("Doe");

        ClientDto client2 = new ClientDto();
        client2.setFirstName("Jane");
        client2.setMiddleName("Middle");
        client2.setLastName("Doe");

        List<ClientDto> clientDtos = List.of(client1, client2);

        consumer.listener(clientDtos, acknowledgment);

        verify(service, times(1)).registerClients(clientDtos);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void listener_WhenServiceThrowsException_ShouldNotAcknowledge() {

        ClientDto client1 = new ClientDto();
        ClientDto client2 = new ClientDto();

        List<ClientDto> clientDtos = List.of(client1, client2);

        doThrow(new RuntimeException("Processing error")).when(service).registerClients(anyList());

        consumer.listener(clientDtos, acknowledgment);

        verify(service, times(1)).registerClients(clientDtos);
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    public void listener_EmptyMessageList_ShouldAcknowledge() {

        List<ClientDto> clientDtos = List.of();

        consumer.listener(clientDtos, acknowledgment);

        verify(service, times(1)).registerClients(clientDtos);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void listener_NullMessageList_ShouldAcknowledge() {

        consumer.listener(null, acknowledgment);

        verify(service, times(1)).registerClients(null);
        verify(acknowledgment, times(1)).acknowledge();
    }
}
