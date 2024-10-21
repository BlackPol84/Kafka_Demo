package ru.t1.java.demo.mapper;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.AccountDto;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountMapperTest {

    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    public void shouldMapAccountToAccountDto() {

        Client client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setMiddleName("Doe");
        client.setLastName("Smith");

        Account account = new Account();
        account.setId(1L);
        account.setClient(client);
        account.setType(AccountType.CREDIT);
        account.setBalance(new BigDecimal("1000.00"));
        account.setBlocked(false);

        AccountDto accountDto = accountMapper.toDto(account);

        assertNotNull(accountDto);
        assertEquals(account.getId(), accountDto.getId());
        assertEquals(account.getClient().getId(), accountDto.getClientId());
        assertEquals(account.getType(), accountDto.getType());
        assertEquals(account.getBalance(), accountDto.getBalance());
        assertEquals(account.isBlocked(), accountDto.isBlocked());
    }

    @Test
    public void shouldMapAccountDtoToAccount() {

        AccountDto accountDto = new AccountDto();
        accountDto.setId(1L);
        accountDto.setClientId(1L);
        accountDto.setType(AccountType.CREDIT);
        accountDto.setBalance(new BigDecimal("1000.00"));
        accountDto.setBlocked(false);

        Account account = accountMapper.toEntity(accountDto);

        assertNotNull(account);
        assertEquals(accountDto.getId(), account.getId());
        assertEquals(accountDto.getClientId(), account.getClient().getId());
        assertEquals(accountDto.getType(), account.getType());
        assertEquals(accountDto.getBalance(), account.getBalance());
        assertEquals(accountDto.isBlocked(), account.isBlocked());
    }
}
