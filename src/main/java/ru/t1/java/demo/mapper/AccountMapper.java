package ru.t1.java.demo.mapper;

import org.mapstruct.*;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.AccountDto;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "clientId", source = "client.id")
    AccountDto toDto(Account account);

    @InheritInverseConfiguration
    Account toEntity(AccountDto dto);
}