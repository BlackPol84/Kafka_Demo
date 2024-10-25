package ru.t1.java.demo.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.util.AbstractIntegrationTestInitializer;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceImplTest extends AbstractIntegrationTestInitializer {

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "account");
    }

    @Test
    public void registerAccounts_ShouldReturnEmptyList_WhenInputIsNull() {
        List<AccountDto> result = accountService.registerAccounts(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void registerAccounts_ShouldReturnEmptyList_WhenInputIsEmpty() {
        List<AccountDto> result = accountService.registerAccounts(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void registerAccounts_ShouldSaveAccounts_ReturnListAccountDtos() {

        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setMiddleName("Middle");

        Long clientId = clientRepository.save(client).getId();

        Client client2 = new Client();
        client2.setFirstName("Jane");
        client2.setLastName("Doe");
        client2.setMiddleName("Middle");

        Long clientId2 = clientRepository.save(client2).getId();

        AccountDto accountDto = new AccountDto();
        accountDto.setClientId(clientId);
        accountDto.setType(AccountType.DEBIT);
        accountDto.setBalance(new BigDecimal("1000.00"));
        accountDto.setBlocked(false);

        AccountDto accountDto2 = new AccountDto();
        accountDto2.setClientId(clientId2);
        accountDto2.setType(AccountType.DEBIT);
        accountDto2.setBalance(new BigDecimal("500.00"));
        accountDto2.setBlocked(false);

        List<AccountDto> accountDtos = Arrays.asList(accountDto, accountDto2);

        List<AccountDto> result = accountService.registerAccounts(accountDtos);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getId() != null
                && a.getClientId().equals(clientId)
                && a.getType().equals(AccountType.DEBIT)
                && a.getBalance().compareTo(new BigDecimal("1000.00")) == 0));
        assertTrue(result.stream().anyMatch(a -> a.getId() != null
                && a.getClientId().equals(clientId2)
                && a.getType().equals(AccountType.DEBIT)
                && a.getBalance().compareTo(new BigDecimal("500.00")) == 0));
        assertFalse(result.get(0).isBlocked());
        assertFalse(result.get(1).isBlocked());

        Set<Long> ids = result.stream().mapToLong(AccountDto::getId)
                .boxed().collect(Collectors.toSet());

        assertEquals(2, ids.size());
    }

    @Test
    void registerAccounts_ShouldIgnoreNullEntities_WhenMappingFails() {

        Client client = new Client();
        client.setFirstName("Brad");
        client.setLastName("Pitt");
        client.setMiddleName("Middle");

        Long clientId = clientRepository.save(client).getId();

        AccountDto accountDto = new AccountDto();
        accountDto.setClientId(clientId);
        accountDto.setType(AccountType.DEBIT);
        accountDto.setBalance(new BigDecimal("1000.00"));
        accountDto.setBlocked(false);

        List<AccountDto> accountDtos = Arrays.asList(accountDto, null);

        List<AccountDto> result = accountService.registerAccounts(accountDtos);

        assertEquals(1, result.size());
        assertEquals(clientId, result.get(0).getClientId());
        assertEquals(AccountType.DEBIT, result.get(0).getType());
        assertEquals(new BigDecimal("1000.00"), result.get(0).getBalance());
        assertFalse(result.get(0).isBlocked());
    }

    @Test
    void unlockAccount_unlockBlockedCreditAccountWithSufficientFunds() {

        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setMiddleName("Middle");

        Client savedClient = clientRepository.save(client);

        Account account = new Account();
        account.setClient(savedClient);
        account.setType(AccountType.CREDIT);
        account.setBalance(new BigDecimal("1000.00"));
        account.setBlocked(true);

        Long accountId = accountRepository.save(account).getId();
        String result = accountService.unlockAccount(accountId);

        assertEquals("Unlocked", result);
    }

    @Test
    void unlockAccount_unlockBlockedCreditAccountWithInsufficientFunds() {

        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setMiddleName("Middle");

        Client savedClient = clientRepository.save(client);

        Account account = new Account();
        account.setClient(savedClient);
        account.setType(AccountType.CREDIT);
        account.setBalance(new BigDecimal("0.00"));
        account.setBlocked(true);

        Long accountId = accountRepository.save(account).getId();
        String result = accountService.unlockAccount(accountId);

        assertEquals("Insufficient funds to unlock", result);
    }

    @Test
    void unlockAccount_unlockBlockedDebitAccount() {

        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setMiddleName("Middle");

        Client savedClient = clientRepository.save(client);

        Account account = new Account();
        account.setClient(savedClient);
        account.setType(AccountType.DEBIT);
        account.setBalance(new BigDecimal("0.00"));
        account.setBlocked(true);

        Long accountId = accountRepository.save(account).getId();
        String result = accountService.unlockAccount(accountId);

        assertEquals("Unlocked", result);
    }

    @Test
    void unlockAccount_WhenNotBlocked() {

        Client client = new Client();
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setMiddleName("Middle");

        Client savedClient = clientRepository.save(client);

        Account account = new Account();
        account.setClient(savedClient);
        account.setType(AccountType.DEBIT);
        account.setBalance(new BigDecimal("0.00"));
        account.setBlocked(false);

        Long accountId = accountRepository.save(account).getId();
        String result = accountService.unlockAccount(accountId);

        assertEquals("Account is not blocked", result);
    }
}
