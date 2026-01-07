package com.aurumx.repository;

import com.aurumx.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    // Card unique check ONLY among active (not deleted) cards
    boolean existsByCardNumberAndDeletedFalse(String cardNumber);

    // Fetch only active cards of customer
    List<CreditCard> findByCustomerIdAndDeletedFalse(Long customerId);

    // Safe fetch
    Optional<CreditCard> findByIdAndDeletedFalse(Long id);

}
