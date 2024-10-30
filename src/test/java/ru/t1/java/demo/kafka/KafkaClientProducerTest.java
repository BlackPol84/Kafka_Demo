package ru.t1.java.demo.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.t1.java.demo.model.dto.ClientDto;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaClientProducerTest {

    private final String topic = "t1_demo_client_registration";

    @Mock
    private KafkaTemplate<String, ClientDto> template;

    @InjectMocks
    private KafkaClientProducer producer;

    @Test
    public void sendTo_whenSuccess_thenFlushCalled() {

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("John");
        clientDto.setLastName("Doe");
        clientDto.setMiddleName("Middle");

        CompletableFuture<SendResult<String, ClientDto>> future = new CompletableFuture<>();
        future.complete(new SendResult<>(null, null));
        when(template.send(topic, clientDto)).thenReturn(future);

        producer.sendTo(topic, clientDto);

        verify(template).send(topic, clientDto);
        verify(template).flush();
    }

    @Test
    public void sendTo_whenInvalidTopic_returnException() {

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("John");
        clientDto.setLastName("Doe");
        clientDto.setMiddleName("Middle");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> {
                    producer.sendTo(null, clientDto);
                });
        assertEquals("Topic must not be null or empty", exception.getMessage());
    }

    @Test
    public void sendTo_whenNullDto_returnException() {

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> {
                    producer.sendTo(topic, null);
                });
        assertEquals("ClientDto must not be null", exception.getMessage());
    }

    @Test
    void sendTo_whenFailure_thenFlushNotCalled() {

        ClientDto clientDto = new ClientDto();
        clientDto.setId(1L);
        clientDto.setFirstName("John");
        clientDto.setLastName("Doe");
        clientDto.setMiddleName("Middle");

        when(template.send(topic, clientDto)).thenThrow(new RuntimeException("Kafka error"));

        producer.sendTo(topic, clientDto);

        verify(template).send(topic, clientDto);
        verify(template, never()).flush();
    }
}
