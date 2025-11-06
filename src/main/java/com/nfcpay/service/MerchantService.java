package com.nfcpay.service;

import com.nfcpay.dao.MerchantDAO;
import com.nfcpay.model.Merchant;
import com.nfcpay.exception.ValidationException;
import com.nfcpay.exception.NFCPayException;
import java.util.List;
import java.util.ArrayList;

/**
 * Merchant Management Service
 */
public class MerchantService {
    private final MerchantDAO merchantDAO;
    
    public MerchantService() {
        this.merchantDAO = new MerchantDAO();
    }
    
    /**
     * Get all active merchants
     */
    public List<Merchant> getActiveMerchants() throws NFCPayException {
        return merchantDAO.getActiveMerchants();
    }
    
    /**
     * Get merchants by category
     */
    public List<Merchant> getMerchantsByCategory(String category) throws NFCPayException {
        ValidationService.validateStringLength(category, "Category", 1, 50);
        return merchantDAO.getMerchantsByCategory(category);
    }
    
    /**
     * Get merchant by ID
     */
    public Merchant getMerchant(int merchantId) throws NFCPayException {
        ValidationService.validatePositiveInteger(merchantId, "Merchant ID");
        
        Merchant merchant = merchantDAO.getMerchantById(merchantId);
        if (merchant == null) {
            throw new ValidationException("Merchant not found");
        }
        
        return merchant;
    }
    
    /**
     * Get merchant by code
     */
    public Merchant getMerchantByCode(String merchantCode) throws NFCPayException {
        ValidationService.validateMerchantCode(merchantCode);
        
        Merchant merchant = merchantDAO.getMerchantByCode(merchantCode.toUpperCase());
        if (merchant == null) {
            throw new ValidationException("Merchant not found with code: " + merchantCode);
        }
        
        return merchant;
    }
    
    /**
     * Search merchants by name
     */
    public List<Merchant> searchMerchants(String searchTerm) throws NFCPayException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new ValidationException("Search term is required");
        }
        
        if (searchTerm.trim().length() < 2) {
            throw new ValidationException("Search term must be at least 2 characters");
        }
        
        // Since searchByName doesn't exist, return all merchants and filter by name
        List<Merchant> allMerchants = merchantDAO.getActiveMerchants();
        List<Merchant> filteredMerchants = new ArrayList<>();
        String searchLower = searchTerm.trim().toLowerCase();
        
        for (Merchant merchant : allMerchants) {
            if (merchant.getMerchantName().toLowerCase().contains(searchLower)) {
                filteredMerchants.add(merchant);
            }
        }
        
        return filteredMerchants;
    }
    
    /**
     * Validate merchant for payment
     */
    public void validateMerchantForPayment(int merchantId) throws NFCPayException {
        Merchant merchant = getMerchant(merchantId);
        
        if (!merchant.isActive()) {
            throw new ValidationException("This merchant is currently not accepting payments");
        }
        
        if (!merchant.canAcceptPayments()) {
            throw new ValidationException("Merchant is temporarily unavailable for payments");
        }
    }
    
    /**
     * Get merchant categories
     */
    public List<String> getMerchantCategories() throws NFCPayException {
        return merchantDAO.getAllCategories();
    }
    
    /**
     * Check if merchant code is available
     */
    public boolean isMerchantCodeAvailable(String merchantCode) throws NFCPayException {
        ValidationService.validateMerchantCode(merchantCode);
        return merchantDAO.getMerchantByCode(merchantCode.toUpperCase()) == null;
    }
    
    /**
     * Get all merchants (active and inactive)
     */
    public List<Merchant> getAllMerchants() throws NFCPayException {
        return merchantDAO.getAllMerchants();
    }
}