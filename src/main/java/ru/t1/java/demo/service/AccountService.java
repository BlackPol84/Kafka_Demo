package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.List;

public interface AccountService {

    void registerAccounts(List<Account> accounts);

    AccountDto create(AccountDto accountDto);
}
