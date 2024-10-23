package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

import java.util.List;

public interface AccountService {

    List<AccountDto> registerAccounts(List<AccountDto> accountDtos);

    String unlockAccount(Long accountId);

}
