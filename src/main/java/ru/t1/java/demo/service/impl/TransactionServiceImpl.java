package ru.t1.java.demo.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.model.TransactionType;
import ru.t1.java.demo.web.CheckWebClient;

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
    private final ClientService clientService;

    @Transactional(isolation = Isolation.READ_COMMITTED) //default level with Postgres
    public void registerTransaction(List<TransactionDto> messageList) {

        for(TransactionDto transactionDto : messageList) {

            try {
                Transaction transaction = new Transaction();
                transaction.setAmount(transactionDto.getAmount());

                Client client = clientRepository.findById(transactionDto.getClientId())
                        .orElseThrow(() -> new EntityNotFoundException("Client not found"));

                Account account = accountRepository.findById(transactionDto.getAccountId())
                        .orElseThrow(() -> new EntityNotFoundException("Account not found"));

                transaction.setClient(client);
                transaction.setAccount(account);
                transaction.setTransactionType(transactionDto.getTransactionType());
                transaction.setProcessed(false);

                if (checkingAccount(transaction) && !clientService.isClientBlocked(client)) {

                    switch (transaction.getTransactionType()) {
                        case WITHDRAW -> withdraw(account, transaction.getAmount());
                        case DEPOSIT -> deposit(account, transaction.getAmount());
                        case CANCEL -> cancel(account.getId());
                    }

                    transaction.setProcessed(true);
                }

                transactionRepository.save(transaction);
                log.debug("Transaction is saved.");
            } catch (Exception e) {
                log.error("Failed to process transaction. Error: {}", e.getMessage());
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED) //default level with Postgres
    public void reprocessFailedTransactions(Long accountId) {

        List<Transaction> failedTransactions = transactionRepository
                .findByAccountIdAndProcessedFalseOrderByIdAsc(accountId);

        for (Transaction transaction : failedTransactions) {
            try {
                Account account = accountRepository.findById(transaction.getAccount().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Account not found"));

                if (checkingAccount(transaction)
                        && !clientService.isClientBlocked(transaction.getClient())) {

                    switch (transaction.getTransactionType()) {
                        case WITHDRAW -> withdraw(account, transaction.getAmount());
                        case DEPOSIT -> deposit(account, transaction.getAmount());
                        case CANCEL -> cancel(account.getId());
                    }

                    transaction.setProcessed(true);
                    transactionRepository.save(transaction);
                    log.debug("Reprocessed transaction ID: {}", transaction.getId());
                }
            } catch (Exception e) {
                log.error("Failed to reprocess transaction ID: {}. Error: {}"
                        , transaction.getId(), e.getMessage());
            }
        }
    }

    private void withdraw(Account account, BigDecimal amount) {

        BigDecimal newBalance = account.getBalance().subtract(amount);

        if(newBalance.compareTo(BigDecimal.ZERO) >= 0) {
            account.setBalance(newBalance);
            accountRepository.save(account);

        } else {
            log.error("Transaction for account {} would result in negative balance. " +
                            "Current balance: {}, Transaction amount: {}",
                    account.getId(), account.getBalance(), amount);
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
