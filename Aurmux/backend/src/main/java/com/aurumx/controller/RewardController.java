package com.aurumx.controller;

import com.aurumx.dto.response.RewardBalanceResponse;
import com.aurumx.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController {
    
    private final RewardService rewardService;
    
    @PostMapping("/process/{customerId}")
    public ResponseEntity<RewardBalanceResponse> processTransactions(@PathVariable Long customerId) {
        RewardBalanceResponse response = rewardService.processTransactions(customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process/card/{cardId}")
    public ResponseEntity<RewardBalanceResponse> processTransactionsByCard(@PathVariable Long cardId) {
        RewardBalanceResponse response = rewardService.processTransactionsByCard(cardId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/balance/{customerId}")
    public ResponseEntity<RewardBalanceResponse> getRewardBalance(@PathVariable Long customerId) {
        RewardBalanceResponse response = rewardService.getRewardBalance(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/catalog/categories")
    public ResponseEntity<List<com.aurumx.entity.RewardCategory>> getCategories() {
        return ResponseEntity.ok(rewardService.getCategories());
    }

    @GetMapping("/catalog/items")
    public ResponseEntity<List<com.aurumx.entity.RewardItem>> getAllRewardItems() {
        return ResponseEntity.ok(rewardService.getRewards(null));
    }

    @GetMapping("/catalog/category/{categoryId}")
    public ResponseEntity<List<com.aurumx.entity.RewardItem>> getRewardItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(rewardService.getRewards(categoryId));
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<com.aurumx.entity.RedemptionHistory>> getRedemptionHistory(@PathVariable Long customerId) {
        return ResponseEntity.ok(rewardService.getRedemptionHistory(customerId));
    }
}
