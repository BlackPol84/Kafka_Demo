package ru.t1.java.demo.service;

import ru.t1.java.demo.model.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    void registerTransaction(List<TransactionDto> messageList);
}
