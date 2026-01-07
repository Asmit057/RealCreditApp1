package com.aurumx.controller;

import com.aurumx.dto.request.GenerateTransactionsRequest;
import com.aurumx.entity.Transaction;
import com.aurumx.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping("/generate")
    public ResponseEntity<List<Transaction>> generateTransactions(@Valid @RequestBody GenerateTransactionsRequest request) {
        List<Transaction> transactions = transactionService.generateTransactions(request.getCreditCardId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transactions);
    }
    
    @GetMapping("/card/{cardId}")
    public ResponseEntity<Page<Transaction>> getTransactionsByCard(
            @PathVariable Long cardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionService.getTransactionsByCard(cardId, pageable);
        return ResponseEntity.ok(transactions);
    }
}
