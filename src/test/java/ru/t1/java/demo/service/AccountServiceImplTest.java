package ru.t1.java.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private AccountMapper mapper;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountServiceImpl accountService;

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

        List<AccountDto> accountDtos = Arrays.asList(accountDto, accountDto2);

        Client client = new Client();
        client.setId(1L);

        Client client2 = new Client();
        client2.setId(2L);

        Account account = new Account();
        account.setClient(client);
        account.setType(AccountType.DEBIT);
        account.setBalance(new BigDecimal("1000.00"));
        account.setBlocked(false);

        Account account2 = new Account();
        account2.setClient(client2);
        account2.setType(AccountType.DEBIT);
        account2.setBalance(new BigDecimal("500.00"));
        account2.setBlocked(false);

        when(mapper.toEntity(accountDto)).thenReturn(account);
        when(mapper.toEntity(accountDto2)).thenReturn(account2);

        when(mapper.toEntity(accountDto)).thenReturn(account);
        when(mapper.toEntity(accountDto2)).thenReturn(account2);
        when(repository.saveAll(anyList())).thenReturn(Arrays.asList(account, account2));
        when(mapper.toDto(account)).thenReturn(accountDto);
        when(mapper.toDto(account2)).thenReturn(accountDto2);

        List<AccountDto> result = accountService.registerAccounts(accountDtos);

        assertEquals(2, result.size());
        assertEquals(accountDto, result.get(0));
        assertEquals(accountDto2, result.get(1));
        verify(repository, times(1)).saveAll(anyList());

    }

    @Test
    void registerAccounts_ShouldIgnoreNullEntities_WhenMappingFails() {

        AccountDto accountDto = new AccountDto();
        accountDto.setClientId(1L);
        accountDto.setType(AccountType.DEBIT);
        accountDto.setBalance(new BigDecimal("1000.00"));
        accountDto.setBlocked(false);

        AccountDto accountDto2 = null;

        List<AccountDto> accountDtos = Arrays.asList(accountDto, accountDto2);

        Client client = new Client();
        client.setId(1L);

        Account account = new Account();
        account.setClient(client);
        account.setType(AccountType.DEBIT);
        account.setBalance(new BigDecimal("1000.00"));
        account.setBlocked(false);

        when(mapper.toEntity(accountDto)).thenReturn(account);
        when(mapper.toEntity(accountDto2)).thenReturn(null);
        when(repository.saveAll(Collections.singletonList(account)))
                .thenReturn(Collections.singletonList(account));
        when(mapper.toDto(account)).thenReturn(accountDto);

        List<AccountDto> result = accountService.registerAccounts(accountDtos);

        ArgumentCaptor<List<Account>> captor = forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals(new BigDecimal("1000.00"), captor.getValue().get(0).getBalance());

        assertEquals(1, result.size());
        verify(repository, times(1)).saveAll(anyList());
        verify(mapper).toEntity(accountDto);
        verify(mapper).toEntity(accountDto2);

    }

    @Test
    void unlockAccount_unlockBlockedCreditAccountWithSufficientFunds() {

        Account blockedCreditAccount = new Account();
        blockedCreditAccount.setId(1L);
        blockedCreditAccount.setBlocked(true);
        blockedCreditAccount.setType(AccountType.CREDIT);
        blockedCreditAccount.setBalance(BigDecimal.valueOf(100));

        when(repository.findById(1L)).thenReturn(Optional.of(blockedCreditAccount));

        String result = accountService.unlockAccount(1L);

        verify(repository).save(blockedCreditAccount);
        verify(transactionService).reprocessFailedTransactions(1L);
        assertEquals("Unlocked", result);
        assertFalse(blockedCreditAccount.isBlocked());
    }

    @Test
    void unlockAccount_unlockBlockedCreditAccountWithInsufficientFunds() {

        Account blockedCreditAccount = new Account();
        blockedCreditAccount.setId(1L);
        blockedCreditAccount.setBlocked(true);
        blockedCreditAccount.setType(AccountType.CREDIT);
        blockedCreditAccount.setBalance(BigDecimal.ZERO);

        when(repository.findById(1L)).thenReturn(Optional.of(blockedCreditAccount));

        String result = accountService.unlockAccount(1L);

        verify(repository, never()).save(any());
        assertEquals("Insufficient funds to unlock", result);
    }

    @Test
    void unlockAccount_unlockBlockedDebitAccount() {

        Account blockedDebitAccount = new Account();
        blockedDebitAccount.setId(2L);
        blockedDebitAccount.setBlocked(true);
        blockedDebitAccount.setType(AccountType.DEBIT);

        when(repository.findById(2L)).thenReturn(Optional.of(blockedDebitAccount));

        String result = accountService.unlockAccount(2L);

        verify(repository).save(blockedDebitAccount);
        assertEquals("Unlocked", result);

        assertFalse(blockedDebitAccount.isBlocked());
    }

    @Test
    void unlockAccount_WhenNotBlocked() {

        Account blockedDebitAccount = new Account();
        blockedDebitAccount.setId(2L);
        blockedDebitAccount.setBlocked(false);
        blockedDebitAccount.setType(AccountType.DEBIT);

        when(repository.findById(2L)).thenReturn(Optional.of(blockedDebitAccount));

        String result = accountService.unlockAccount(2L);

        verify(repository, never()).save(any());
        assertEquals("Account is not blocked", result);
    }
}
