package com.aurumx.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCustomerRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^(\\+91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "customer.phoneNumber.wanted" )
    private String phone;

    @NotNull(message = "customer.dateOfBirth.notnull")
    @Past(message = "customer.dob.past")
    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Association date is required")
    @PastOrPresent(message = "customer.associationDate.pastorpresent")
    @JsonProperty("associationDate")
    private LocalDate associationDate;

    private String imageUrl;
}
