package ru.t1.java.demo.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.t1.java.demo.model.TransactionType;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.TransactionService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaTransactionConsumerTest {

    @InjectMocks
    private KafkaTransactionConsumer consumer;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private TransactionService service;

    @Test
    public void listener_WithValidMessageList_ShouldAcknowledge() {

        TransactionDto transaction = new TransactionDto();
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setClientId(1L);
        transaction.setAccountId(1L);
        transaction.setTransactionType(TransactionType.DEPOSIT);

        TransactionDto transaction2 = new TransactionDto();
        transaction2.setAmount(new BigDecimal("500.00"));
        transaction2.setClientId(2L);
        transaction2.setAccountId(2L);
        transaction2.setTransactionType(TransactionType.DEPOSIT);

        List<TransactionDto> transactionDtos = List.of(transaction, transaction2);

        consumer.listener(transactionDtos, acknowledgment);

        verify(service, times(1)).registerTransaction(transactionDtos);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void listener_WhenServiceThrowsException_ShouldNotAcknowledge() {

        TransactionDto transaction = new TransactionDto();
        TransactionDto transaction2 = new TransactionDto();

        List<TransactionDto> transactionDtos = List.of(transaction, transaction2);

        doThrow(new RuntimeException("Processing error")).when(service).registerTransaction(anyList());

        consumer.listener(transactionDtos, acknowledgment);

        verify(service, times(1)).registerTransaction(transactionDtos);
        verify(acknowledgment, never()).acknowledge();

    }

    @Test
    void listener_WithEmptyMessageList_ShouldAcknowledge() {

        List<TransactionDto> transactionDtos = Collections.emptyList();

        consumer.listener(transactionDtos, acknowledgment);

        verify(service, times(1)).registerTransaction(transactionDtos);
        verify(acknowledgment, times(1)).acknowledge();
    }
}
