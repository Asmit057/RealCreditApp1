package com.aurumx.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddCreditCardRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp="^[0-9]{16}$", message="Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    @Pattern(regexp="^[a-zA-Z ]{2,30}$", message = "Card Holder name length must be between 2 to 30")
    private String cardHolderName;

    @NotNull(message = "Expiry date is required")
    @FutureOrPresent(message = "CreditCard.expiryDate.futureorpresent")
    private LocalDate expiryDate;
}
