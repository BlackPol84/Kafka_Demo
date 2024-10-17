package ru.t1.java.demo.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.kafka.KafkaFailedTransactionProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.dto.FailedTransactionDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionType;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    @Value("${spring.kafka.topic.transaction-errors}")
    private String topic;

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final KafkaFailedTransactionProducer failedTransactionProducer;

    @Transactional
    public void registerTransaction(List<TransactionDto> messageList) {

        for(TransactionDto transactionDto : messageList) {

            Transaction transaction = new Transaction();
            transaction.setAmount(transactionDto.getAmount());

            Client client = clientRepository.findById(transactionDto.getClientId())
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));

            Account account = accountRepository.findById(transactionDto.getAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("Account not found"));

            transaction.setClient(client);
            transaction.setAccount(account);
            transaction.setTransactionType(transactionDto.getTransactionType());

            if(!checkingAccount(transaction)) {
                continue;
            }

            switch (transaction.getTransactionType()) {
                case WITHDRAW -> withdraw(account, transaction.getAmount());
                case DEPOSIT -> deposit(account, transaction.getAmount());
                case CANCEL -> cancel(account.getId());
            }

        transactionRepository.save(transaction);
        log.debug("Transaction is saved.");
        }
    }

//    @Transactional
//    public void processPendingTransactions(Long idUnblockedAccount) {
//
//        List<Transaction> pendingTransactions = transactionRepository
//                .findTransactionByAccountIdAndProcessedFalse(idUnblockedAccount);
//
//        for(Transaction transaction : pendingTransactions) {
//
//
//        }
//    }

    private void withdraw(Account account, BigDecimal amount) {

        BigDecimal newBalance = account.getBalance().subtract(amount);

        if(newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Transaction for account {} would result in negative balance. " +
                            "Current balance: {}, Transaction amount: {}",
                    account.getId(), account.getBalance(), amount);
            throw new IllegalArgumentException("Insufficient balance for transaction.");
        } else {
            account.setBalance(newBalance);
            accountRepository.save(account);
        }
    }

    private void deposit(Account account, BigDecimal amount) {

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    private void cancel(Long accountId) {

        Transaction lastTransaction = transactionRepository
                .findTopByAccountIdOrderByIdDesc(accountId)
                .orElseThrow(() -> new RuntimeException("No transaction found for account"));

        Account account = lastTransaction.getAccount();

        if(lastTransaction.getTransactionType() == TransactionType.WITHDRAW) {
            account.setBalance(account.getBalance().add(lastTransaction.getAmount()));

        } else if (lastTransaction.getTransactionType() == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance().subtract(lastTransaction.getAmount()));

        } else {
            throw new IllegalArgumentException("Invalid transaction type for cancellation");
        }

        accountRepository.save(account);
        transactionRepository.delete(lastTransaction);
    }

    private boolean checkingAccount(Transaction transaction) {

        if(transaction.getAccount().isBlocked()) {

            log.warn("The account is blocked. Account ID: {}", transaction.getAccount().getId());

            transaction.setProcessed(false);
            transactionRepository.save(transaction);

            FailedTransactionDto failedTransaction = new FailedTransactionDto();
            failedTransaction.setOriginalTransactionId(transaction.getId());
            failedTransaction.setAccountId(transaction.getAccount().getId());

            failedTransactionProducer.sendTo(topic, failedTransaction);

            return false;
        }

        return true;
    }
}
