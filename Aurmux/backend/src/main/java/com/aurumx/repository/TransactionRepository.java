package com.aurumx.repository;

import com.aurumx.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByCreditCardId(Long creditCardId, Pageable pageable);
    List<Transaction> findByCreditCardId(Long creditCardId); // Keep for compatibility if needed
    
    @Query("SELECT t FROM Transaction t " +
           "JOIN t.creditCard cc " +
           "WHERE cc.customer.id = :customerId AND t.processed = false")
    List<Transaction> findUnprocessedByCustomerId(@Param("customerId") Long customerId);

    List<Transaction> findByCreditCardIdAndProcessedFalse(Long creditCardId);
}
