package com.aurumx.service;

import com.aurumx.entity.RewardCategory;
import com.aurumx.entity.RewardItem;
import com.aurumx.repository.RewardCategoryRepository;
import com.aurumx.repository.RewardItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardCatalogService {
    
    private final RewardCategoryRepository rewardCategoryRepository;
    private final RewardItemRepository rewardItemRepository;
    
    public List<RewardCategory> getAllCategories() {
        return rewardCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }
    
    public List<RewardItem> getItemsByCategory(Long categoryId) {
        return rewardItemRepository.findByCategoryIdAndAvailableTrue(categoryId);
    }
    
    public List<RewardItem> getAllAvailableItems() {
        return rewardItemRepository.findByAvailableTrue();
    }
}
