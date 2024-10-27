package ru.t1.java.demo.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaAccountConsumerTest {

    @InjectMocks
    private KafkaAccountConsumer consumer;

    @Mock
    private AccountService service;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void listener_WithValidMessageList_ShouldAcknowledge() {

        AccountDto accountDto = new AccountDto();
        accountDto.setClientId(1L);
        accountDto.setType(AccountType.DEBIT);
        accountDto.setBalance(new BigDecimal("1000.00"));
        accountDto.setBlocked(false);

        AccountDto accountDto2 = new AccountDto();
        accountDto2.setClientId(2L);
        accountDto2.setType(AccountType.DEBIT);
        accountDto2.setBalance(new BigDecimal("500.00"));
        accountDto2.setBlocked(false);

        List<AccountDto> accountDtos = List.of(accountDto, accountDto2);

        consumer.listener(accountDtos, acknowledgment);

        verify(service, times(1)).registerAccounts(accountDtos);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void listener_WhenServiceThrowsException_ShouldNotAcknowledge() {

        AccountDto accountDto = new AccountDto();
        AccountDto accountDto2 = new AccountDto();

        List<AccountDto> accountDtos = List.of(accountDto, accountDto2);

        doThrow(new RuntimeException("Processing error")).when(service).registerAccounts(anyList());

        consumer.listener(accountDtos, acknowledgment);

        verify(service, times(1)).registerAccounts(accountDtos);
        verify(acknowledgment, never()).acknowledge();

    }

    @Test
    void listener_WithEmptyMessageList_ShouldAcknowledge() {

        List<AccountDto> accountDtos = Collections.emptyList();

        consumer.listener(accountDtos, acknowledgment);

        verify(service, times(1)).registerAccounts(any());
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void listener_WithNullMessageList_ShouldAcknowledge() {

        consumer.listener(null, acknowledgment);

        verify(service, times(1)).registerAccounts(any());
        verify(acknowledgment, times(1)).acknowledge();
    }
}
