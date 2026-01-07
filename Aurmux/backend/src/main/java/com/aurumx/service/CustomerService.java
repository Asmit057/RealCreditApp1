package com.aurumx.service;

import com.aurumx.config.CustomerConfig;
import com.aurumx.dto.request.CreateCustomerRequest;
import com.aurumx.dto.response.ApiResponse;
import com.aurumx.dto.response.CustomerResponse;
import com.aurumx.entity.Customer;
import com.aurumx.entity.Reward;
import com.aurumx.enums.CustomerType;
import com.aurumx.exception.BusinessRuleViolationException;
import com.aurumx.exception.ResourceNotFoundException;
import com.aurumx.repository.CustomerRepository;
import com.aurumx.repository.RewardRepository;
import com.aurumx.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static com.aurumx.validator.Validator.calculateCustomerElegiblity;
import static com.aurumx.validator.Validator.isValidPhoneNumber;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final Validator validator;
    private final CustomerRepository customerRepository;
    private final RewardRepository rewardRepository;
    private final CustomerConfig customerConfig;

    @Transactional
    public ApiResponse<CustomerResponse> createCustomer(CreateCustomerRequest request) {


        if (customerRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new BusinessRuleViolationException(
                    "Customer with email " + request.getEmail() + " already exists"
            );
        }


        var deletedCustomerOpt = customerRepository.findByEmailAndDeletedTrue(request.getEmail());

        if (deletedCustomerOpt.isPresent()) {
            Customer customer = deletedCustomerOpt.get();

            //DO NOT overwrite fields
            customer.setDeleted(false);

            Customer restored = customerRepository.save(customer);

            return new ApiResponse<>(
                    "Customer restored successfully",
                    mapToResponse(restored)
            );
        }
        if (!isValidPhoneNumber(request.getPhone())) {
            throw new BusinessRuleViolationException("Please enter 10 digit's valid Phone number");
        }

        if (!calculateCustomerElegiblity(request.getDateOfBirth())) {
            throw new BusinessRuleViolationException("Only above 18 years old customers are allowed");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAssociationDate(request.getAssociationDate());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setCustomerType(calculateCustomerType(request.getAssociationDate()));
        customer.setImageUrl(
                customer.getImageUrl() != null && !customer.getImageUrl().isBlank()
                        ? customer.getImageUrl()
                        : "https://drive.google.com/file/d/1qWxtixKccOxdFnuqrv4tjJFUaGlXk_sF/view?usp=drive_link"
        );
        customer.setDeleted(false);

        // TODO: replace with logged CES user later
        customer.setCreatedBy(1L);

        Customer savedCustomer = customerRepository.save(customer);

        return new ApiResponse<>(
                "Customer created successfully",
                mapToResponse(savedCustomer)
        );
    }

    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        return mapToResponse(customer);
    }

    public Page<CustomerResponse> searchByName(String name, Pageable pageable) {
        return customerRepository.searchByName(name, pageable)
                .map(this::mapToResponse);
    }

    public Page<CustomerResponse> searchByCardNumber(String cardNumber, Pageable pageable) {
        return customerRepository.searchByCardNumber(cardNumber, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    private CustomerType calculateCustomerType(LocalDate associationDate) {
        int yearsAssociated = Period.between(associationDate, LocalDate.now()).getYears();

        if (yearsAssociated >= customerConfig.getPremiumAssociationYears()) {
            return CustomerType.PREMIUM;
        }
        return CustomerType.REGULAR;
    }

    private CustomerResponse mapToResponse(Customer customer) {

        BigDecimal rewardBalance = rewardRepository
                .findByCreditCard_CustomerId(customer.getId())
                .stream()
                .map(Reward::getPointsBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getDateOfBirth(),
                customer.getAssociationDate(),
                customer.getCustomerType(),
                customer.getImageUrl(),
                rewardBalance
        );
    }
}
