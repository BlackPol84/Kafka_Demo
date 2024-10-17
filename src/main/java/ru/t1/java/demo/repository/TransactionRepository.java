package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTopByAccountIdOrderByIdDesc(Long accountId);

    List<Transaction> findByAccountIdAndProcessedFalseOrderByIdAsc(Long accountId);
}