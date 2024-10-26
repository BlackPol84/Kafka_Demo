package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.kafka.KafkaFailedTransactionProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.TransactionType;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.impl.TransactionServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private KafkaFailedTransactionProducer failedTransactionProducer;

    @Mock
    private ClientService clientService;

    private Account account;
    private Client client;

    @BeforeEach
    public void setUp() {

        account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("100.00"));
        account.setBlocked(false);

        client = new Client();
        client.setId(1L);
    }

    @Test
    public void registerTransaction_whenAccountNotBlocked_whenClientNotBlocked_SuccessfulDeposit() {

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("50.00"));
        transactionDto.setClientId(client.getId());
        transactionDto.setAccountId(account.getId());
        transactionDto.setTransactionType(TransactionType.DEPOSIT);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(clientService.isClientBlocked(client)).thenReturn(false);

        transactionService.registerTransaction(List.of(transactionDto));

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void registerTransaction_whenAccountNotBlocked_whenClientNotBlocked_SuccessfulWithdraw() {

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("50.00"));
        transactionDto.setClientId(client.getId());
        transactionDto.setAccountId(account.getId());
        transactionDto.setTransactionType(TransactionType.WITHDRAW);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(clientService.isClientBlocked(client)).thenReturn(false);

        transactionService.registerTransaction(List.of(transactionDto));

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(any(Account.class));
        assertEquals(new BigDecimal("50.00"), account.getBalance());
    }

    @Test
    public void registerTransaction_whenAccountNotBlocked_whenClientNotBlocked_InsufficientFunds() {

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("150.00"));
        transactionDto.setClientId(client.getId());
        transactionDto.setAccountId(account.getId());
        transactionDto.setTransactionType(TransactionType.WITHDRAW);

        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(clientService.isClientBlocked(client)).thenReturn(false);

        transactionService.registerTransaction(List.of(transactionDto));

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, never()).save(any());
        assertEquals(new BigDecimal("100.00"), account.getBalance());
    }

//    @Test
//    public void registerTransaction_WhenCancelWithdrawTransaction_Success() {
//
//        Transaction lastTransaction = new Transaction();
//        lastTransaction.setId(1L);
//        lastTransaction.setAccount(account);
//        lastTransaction.setAmount(BigDecimal.valueOf(50));
//        lastTransaction.setTransactionType(TransactionType.WITHDRAW);
//
//        when(transactionRepository.findTopByAccountIdOrderByIdDesc(account.getId()))
//                .thenReturn(Optional.of(lastTransaction));
//
//        transactionService.cancel(account.getId());
//
//        verify(accountRepository, times(1)).save(account);
//        verify(transactionRepository, times(1)).delete(lastTransaction);
//        assertEquals(BigDecimal.valueOf(150), account.getBalance()); // Проверяем, что баланс увеличился
//    }

}
