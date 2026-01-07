package com.aurumx.service;

import com.aurumx.dto.request.AddToCartRequest;
import com.aurumx.dto.response.RedemptionResponse;
import com.aurumx.entity.*;
import com.aurumx.enums.RedemptionStatus;
import com.aurumx.exception.BusinessRuleViolationException;
import com.aurumx.exception.InsufficientRewardBalanceException;
import com.aurumx.exception.ResourceNotFoundException;
import com.aurumx.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final RewardItemRepository rewardItemRepository;
    private final CustomerRepository customerRepository;
    private final RewardRepository rewardRepository;
    private final RedemptionHistoryRepository redemptionHistoryRepository;
    
    @Transactional
    public void addToCart(AddToCartRequest request) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        RewardItem rewardItem = rewardItemRepository.findById(request.getRewardItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Reward item not found"));
        
        if (!rewardItem.isAvailable()) {
            throw new BusinessRuleViolationException("Reward item is not available");
        }
        
        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setRewardItem(rewardItem);
        cartItem.setQuantity(request.getQuantity());
        
        cartItemRepository.save(cartItem);
        log.info("Added {} x {} to cart for customer {}", 
                request.getQuantity(), rewardItem.getName(), customer.getName());
    }
    
    public com.aurumx.dto.response.CartResponse getCart(Long customerId) {
        List<CartItem> items = cartItemRepository.findByCustomerId(customerId);
        
        List<com.aurumx.dto.response.CartResponse.CartItemDto> itemDtos = new ArrayList<>();
        int totalPoints = 0;
        
        for (CartItem item : items) {
            int itemTotal = item.getRewardItem().getPointsCost() * item.getQuantity();
            totalPoints += itemTotal;
            
            itemDtos.add(new com.aurumx.dto.response.CartResponse.CartItemDto(
                item.getId(),
                item.getRewardItem().getId(),
                item.getRewardItem().getName(),
                item.getQuantity(),
                item.getRewardItem().getPointsCost(),
                itemTotal,
                item.getAddedAt()
            ));
        }
        
        return new com.aurumx.dto.response.CartResponse(
            customerId, // using customerId as cart ID roughly
            customerId,
            itemDtos,
            totalPoints
        );
    }
    
    @Transactional
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void updateCartItemQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            cartItemRepository.deleteById(cartItemId);
            return;
        }
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        // Ensure reward item is still available if increasing (optional check, but good practice)
        if (quantity > cartItem.getQuantity() && !cartItem.getRewardItem().isAvailable()) {
            throw new BusinessRuleViolationException("Reward item is no longer available");
        }
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
    
    /**
     * Redeem all cart items for a customer
     * All-or-nothing redemption - partial redemption not allowed
     * Balance can never go negative
     */
    @Transactional
    public RedemptionResponse redeemCart(Long customerId, Long creditCardId) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        List<CartItem> cartItems = cartItemRepository.findByCustomerId(customerId);
        
        if (cartItems.isEmpty()) {
            throw new BusinessRuleViolationException("Cart is empty");
        }
        
        // Get Reward account for the selected CARD
        Reward reward = rewardRepository.findByCreditCardId(creditCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward account not found for this card"));
                
        // Verify card belongs to customer
        if (!reward.getCreditCard().getCustomer().getId().equals(customerId)) {
            throw new BusinessRuleViolationException("Credit card does not belong to this customer");
        }
        
        // Calculate total points required
        BigDecimal totalPointsRequired = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            int itemTotal = cartItem.getRewardItem().getPointsCost() * cartItem.getQuantity();
            totalPointsRequired = totalPointsRequired.add(BigDecimal.valueOf(itemTotal));
        }
        
        // Check balance
        if (reward.getPointsBalance().compareTo(totalPointsRequired) < 0) {
            throw new com.aurumx.exception.InsufficientRewardBalanceException(
                    "Insufficient points balance. Required: " + totalPointsRequired + 
                    ", Available: " + reward.getPointsBalance()
            );
        }
        
        // Create redemption history
        RedemptionHistory redemptionHistory = new RedemptionHistory();
        redemptionHistory.setCustomer(customer);
        redemptionHistory.setTotalPointsUsed(totalPointsRequired);
        redemptionHistory.setStatus(RedemptionStatus.COMPLETED);
        
        List<RedemptionItem> redemptionItems = new ArrayList<>();
        List<RedemptionResponse.RedemptionItemDto> responseDtos = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            RedemptionItem redemptionItem = new RedemptionItem();
            redemptionItem.setRedemption(redemptionHistory);
            redemptionItem.setRewardItem(cartItem.getRewardItem());
            redemptionItem.setQuantity(cartItem.getQuantity());
            redemptionItem.setPointsCost(cartItem.getRewardItem().getPointsCost());
            redemptionItems.add(redemptionItem);
            
            int itemTotal = cartItem.getRewardItem().getPointsCost() * cartItem.getQuantity();
            responseDtos.add(new RedemptionResponse.RedemptionItemDto(
                    cartItem.getRewardItem().getName(),
                    cartItem.getQuantity(),
                    cartItem.getRewardItem().getPointsCost(),
                    itemTotal
            ));
        }
        
        redemptionHistory.setItems(redemptionItems);
        
        // Deduct points
        reward.setPointsBalance(reward.getPointsBalance().subtract(totalPointsRequired));
        reward.setLastUpdated(LocalDateTime.now());
        
        // Save
        RedemptionHistory savedRedemption = redemptionHistoryRepository.save(redemptionHistory);
        rewardRepository.save(reward);
        
        // Clear cart
        cartItemRepository.deleteAll(cartItems);
        
        return new RedemptionResponse(
                savedRedemption.getId(),
                customer.getId(),
                totalPointsRequired,
                savedRedemption.getRedeemedAt(),
                responseDtos
        );
    }
    
    public List<RedemptionHistory> getRedemptionHistory(Long customerId) {
        return redemptionHistoryRepository.findByCustomerIdOrderByRedeemedAtDesc(customerId);
    }
}
