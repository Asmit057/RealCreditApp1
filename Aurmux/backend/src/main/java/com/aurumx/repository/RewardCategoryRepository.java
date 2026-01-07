package com.aurumx.repository;

import com.aurumx.entity.RewardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardCategoryRepository extends JpaRepository<RewardCategory, Long> {
    List<RewardCategory> findAllByOrderByDisplayOrderAsc();
}
