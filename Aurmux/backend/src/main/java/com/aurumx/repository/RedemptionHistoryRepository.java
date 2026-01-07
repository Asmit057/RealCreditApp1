package com.aurumx.repository;

import com.aurumx.entity.RedemptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RedemptionHistoryRepository extends JpaRepository<RedemptionHistory, Long> {
    List<RedemptionHistory> findByCustomerIdOrderByRedeemedAtDesc(Long customerId);
}
