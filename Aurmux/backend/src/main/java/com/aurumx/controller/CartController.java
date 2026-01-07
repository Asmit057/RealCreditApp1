package com.aurumx.controller;

import com.aurumx.dto.request.AddToCartRequest;
import com.aurumx.dto.response.CartResponse;
import com.aurumx.dto.response.RedemptionResponse;
import com.aurumx.entity.CartItem;
import com.aurumx.entity.RedemptionHistory;
import com.aurumx.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        CartResponse cart = cartService.getCart(customerId);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long itemId) {
        cartService.removeFromCart(itemId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Void> updateCartItemQuantity(@PathVariable Long itemId, @RequestParam int quantity) {
        cartService.updateCartItemQuantity(itemId, quantity);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{customerId}/redeem")
    public ResponseEntity<RedemptionResponse> redeemCart(
            @PathVariable Long customerId, 
            @RequestParam(required = true) Long creditCardId) {
        RedemptionResponse response = cartService.redeemCart(customerId, creditCardId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/redemption-history/{customerId}")
    public ResponseEntity<List<RedemptionHistory>> getRedemptionHistory(@PathVariable Long customerId) {
        List<RedemptionHistory> history = cartService.getRedemptionHistory(customerId);
        return ResponseEntity.ok(history);
    }
}
