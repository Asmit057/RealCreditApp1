package com.aurumx.validator;

import com.aurumx.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class Validator {
    public static boolean calculateCustomerElegiblity(LocalDate dob) {
        int yearsAssociated = Period.between(dob, LocalDate.now()).getYears();

        if (yearsAssociated >= 18) {
            return true;
        }
        return false;
    }
    public static boolean isValidPhoneNumber(String phnNo) {
        if (phnNo== null) return false;
        String regex = "^[6-9]\\d{9}$";
        return phnNo.matches(regex);
    }
}
