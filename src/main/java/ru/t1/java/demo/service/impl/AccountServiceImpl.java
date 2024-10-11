package ru.t1.java.demo.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final AccountMapper mapper;

    public void registerAccounts(List<Account> accounts) {
        repository.saveAll(accounts);
    }

    public AccountDto create(AccountDto accountDto) {
        Account account = mapper.toEntity(accountDto);
        Account savedAccount = repository.save(account);

        return mapper.toDto(savedAccount);
    }

}
