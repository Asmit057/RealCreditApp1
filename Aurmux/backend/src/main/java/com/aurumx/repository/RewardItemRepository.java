package com.aurumx.repository;

import com.aurumx.entity.RewardItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardItemRepository extends JpaRepository<RewardItem, Long> {
    List<RewardItem> findByCategoryId(Long categoryId);
    List<RewardItem> findByCategoryIdAndAvailableTrue(Long categoryId);
    List<RewardItem> findByAvailableTrue();
}
