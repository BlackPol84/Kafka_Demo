package ru.t1.java.demo.service.impl;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.AccountType;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final TransactionService transactionService;

    public void registerAccounts(List<AccountDto> messageList) {

        List<Account> accounts = messageList.stream()
                .map(mapper::toEntity)
                .toList();

        repository.saveAll(accounts);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED) //default level with Postgres
    public String unlockAccount(Long accountId) {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (account.isBlocked()) {
            if (account.getType() == AccountType.CREDIT) {
                if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    account.setBlocked(false);
                    repository.save(account);
                    log.info("Account {} is unlocked.", accountId);

                    transactionService.reprocessFailedTransactions(accountId);

                    return "Unlocked";
                } else {
                    log.warn("Insufficient funds to unlock account {}.", accountId);
                    return "Insufficient funds to unlock";
                }
            } else if (account.getType() == AccountType.DEBIT) {
                account.setBlocked(false);
                repository.save(account);
                log.info("Deposit account {} is unlocked.", accountId);

                return "Unlocked";
            }
        }

        return "Account is not blocked";

    }
}
