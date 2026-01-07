package com.aurumx.repository;

import com.aurumx.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByDeletedFalse(Pageable pageable);

    Optional<Customer> findByIdAndDeletedFalse(Long id);

    @Query("SELECT c FROM Customer c WHERE c.deleted = false AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ")
    Page<Customer> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Customer c " +
            "JOIN CreditCard cc ON cc.customer.id = c.id " +
            "WHERE c.deleted = false AND cc.cardNumber LIKE CONCAT('%', :cardNumber, '%')")
    Page<Customer> searchByCardNumber(@Param("cardNumber") String cardNumber, Pageable pageable);


    boolean existsByEmailAndDeletedFalse(String email);

    //Find soft-deleted customer for restore
    Optional<Customer> findByEmailAndDeletedTrue(String email);
}
