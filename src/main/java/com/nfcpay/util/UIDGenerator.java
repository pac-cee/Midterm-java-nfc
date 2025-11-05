package com.nfcpay.util;

import java.util.Random;
import java.util.UUID;

/**
 * UID Generator for cards and transactions
 */
public class UIDGenerator {
    private static final Random random = new Random();
    
    public static String generateCardUID() {
        StringBuilder uid = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            uid.append(random.nextInt(10));
        }
        return uid.toString();
    }
    
    public static String generateTransactionReference() {
        return "TXN" + System.currentTimeMillis() + random.nextInt(1000);
    }
    
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}