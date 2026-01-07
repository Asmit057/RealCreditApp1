package com.aurumx.repository;

import com.aurumx.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    Optional<Reward> findByCreditCardId(Long creditCardId);
    List<Reward> findByCreditCard_CustomerId(Long customerId);
}
