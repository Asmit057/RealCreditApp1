package com.aurumx.service;

import com.aurumx.config.TransactionConfig;
import com.aurumx.entity.CreditCard;
import com.aurumx.entity.Transaction;
import com.aurumx.exception.ResourceNotFoundException;
import com.aurumx.repository.CreditCardRepository;
import com.aurumx.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CreditCardRepository creditCardRepository;
    private final TransactionConfig transactionConfig;

    private static final String[] MERCHANTS = {

            "Amazon", "Flipkart", "Apple Store", "Louis Vuitton", "Gucci",
            "Rolex Boutique", "Tiffany & Co", "Burberry", "Prada", "Chanel",
            "Dior", "Cartier", "Samsung Experience Store", "Bose", "Bang & Olufsen",
            "Sony Center", "Microsoft Store", "Emirates Airlines", "Qatar Airways",
            "Singapore Airlines", "Lufthansa", "Marriott Hotels", "Hilton Hotels",
            "Taj Hotels", "The Leela Palace", "ITC Maurya", "Olive Bar & Kitchen",
            "Bukhara Restaurant", "Wasabi by Morimoto", "Tanishq", "Kalyan Jewellers",
            "Malabar Gold", "PC Jewellers", "Zara", "H&M Premium", "Marks & Spencer",
            "Harvey Nichols", "Selfridges"
    };

    /**
     * Generate random transactions for a credit card
     * Count is configured via TransactionConfig
     * Amount range is configured via TransactionConfig
     */
    @Transactional
    public List<Transaction> generateTransactions(Long creditCardId) {
        CreditCard creditCard = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found with id: " + creditCardId));

        List<Transaction> transactions = new ArrayList<>();
        Random random = new Random();

        int count = transactionConfig.getGenerationCount();
        int minAmount = transactionConfig.getMinAmount();
        int maxAmount = transactionConfig.getMaxAmount();

        for (int i = 0; i < count; i++) {
            Transaction transaction = new Transaction();
            transaction.setCreditCard(creditCard);

            // Generate random amount between min and max
            int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);
            transaction.setAmount(BigDecimal.valueOf(amount));

            // Random merchant
            transaction.setMerchant(MERCHANTS[random.nextInt(MERCHANTS.length)]);

            // Random date within last 30 days
            transaction.setTransactionDate(LocalDateTime.now().minusDays(random.nextInt(30)));

            transaction.setProcessed(false);

            transactions.add(transaction);
        }

        return transactionRepository.saveAll(transactions);
    }

    public org.springframework.data.domain.Page<Transaction> getTransactionsByCard(Long creditCardId, org.springframework.data.domain.Pageable pageable) {
        return transactionRepository.findByCreditCardId(creditCardId, pageable);
    }
}
