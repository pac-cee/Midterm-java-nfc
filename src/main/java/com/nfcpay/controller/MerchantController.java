package com.nfcpay.controller;

import com.nfcpay.service.MerchantService;
import com.nfcpay.model.Merchant;
import com.nfcpay.exception.NFCPayException;
import java.util.List;

/**
 * Merchant Management Controller
 * Handles merchant operations through MerchantService
 */
public class MerchantController {
    private final MerchantService merchantService;
    
    public MerchantController() {
        this.merchantService = new MerchantService();
    }
    
    public List<Merchant> getActiveMerchants() throws NFCPayException {
        return merchantService.getActiveMerchants();
    }
    
    public List<Merchant> getMerchantsByCategory(String category) throws NFCPayException {
        return merchantService.getMerchantsByCategory(category);
    }
    
    public Merchant getMerchant(int merchantId) throws NFCPayException {
        return merchantService.getMerchant(merchantId);
    }
    
    public Merchant getMerchantByCode(String merchantCode) throws NFCPayException {
        return merchantService.getMerchantByCode(merchantCode);
    }
    
    public List<Merchant> searchMerchants(String searchTerm) throws NFCPayException {
        return merchantService.searchMerchants(searchTerm);
    }
    
    public void validateMerchantForPayment(int merchantId) throws NFCPayException {
        merchantService.validateMerchantForPayment(merchantId);
    }
    
    public List<String> getMerchantCategories() throws NFCPayException {
        return merchantService.getMerchantCategories();
    }
    
    public boolean isMerchantCodeAvailable(String merchantCode) throws NFCPayException {
        return merchantService.isMerchantCodeAvailable(merchantCode);
    }
}