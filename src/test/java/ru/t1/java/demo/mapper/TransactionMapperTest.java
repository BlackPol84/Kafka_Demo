package ru.t1.java.demo.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.TransactionType;
import ru.t1.java.demo.model.dto.TransactionDto;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionMapperTest {

    private final TransactionMapper transactionMapper =
            Mappers.getMapper(TransactionMapper.class);

    @Test
    public void shouldMapTransactionToTransactionDto() {

        Client client = new Client();
        client.setId(1L);

        Account account = new Account();
        account.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setClient(client);
        transaction.setAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setProcessed(true);

        TransactionDto transactionDto = transactionMapper.toDto(transaction);

        assertEquals(transaction.getAmount(), transactionDto.getAmount());
        assertEquals(client.getId(), transactionDto.getClientId());
        assertEquals(account.getId(), transactionDto.getAccountId());
        assertEquals(transaction.getTransactionType(), transactionDto.getTransactionType());
    }

    @Test
    public void shouldMapTransactionDtoToTransaction() {

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("150.00"));
        transactionDto.setClientId(3L);
        transactionDto.setAccountId(4L);
        transactionDto.setTransactionType(TransactionType.DEPOSIT);

        Transaction transaction = transactionMapper.toEntity(transactionDto);

        assertEquals(transactionDto.getAmount(), transaction.getAmount());
        assertEquals(transactionDto.getTransactionType(), transaction.getTransactionType());
        assertEquals(transactionDto.getClientId(), transaction.getClient().getId());
        assertEquals(transactionDto.getAccountId(), transaction.getAccount().getId());
    }
}
