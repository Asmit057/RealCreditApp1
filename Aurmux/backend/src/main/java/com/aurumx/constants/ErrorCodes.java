package com.aurumx.constants;

public class ErrorCodes {
    // Authentication & Authorization
    public static final String UNAUTHORIZED = "ERR_001";
    public static final String INVALID_CREDENTIALS = "ERR_002";
    public static final String ACCESS_DENIED = "ERR_003";
    public static final String SELF_DELETE_NOT_ALLOWED = "ERR_004";
    
    // Resource Not Found
    public static final String CUSTOMER_NOT_FOUND = "ERR_101";
    public static final String CES_USER_NOT_FOUND = "ERR_102";
    public static final String CREDIT_CARD_NOT_FOUND = "ERR_103";
    public static final String TRANSACTION_NOT_FOUND = "ERR_104";
    public static final String REWARD_ITEM_NOT_FOUND = "ERR_105";
    public static final String CART_ITEM_NOT_FOUND = "ERR_106";
    
    // Business Rule Violations
    public static final String DUPLICATE_CREDIT_CARD = "ERR_201";
    public static final String DUPLICATE_USERNAME = "ERR_202";
    public static final String DUPLICATE_EMAIL = "ERR_203";
    public static final String TRANSACTION_ALREADY_PROCESSED = "ERR_204";
    public static final String INSUFFICIENT_REWARD_BALANCE = "ERR_205";
    public static final String EMPTY_CART = "ERR_206";
    public static final String PARTIAL_REDEMPTION_NOT_ALLOWED = "ERR_207";
    public static final String INVALID_TRANSACTION_AMOUNT = "ERR_208";
    
    private ErrorCodes() {
        // Private constructor to prevent instantiation
    }
}
