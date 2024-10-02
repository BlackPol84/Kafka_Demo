package ru.t1.java.demo.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.TransactionDto;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "accountId", source = "account.id")
    TransactionDto toDto(Transaction transaction);

    @InheritInverseConfiguration
    Transaction toEntity(TransactionDto dto);
}