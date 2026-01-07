package com.aurumx.service;

import com.aurumx.config.RewardConfig;
import com.aurumx.dto.response.RewardBalanceResponse;
import com.aurumx.entity.Customer;
import com.aurumx.entity.Reward;
import com.aurumx.entity.RewardCategory;
import com.aurumx.entity.RewardItem;
import com.aurumx.entity.Transaction;
import com.aurumx.enums.CustomerType;
import com.aurumx.exception.ResourceNotFoundException;
import com.aurumx.repository.CustomerRepository;
import com.aurumx.repository.RewardRepository;
import com.aurumx.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {
    
    private final RewardRepository rewardRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final com.aurumx.repository.RewardCategoryRepository rewardCategoryRepository;
    private final com.aurumx.repository.RewardItemRepository rewardItemRepository;
    private final com.aurumx.repository.CreditCardRepository creditCardRepository;
    private final com.aurumx.repository.RedemptionHistoryRepository redemptionHistoryRepository;
    private final RewardConfig rewardConfig;
    
    /**
     * Process unprocessed transactions for a customer and calculate rewards
     * Uses configuration for reward percentages based on customer type
     * Transactions can only be processed once (idempotent)
     */
    @Transactional
    public RewardBalanceResponse processTransactions(Long customerId) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        List<Transaction> unprocessedTransactions = transactionRepository.findUnprocessedByCustomerId(customerId);
        
        if (unprocessedTransactions.isEmpty()) {
            log.info("No unprocessed transactions found for customer: {}", customerId);
            return getRewardBalance(customerId);
        }
        
        // Group transactions by credit card to update card-specific rewards
        java.util.Map<com.aurumx.entity.CreditCard, List<Transaction>> transactionsByCard = unprocessedTransactions.stream()
                .filter(t -> t.getCreditCard() != null)
                .collect(java.util.stream.Collectors.groupingBy(Transaction::getCreditCard));
                
        BigDecimal totalNewPoints = BigDecimal.ZERO;
        
        int rewardPercentage = customer.getCustomerType() == CustomerType.PREMIUM 
                ? rewardConfig.getPremiumPercentage() 
                : rewardConfig.getRegularPercentage();
        
        for (java.util.Map.Entry<com.aurumx.entity.CreditCard, List<Transaction>> entry : transactionsByCard.entrySet()) {
            com.aurumx.entity.CreditCard card = entry.getKey();
            List<Transaction> cardTransactions = entry.getValue();
            
    
            // Get or create reward account for this CARD
            Reward reward = rewardRepository.findByCreditCardId(card.getId())
                    .orElseGet(() -> {
                        Reward newReward = new Reward();
                        newReward.setCreditCard(card);
                        newReward.setCustomer(card.getCustomer()); // Set customer to satisfy DB constraint
                        newReward.setPointsBalance(BigDecimal.ZERO);
                        newReward.setLifetimeEarned(BigDecimal.ZERO);
                        return rewardRepository.save(newReward);
                    });
            
            BigDecimal cardNewPoints = BigDecimal.ZERO;
            
            for (Transaction transaction : cardTransactions) {
                BigDecimal rewardPoints = transaction.getAmount()
                        .multiply(BigDecimal.valueOf(rewardPercentage))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                
                cardNewPoints = cardNewPoints.add(rewardPoints);
                
                transaction.setProcessed(true);
                transaction.setRewardPoints(rewardPoints);
            }
            
            reward.setPointsBalance(reward.getPointsBalance().add(cardNewPoints));
            reward.setLifetimeEarned(reward.getLifetimeEarned().add(cardNewPoints));
            reward.setLastUpdated(LocalDateTime.now());
            
            rewardRepository.save(reward);
            totalNewPoints = totalNewPoints.add(cardNewPoints);
        }
        
        transactionRepository.saveAll(unprocessedTransactions);
        
        log.info("Processed rewards for customer {}. Total new points: {}", customer.getName(), totalNewPoints);
        
        return getRewardBalance(customerId);
    }

    /**
     * Process unprocessed transactions for a specific credit card
     */
    @Transactional
    public RewardBalanceResponse processTransactionsByCard(Long cardId) {
        com.aurumx.entity.CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found with id: " + cardId));
        
        Customer customer = card.getCustomer();
        
        List<Transaction> unprocessedTransactions = transactionRepository.findByCreditCardIdAndProcessedFalse(cardId);
        
        if (unprocessedTransactions.isEmpty()) {
            log.info("No unprocessed transactions found for card: {}", cardId);
            return getRewardBalance(customer.getId());
        }
        
        int rewardPercentage = customer.getCustomerType() == CustomerType.PREMIUM 
                ? rewardConfig.getPremiumPercentage() 
                : rewardConfig.getRegularPercentage();
        
        // Get or create reward account for this CARD
        Reward reward = rewardRepository.findByCreditCardId(cardId)
                .orElseGet(() -> {
                    Reward newReward = new Reward();
                    newReward.setCreditCard(card);
                    newReward.setCustomer(customer);
                    newReward.setPointsBalance(BigDecimal.ZERO);
                    newReward.setLifetimeEarned(BigDecimal.ZERO);
                    return rewardRepository.save(newReward);
                });
        
        BigDecimal cardNewPoints = BigDecimal.ZERO;
        
        for (Transaction transaction : unprocessedTransactions) {
            BigDecimal rewardPoints = transaction.getAmount()
                    .multiply(BigDecimal.valueOf(rewardPercentage))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            cardNewPoints = cardNewPoints.add(rewardPoints);
            
            transaction.setProcessed(true);
            transaction.setRewardPoints(rewardPoints);
        }
        
        reward.setPointsBalance(reward.getPointsBalance().add(cardNewPoints));
        reward.setLifetimeEarned(reward.getLifetimeEarned().add(cardNewPoints));
        reward.setLastUpdated(LocalDateTime.now());
        
        rewardRepository.save(reward);
        transactionRepository.saveAll(unprocessedTransactions);
        
        log.info("Processed rewards for card {}. New points: {}", cardId, cardNewPoints);
        
        return getRewardBalance(customer.getId());
    }
    
    public RewardBalanceResponse getRewardBalance(Long customerId) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        List<com.aurumx.entity.CreditCard> cards = creditCardRepository.findByCustomerIdAndDeletedFalse(customerId);
        List<Reward> rewards = rewardRepository.findByCreditCard_CustomerId(customerId);
        
        // Map rewards by card ID for easy lookup
        java.util.Map<Long, Reward> rewardMap = rewards.stream()
                .collect(java.util.stream.Collectors.toMap(r -> r.getCreditCard().getId(), r -> r));
        
        BigDecimal totalPoints = BigDecimal.ZERO;
        BigDecimal totalLifetime = BigDecimal.ZERO;
        java.util.List<RewardBalanceResponse.CardRewardDto> cardRewards = new java.util.ArrayList<>();
        
        for (com.aurumx.entity.CreditCard card : cards) {
            Reward reward = rewardMap.get(card.getId());
            BigDecimal points = (reward != null) ? reward.getPointsBalance() : BigDecimal.ZERO;
            
            if (reward != null) {
                totalPoints = totalPoints.add(reward.getPointsBalance());
                totalLifetime = totalLifetime.add(reward.getLifetimeEarned());
            }
            
            cardRewards.add(new RewardBalanceResponse.CardRewardDto(
                card.getId(),
                maskCardNumber(card.getCardNumber()),
                points
            ));
        }
        
        return new RewardBalanceResponse(
                customer.getId(),
                customer.getName(),
                totalPoints,
                totalLifetime,
                cardRewards
        );
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public List<RewardCategory> getCategories() {
        return rewardCategoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    public List<RewardItem> getRewards(Long categoryId) {
        if (categoryId != null) {
            return rewardItemRepository.findByCategoryIdAndAvailableTrue(categoryId);
        }
        return rewardItemRepository.findByAvailableTrue();
    }

    public List<com.aurumx.entity.RedemptionHistory> getRedemptionHistory(Long customerId) {
        return redemptionHistoryRepository.findByCustomerIdOrderByRedeemedAtDesc(customerId);
    }
}
