# 🏗️ Complete File Structure & Implementation Guide

## Project: NFC Pay - Smart Tap & Pay Payment System

**Student**: Pacifique Bakundukize (26798)
**Course**: INSY 7312 - Java Programming

---

## 📁 Complete Directory Structure

```
NFC_Payment_System/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── nfcpay/
│       │           ├── model/
│       │           │   ├── User.java
│       │           │   ├── Wallet.java
│       │           │   ├── Card.java
│       │           │   ├── Transaction.java
│       │           │   ├── Merchant.java
│       │           │   └── enums/
│       │           │       ├── CardType.java
│       │           │       ├── TransactionType.java
│       │           │       ├── TransactionStatus.java
│       │           │       └── Currency.java
│       │           │
│       │           ├── dao/
│       │           │   ├── DatabaseConnection.java
│       │           │   ├── interfaces/
│       │           │   │   ├── UserDAO.java
│       │           │   │   ├── WalletDAO.java
│       │           │   │   ├── CardDAO.java
│       │           │   │   ├── TransactionDAO.java
│       │           │   │   └── MerchantDAO.java
│       │           │   └── impl/
│       │           │       ├── UserDAOImpl.java
│       │           │       ├── WalletDAOImpl.java
│       │           │       ├── CardDAOImpl.java
│       │           │       ├── TransactionDAOImpl.java
│       │           │       └── MerchantDAOImpl.java
│       │           │
│       │           ├── service/
│       │           │   ├── AuthService.java
│       │           │   ├── WalletService.java
│       │           │   ├── CardService.java
│       │           │   ├── PaymentService.java
│       │           │   ├── TransactionService.java
│       │           │   ├── MerchantService.java
│       │           │   ├── ValidationService.java
│       │           │   └── NFCService.java
│       │           │
│       │           ├── controller/
│       │           │   ├── AuthController.java
│       │           │   ├── DashboardController.java
│       │           │   ├── WalletController.java
│       │           │   ├── CardController.java
│       │           │   ├── PaymentController.java
│       │           │   ├── TransactionController.java
│       │           │   └── MerchantController.java
│       │           │
│       │           ├── view/
│       │           │   ├── frames/
│       │           │   │   ├── LoginFrame.java
│       │           │   │   ├── RegisterFrame.java
│       │           │   │   └── MainFrame.java
│       │           │   ├── panels/
│       │           │   │   ├── DashboardPanel.java
│       │           │   │   ├── WalletPanel.java
│       │           │   │   ├── CardManagementPanel.java
│       │           │   │   ├── PaymentPanel.java
│       │           │   │   ├── TransactionHistoryPanel.java
│       │           │   │   └── MerchantPanel.java
│       │           │   ├── components/
│       │           │   │   ├── CustomButton.java
│       │           │   │   ├── CustomTextField.java
│       │           │   │   ├── CustomTable.java
│       │           │   │   ├── CustomComboBox.java
│       │           │   │   └── CustomPanel.java
│       │           │   └── dialogs/
│       │           │       ├── AddCardDialog.java
│       │           │       ├── EditCardDialog.java
│       │           │       ├── PaymentConfirmDialog.java
│       │           │       └── TransactionDetailsDialog.java
│       │           │
│       │           ├── util/
│       │           │   ├── Session.java
│       │           │   ├── PasswordUtil.java
│       │           │   ├── UIDGenerator.java
│       │           │   ├── DateUtil.java
│       │           │   ├── CurrencyUtil.java
│       │           │   ├── ValidationUtil.java
│       │           │   └── UIConstants.java
│       │           │
│       │           └── Main.java
│       │
│       └── resources/
│           ├── database/
│           │   ├── schema.sql
│           │   └── sample_data.sql
│           ├── config/
│           │   └── database.properties
│           └── icons/
│               ├── app_icon.png
│               ├── card_icon.png
│               ├── payment_icon.png
│               └── transaction_icon.png
│
├── lib/
│   ├── postgresql-42.6.0.jar
│   └── flatlaf-3.2.5.jar
├── docs/
│   ├── README.md
│   ├── system-design.md
│   ├── database-schema.md
│   ├── implementation-plan.md
│   └── user-manual.md
└── 
```

---

## 🏛️ Model Layer (Data Objects)

### 1. User.java

```java
public class User {
    // Fields
    private int userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
  
    // Constructors
    public User();
    public User(String fullName, String email, String passwordHash, String phone);
    public User(int userId, String fullName, String email, String passwordHash, String phone, boolean isActive, LocalDateTime createdAt, LocalDateTime lastLogin);
  
    // Getters & Setters
    public int getUserId();
    public void setUserId(int userId);
    public String getFullName();
    public void setFullName(String fullName);
    public String getEmail();
    public void setEmail(String email);
    public String getPasswordHash();
    public void setPasswordHash(String passwordHash);
    public String getPhone();
    public void setPhone(String phone);
    public boolean isActive();
    public void setActive(boolean active);
    public LocalDateTime getCreatedAt();
    public void setCreatedAt(LocalDateTime createdAt);
    public LocalDateTime getLastLogin();
    public void setLastLogin(LocalDateTime lastLogin);
  
    // Utility Methods
    @Override
    public String toString();
    @Override
    public boolean equals(Object obj);
    @Override
    public int hashCode();
}
```

### 2. Wallet.java

```java
public class Wallet {
    // Fields
    private int walletId;
    private int userId;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
  
    // Constructors
    public Wallet();
    public Wallet(int userId, BigDecimal balance, Currency currency);
    public Wallet(int walletId, int userId, BigDecimal balance, Currency currency, LocalDateTime createdAt, LocalDateTime updatedAt);
  
    // Getters & Setters
    public int getWalletId();
    public void setWalletId(int walletId);
    public int getUserId();
    public void setUserId(int userId);
    public BigDecimal getBalance();
    public void setBalance(BigDecimal balance);
    public Currency getCurrency();
    public void setCurrency(Currency currency);
    public LocalDateTime getCreatedAt();
    public void setCreatedAt(LocalDateTime createdAt);
    public LocalDateTime getUpdatedAt();
    public void setUpdatedAt(LocalDateTime updatedAt);
  
    // Business Methods
    public boolean hasSufficientBalance(BigDecimal amount);
    public void addFunds(BigDecimal amount);
    public void deductFunds(BigDecimal amount);
    public String getFormattedBalance();
  
    // Utility Methods
    @Override
    public String toString();
    @Override
    public boolean equals(Object obj);
    @Override
    public int hashCode();
}
```

### 3. Card.java

```java
public class Card {
    // Fields
    private int cardId;
    private int userId;
    private String cardUid;
    private String cardName;
    private CardType cardType;
    private boolean isActive;
    private LocalDateTime createdAt;
  
    // Constructors
    public Card();
    public Card(int userId, String cardUid, String cardName, CardType cardType);
    public Card(int cardId, int userId, String cardUid, String cardName, CardType cardType, boolean isActive, LocalDateTime createdAt);
  
    // Getters & Setters
    public int getCardId();
    public void setCardId(int cardId);
    public int getUserId();
    public void setUserId(int userId);
    public String getCardUid();
    public void setCardUid(String cardUid);
    public String getCardName();
    public void setCardName(String cardName);
    public CardType getCardType();
    public void setCardType(CardType cardType);
    public boolean isActive();
    public void setActive(boolean active);
    public LocalDateTime getCreatedAt();
    public void setCreatedAt(LocalDateTime createdAt);
  
    // Business Methods
    public void activate();
    public void deactivate();
    public boolean canProcessPayment();
    public String getDisplayName();
  
    // Utility Methods
    @Override
    public String toString();
    @Override
    public boolean equals(Object obj);
    @Override
    public int hashCode();
}
```

### 4. Transaction.java

```java
public class Transaction {
    // Fields
    private int transactionId;
    private int userId;
    private int cardId;
    private int merchantId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus status;
    private String referenceCode;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
  
    // Constructors
    public Transaction();
    public Transaction(int userId, int cardId, int merchantId, BigDecimal amount, TransactionType transactionType, String referenceCode, String description);
    public Transaction(int transactionId, int userId, int cardId, int merchantId, BigDecimal amount, TransactionType transactionType, TransactionStatus status, String referenceCode, String description, LocalDateTime createdAt, LocalDateTime processedAt);
  
    // Getters & Setters
    public int getTransactionId();
    public void setTransactionId(int transactionId);
    public int getUserId();
    public void setUserId(int userId);
    public int getCardId();
    public void setCardId(int cardId);
    public int getMerchantId();
    public void setMerchantId(int merchantId);
    public BigDecimal getAmount();
    public void setAmount(BigDecimal amount);
    public TransactionType getTransactionType();
    public void setTransactionType(TransactionType transactionType);
    public TransactionStatus getStatus();
    public void setStatus(TransactionStatus status);
    public String getReferenceCode();
    public void setReferenceCode(String referenceCode);
    public String getDescription();
    public void setDescription(String description);
    public LocalDateTime getCreatedAt();
    public void setCreatedAt(LocalDateTime createdAt);
    public LocalDateTime getProcessedAt();
    public void setProcessedAt(LocalDateTime processedAt);
  
    // Business Methods
    public void markAsSuccess();
    public void markAsFailed();
    public void markAsCancelled();
    public boolean isPending();
    public boolean isSuccessful();
    public String getFormattedAmount();
    public String getStatusDisplay();
  
    // Utility Methods
    @Override
    public String toString();
    @Override
    public boolean equals(Object obj);
    @Override
    public int hashCode();
}
```

### 5. Merchant.java

```java
public class Merchant {
    // Fields
    private int merchantId;
    private String merchantName;
    private String merchantCode;
    private String category;
    private boolean isActive;
    private LocalDateTime createdAt;
  
    // Constructors
    public Merchant();
    public Merchant(String merchantName, String merchantCode, String category);
    public Merchant(int merchantId, String merchantName, String merchantCode, String category, boolean isActive, LocalDateTime createdAt);
  
    // Getters & Setters
    public int getMerchantId();
    public void setMerchantId(int merchantId);
    public String getMerchantName();
    public void setMerchantName(String merchantName);
    public String getMerchantCode();
    public void setMerchantCode(String merchantCode);
    public String getCategory();
    public void setCategory(String category);
    public boolean isActive();
    public void setActive(boolean active);
    public LocalDateTime getCreatedAt();
    public void setCreatedAt(LocalDateTime createdAt);
  
    // Business Methods
    public void activate();
    public void deactivate();
    public boolean canAcceptPayments();
    public String getDisplayName();
  
    // Utility Methods
    @Override
    public String toString();
    @Override
    public boolean equals(Object obj);
    @Override
    public int hashCode();
}
```

---

## 🔗 DAO Layer (Data Access Objects)

### 1. UserDAO.java (Interface)

```java
public interface UserDAO {
    // Create Operations
    boolean createUser(User user);
  
    // Read Operations
    User getUserById(int userId);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    List<User> getActiveUsers();
  
    // Update Operations
    boolean updateUser(User user);
    boolean updateLastLogin(int userId);
    boolean updatePassword(int userId, String newPasswordHash);
    boolean activateUser(int userId);
    boolean deactivateUser(int userId);
  
    // Delete Operations
    boolean deleteUser(int userId);
  
    // Authentication
    User authenticateUser(String email, String passwordHash);
  
    // Validation
    boolean emailExists(String email);
    int getUserCount();
}
```

### 2. WalletDAO.java (Interface)

```java
public interface WalletDAO {
    // Create Operations
    boolean createWallet(Wallet wallet);
  
    // Read Operations
    Wallet getWalletById(int walletId);
    Wallet getWalletByUserId(int userId);
    BigDecimal getBalance(int userId);
  
    // Update Operations
    boolean updateBalance(int userId, BigDecimal newBalance);
    boolean addFunds(int userId, BigDecimal amount);
    boolean deductFunds(int userId, BigDecimal amount);
    boolean updateCurrency(int userId, Currency currency);
  
    // Delete Operations
    boolean deleteWallet(int walletId);
  
    // Validation
    boolean hasSufficientBalance(int userId, BigDecimal amount);
    boolean walletExists(int userId);
}
```

### 3. CardDAO.java (Interface)

```java
public interface CardDAO {
    // Create Operations
    boolean createCard(Card card);
  
    // Read Operations
    Card getCardById(int cardId);
    List<Card> getCardsByUserId(int userId);
    List<Card> getActiveCardsByUserId(int userId);
    Card getCardByUid(String cardUid);
  
    // Update Operations
    boolean updateCard(Card card);
    boolean activateCard(int cardId);
    boolean deactivateCard(int cardId);
    boolean updateCardName(int cardId, String newName);
  
    // Delete Operations
    boolean deleteCard(int cardId);
  
    // Validation
    boolean cardUidExists(String cardUid);
    boolean cardBelongsToUser(int cardId, int userId);
    int getActiveCardCount(int userId);
    boolean canAddMoreCards(int userId);
}
```

### 4. TransactionDAO.java (Interface)

```java
public interface TransactionDAO {
    // Create Operations
    boolean createTransaction(Transaction transaction);
  
    // Read Operations
    Transaction getTransactionById(int transactionId);
    List<Transaction> getTransactionsByUserId(int userId);
    List<Transaction> getTransactionsByCardId(int cardId);
    List<Transaction> getTransactionsByMerchantId(int merchantId);
    List<Transaction> getRecentTransactions(int userId, int limit);
    List<Transaction> getTransactionsByDateRange(int userId, LocalDate startDate, LocalDate endDate);
    List<Transaction> getTransactionsByStatus(int userId, TransactionStatus status);
  
    // Update Operations
    boolean updateTransactionStatus(int transactionId, TransactionStatus status);
    boolean updateProcessedTime(int transactionId);
  
    // Delete Operations
    boolean deleteTransaction(int transactionId);
  
    // Search Operations
    List<Transaction> searchTransactions(int userId, String searchTerm);
    List<Transaction> filterTransactions(int userId, TransactionStatus status, LocalDate startDate, LocalDate endDate);
  
    // Analytics
    BigDecimal getTotalSpent(int userId, LocalDate startDate, LocalDate endDate);
    int getTransactionCount(int userId);
    Map<String, BigDecimal> getSpendingByCategory(int userId, LocalDate startDate, LocalDate endDate);
  
    // Validation
    boolean referenceCodeExists(String referenceCode);
}
```

### 5. MerchantDAO.java (Interface)

```java
public interface MerchantDAO {
    // Create Operations
    boolean createMerchant(Merchant merchant);
  
    // Read Operations
    Merchant getMerchantById(int merchantId);
    Merchant getMerchantByCode(String merchantCode);
    List<Merchant> getAllMerchants();
    List<Merchant> getActiveMerchants();
    List<Merchant> getMerchantsByCategory(String category);
  
    // Update Operations
    boolean updateMerchant(Merchant merchant);
    boolean activateMerchant(int merchantId);
    boolean deactivateMerchant(int merchantId);
  
    // Delete Operations
    boolean deleteMerchant(int merchantId);
  
    // Validation
    boolean merchantCodeExists(String merchantCode);
    boolean merchantNameExists(String merchantName);
    int getMerchantCount();
  
    // Analytics
    List<String> getAllCategories();
    Map<String, Integer> getMerchantCountByCategory();
}
```

---

## ⚙️ Service Layer (Business Logic)

### 1. AuthService.java

```java
public class AuthService {
    private UserDAO userDAO;
    private WalletDAO walletDAO;
    private ValidationService validationService;
  
    // Constructor
    public AuthService();
  
    // Authentication Methods
    public User authenticateUser(String email, String password);
    public boolean registerUser(String fullName, String email, String password, String confirmPassword, String phone);
    public void logoutUser();
    public boolean changePassword(int userId, String oldPassword, String newPassword);
  
    // Session Management
    public boolean isUserLoggedIn();
    public User getCurrentUser();
    public void updateLastLogin(int userId);
  
    // Account Management
    public boolean activateAccount(int userId);
    public boolean deactivateAccount(int userId);
    public boolean updateProfile(int userId, String fullName, String phone);
  
    // Validation
    public List<String> validateRegistration(String fullName, String email, String password, String confirmPassword, String phone);
    public List<String> validateLogin(String email, String password);
    public boolean isEmailAvailable(String email);
  
    // Password Management
    private String hashPassword(String password);
    private boolean verifyPassword(String password, String hash);
    private boolean isValidPassword(String password);
}
```

### 2. WalletService.java

```java
public class WalletService {
    private WalletDAO walletDAO;
    private ValidationService validationService;
  
    // Constructor
    public WalletService();
  
    // Wallet Operations
    public boolean createWallet(int userId, Currency currency);
    public Wallet getUserWallet(int userId);
    public BigDecimal getBalance(int userId);
    public String getFormattedBalance(int userId);
  
    // Fund Management
    public boolean addFunds(int userId, BigDecimal amount);
    public boolean deductFunds(int userId, BigDecimal amount);
    public boolean transferFunds(int fromUserId, int toUserId, BigDecimal amount);
  
    // Currency Operations
    public boolean changeCurrency(int userId, Currency newCurrency);
    public BigDecimal convertCurrency(BigDecimal amount, Currency fromCurrency, Currency toCurrency);
  
    // Validation
    public boolean hasSufficientBalance(int userId, BigDecimal amount);
    public List<String> validateFundOperation(BigDecimal amount);
    public boolean isValidAmount(BigDecimal amount);
  
    // Analytics
    public Map<String, Object> getWalletSummary(int userId);
    public List<BigDecimal> getBalanceHistory(int userId, int days);
}
```

### 3. CardService.java

```java
public class CardService {
    private CardDAO cardDAO;
    private ValidationService validationService;
    private NFCService nfcService;
  
    // Constructor
    public CardService();
  
    // Card Management
    public boolean addCard(int userId, String cardName, CardType cardType);
    public boolean updateCard(int cardId, String newName);
    public boolean deleteCard(int cardId);
    public List<Card> getUserCards(int userId);
    public List<Card> getActiveUserCards(int userId);
  
    // Card Operations
    public boolean activateCard(int cardId);
    public boolean deactivateCard(int cardId);
    public Card getCardById(int cardId);
    public Card getCardByUid(String cardUid);
  
    // Validation
    public List<String> validateCardCreation(int userId, String cardName, CardType cardType);
    public boolean canAddMoreCards(int userId);
    public boolean cardBelongsToUser(int cardId, int userId);
    public boolean isCardActive(int cardId);
  
    // NFC Operations
    public String generateCardUid();
    public boolean simulateNFCTap(String cardUid);
    public boolean validateCardForPayment(int cardId, int userId);
  
    // Analytics
    public int getActiveCardCount(int userId);
    public int getTotalCardCount(int userId);
    public Map<CardType, Integer> getCardTypeDistribution(int userId);
}
```

### 4. PaymentService.java

```java
public class PaymentService {
    private TransactionDAO transactionDAO;
    private WalletDAO walletDAO;
    private CardDAO cardDAO;
    private MerchantDAO merchantDAO;
    private ValidationService validationService;
  
    // Constructor
    public PaymentService();
  
    // Payment Processing
    public Transaction processPayment(int userId, int cardId, int merchantId, BigDecimal amount, String description);
    public boolean simulateNFCPayment(String cardUid, int merchantId, BigDecimal amount, String description);
    public String generateTransactionReference();
  
    // Payment Validation
    public List<String> validatePaymentRequest(int userId, int cardId, int merchantId, BigDecimal amount);
    public boolean canProcessPayment(int userId, int cardId, int merchantId, BigDecimal amount);
  
    // Transaction Management
    public boolean updateTransactionStatus(int transactionId, TransactionStatus status);
    public boolean cancelTransaction(int transactionId);
    public boolean refundTransaction(int transactionId);
  
    // Payment Limits
    public boolean isWithinDailyLimit(int userId, BigDecimal amount);
    public boolean isWithinTransactionLimit(BigDecimal amount);
    public BigDecimal getDailySpent(int userId);
    public BigDecimal getRemainingDailyLimit(int userId);
  
    // Payment Analytics
    public Map<String, Object> getPaymentSummary(int userId);
    public List<Transaction> getFailedPayments(int userId);
    public BigDecimal getTotalSpent(int userId, LocalDate startDate, LocalDate endDate);
}
```

### 5. TransactionService.java

```java
public class TransactionService {
    private TransactionDAO transactionDAO;
    private CardDAO cardDAO;
    private MerchantDAO merchantDAO;
    private ValidationService validationService;
  
    // Constructor
    public TransactionService();
  
    // Transaction Retrieval
    public List<Transaction> getUserTransactions(int userId);
    public List<Transaction> getRecentTransactions(int userId, int limit);
    public Transaction getTransactionById(int transactionId);
    public List<Transaction> getTransactionsByDateRange(int userId, LocalDate startDate, LocalDate endDate);
  
    // Search and Filter
    public List<Transaction> searchTransactions(int userId, String searchTerm);
    public List<Transaction> filterByStatus(int userId, TransactionStatus status);
    public List<Transaction> filterByMerchant(int userId, int merchantId);
    public List<Transaction> filterByAmount(int userId, BigDecimal minAmount, BigDecimal maxAmount);
  
    // Transaction Details
    public Map<String, Object> getTransactionDetails(int transactionId);
    public String getTransactionSummary(int transactionId);
    public List<Transaction> getRelatedTransactions(int transactionId);
  
    // Export Operations
    public String exportTransactionsToCSV(int userId, LocalDate startDate, LocalDate endDate);
    public byte[] exportTransactionsToPDF(int userId, LocalDate startDate, LocalDate endDate);
  
    // Analytics
    public Map<String, Object> getTransactionAnalytics(int userId);
    public Map<String, BigDecimal> getSpendingByCategory(int userId, LocalDate startDate, LocalDate endDate);
    public Map<String, Integer> getTransactionCountByStatus(int userId);
    public List<Map<String, Object>> getMonthlySpendingTrend(int userId, int months);
}
```

### 6. ValidationService.java

```java
public class ValidationService {
    // Constructor
    public ValidationService();
  
    // Business Validations (5 Required)
    public boolean validateUserAuthentication(String email, String password);
    public boolean validateCardOwnership(int cardId, int userId);
    public boolean validateSufficientBalance(int userId, BigDecimal amount);
    public boolean validateCardStatus(int cardId);
    public boolean validateTransactionLimits(int userId, BigDecimal amount);
  
    // Technical Validations (5 Required)
    public boolean isValidEmail(String email);
    public boolean isValidPassword(String password);
    public boolean isValidPhone(String phone);
    public boolean isValidAmount(String amount);
    public boolean isValidCardUID(String cardUid);
  
    // Input Validation
    public List<String> validateUserInput(String fullName, String email, String password, String phone);
    public List<String> validateCardInput(String cardName, String cardType);
    public List<String> validatePaymentInput(String amount, String cardId, String merchantId);
  
    // Format Validation
    public boolean isValidDateRange(LocalDate startDate, LocalDate endDate);
    public boolean isValidCurrency(String currency);
    public boolean isValidTransactionReference(String reference);
  
    // Business Rule Validation
    public boolean canUserAddCard(int userId);
    public boolean canProcessTransaction(int userId, int cardId, int merchantId);
    public boolean isWithinBusinessHours();
    public boolean isValidMerchantCategory(String category);
  
    // Security Validation
    public boolean isSecurePassword(String password);
    public boolean isValidSession(int userId);
    public boolean hasValidPermissions(int userId, String operation);
}
```

---

## 🎮 Controller Layer (Event Handlers)

### 1. AuthController.java

```java
public class AuthController {
    private AuthService authService;
    private LoginFrame loginFrame;
    private RegisterFrame registerFrame;
    private MainFrame mainFrame;
  
    // Constructor
    public AuthController();
  
    // Authentication Handlers
    public void handleLogin(String email, String password);
    public void handleRegister(String fullName, String email, String password, String confirmPassword, String phone);
    public void handleLogout();
    public void handleForgotPassword(String email);
  
    // Navigation Handlers
    public void showLoginFrame();
    public void showRegisterFrame();
    public void showMainFrame();
    public void switchToRegister();
    public void switchToLogin();
  
    // Validation Handlers
    public void validateLoginForm(String email, String password);
    public void validateRegisterForm(String fullName, String email, String password, String confirmPassword, String phone);
  
    // UI Update Methods
    public void updateLoginStatus(boolean success, String message);
    public void updateRegistrationStatus(boolean success, String message);
    public void clearLoginForm();
    public void clearRegisterForm();
  
    // Error Handling
    public void handleAuthenticationError(String errorMessage);
    public void handleRegistrationError(List<String> errors);
    public void showSuccessMessage(String message);
    public void showErrorMessage(String message);
}
```

### 2. DashboardController.java

```java
public class DashboardController {
    private WalletService walletService;
    private CardService cardService;
    private TransactionService transactionService;
    private DashboardPanel dashboardPanel;
  
    // Constructor
    public DashboardController();
  
    // Dashboard Data Loading
    public void loadDashboardData();
    public void refreshDashboard();
    public void loadUserSummary();
    public void loadRecentTransactions();
    public void loadQuickStats();
  
    // Quick Action Handlers
    public void handleQuickPayment();
    public void handleAddCard();
    public void handleViewAllTransactions();
    public void handleTopUpWallet();
    public void handleViewProfile();
  
    // Data Refresh Methods
    public void refreshBalance();
    public void refreshCardCount();
    public void refreshTransactionCount();
    public void refreshRecentActivity();
  
    // Navigation Handlers
    public void navigateToCards();
    public void navigateToTransactions();
    public void navigateToPayment();
    public void navigateToWallet();
  
    // UI Update Methods
    public void updateBalanceDisplay(BigDecimal balance);
    public void updateCardCountDisplay(int count);
    public void updateTransactionTable(List<Transaction> transactions);
    public void updateQuickStats(Map<String, Object> stats);
  
    // Event Handlers
    public void handleRefreshClick();
    public void handleTransactionClick(int transactionId);
    public void handleCardClick(int cardId);
}
```

### 3. PaymentController.java

```java
public class PaymentController {
    private PaymentService paymentService;
    private CardService cardService;
    private MerchantService merchantService;
    private PaymentPanel paymentPanel;
  
    // Constructor
    public PaymentController();
  
    // Payment Processing
    public void handlePayment(int cardId, int merchantId, BigDecimal amount, String description);
    public void handleNFCPayment(String cardUid, int merchantId, BigDecimal amount);
    public void handlePaymentConfirmation(Transaction transaction);
    public void handlePaymentCancellation();
  
    // Form Management
    public void loadPaymentForm();
    public void clearPaymentForm();
    public void validatePaymentForm(String amount, String cardId, String merchantId);
    public void populateCardDropdown();
    public void populateMerchantDropdown();
  
    // NFC Simulation
    public void simulateNFCTap();
    public void handleCardDetection(String cardUid);
    public void showNFCAnimation();
    public void hideNFCAnimation();
  
    // Validation Handlers
    public void validateAmount(String amount);
    public void validateCardSelection(int cardId);
    public void validateMerchantSelection(int merchantId);
    public void validateDescription(String description);
  
    // UI Update Methods
    public void updateBalanceDisplay();
    public void updateCardList(List<Card> cards);
    public void updateMerchantList(List<Merchant> merchants);
    public void showPaymentProgress();
    public void hidePaymentProgress();
  
    // Event Handlers
    public void handleCardSelectionChange(int cardId);
    public void handleMerchantSelectionChange(int merchantId);
    public void handleAmountChange(String amount);
    public void handlePayButtonClick();
    public void handleCancelButtonClick();
  
    // Result Handlers
    public void handlePaymentSuccess(Transaction transaction);
    public void handlePaymentFailure(String errorMessage);
    public void showPaymentResult(boolean success, String message);
}
```

---

## 🖼️ View Layer (GUI Components)

### 1. LoginFrame.java

```java
public class LoginFrame extends JFrame {
    // Components
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel titleLabel;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
  
    // Constructor
    public LoginFrame();
  
    // Initialization Methods
    private void initializeComponents();
    private void setupLayout();
    private void addEventListeners();
    private void setupStyling();
  
    // UI Methods
    public void clearFields();
    public void setFieldsEnabled(boolean enabled);
    public void showValidationError(String field, String message);
    public void clearValidationErrors();
  
    // Event Handlers
    private void handleLoginClick();
    private void handleRegisterClick();
    private void handleEnterKeyPress();
    private void handleFieldFocusGained(JTextField field);
    private void handleFieldFocusLost(JTextField field);
  
    // Validation UI
    public void highlightInvalidField(JTextField field);
    public void removeFieldHighlight(JTextField field);
    public void showLoginError(String message);
    public void showLoginSuccess(String message);
  
    // Getters
    public String getEmail();
    public String getPassword();
  
    // Utility Methods
    private void centerFrame();
    private void setupKeyBindings();
    private void setupTooltips();
}
```

### 2. DashboardPanel.java

```java
public class DashboardPanel extends JPanel {
    // Components
    private JLabel balanceLabel;
    private JLabel cardCountLabel;
    private JLabel transactionCountLabel;
    private JTable recentTransactionsTable;
    private JButton quickPayButton;
    private JButton addCardButton;
    private JButton viewAllTransactionsButton;
    private JButton topUpButton;
    private JPanel summaryPanel;
    private JPanel quickActionsPanel;
    private JPanel transactionsPanel;
  
    // Constructor
    public DashboardPanel();
  
    // Initialization Methods
    private void initializeComponents();
    private void setupLayout();
    private void addEventListeners();
    private void setupStyling();
  
    // Data Loading Methods
    public void loadDashboardData();
    public void refreshBalance(BigDecimal balance);
    public void refreshCardCount(int count);
    public void refreshTransactionCount(int count);
    public void loadRecentTransactions(List<Transaction> transactions);
  
    // UI Update Methods
    public void updateBalanceDisplay(String formattedBalance);
    public void updateSummaryCards(Map<String, Object> summary);
    public void updateTransactionTable(List<Transaction> transactions);
    public void showLoadingIndicator();
    public void hideLoadingIndicator();
  
    // Event Handlers
    private void handleQuickPayClick();
    private void handleAddCardClick();
    private void handleViewAllTransactionsClick();
    private void handleTopUpClick();
    private void handleRefreshClick();
    private void handleTransactionRowClick(int row);
  
    // Table Management
    private void setupTransactionTable();
    private DefaultTableModel createTransactionTableModel();
    private void updateTableData(List<Transaction> transactions);
  
    // Summary Cards
    private JPanel createSummaryCard(String title, String value, Color color);
    private void updateSummaryCard(JPanel card, String value);
  
    // Quick Actions
    private JPanel createQuickActionsPanel();
    private void enableQuickActions(boolean enabled);
}
```

### 3. PaymentPanel.java

```java
public class PaymentPanel extends JPanel {
    // Components
    private JComboBox<Card> cardComboBox;
    private JComboBox<Merchant> merchantComboBox;
    private JTextField amountField;
    private JTextArea descriptionArea;
    private JButton payButton;
    private JButton simulateNFCButton;
    private JButton clearButton;
    private JLabel balanceLabel;
    private JLabel cardLabel;
    private JLabel merchantLabel;
    private JLabel amountLabel;
    private JLabel descriptionLabel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private JPanel nfcPanel;
  
    // Constructor
    public PaymentPanel();
  
    // Initialization Methods
    private void initializeComponents();
    private void setupLayout();
    private void addEventListeners();
    private void setupStyling();
  
    // Data Loading Methods
    public void loadCards(List<Card> cards);
    public void loadMerchants(List<Merchant> merchants);
    public void refreshBalance(BigDecimal balance);
  
    // Form Management
    public void clearForm();
    public void validateForm();
    public void setFormEnabled(boolean enabled);
    public void resetForm();
  
    // Event Handlers
    private void handlePayClick();
    private void handleSimulateNFCClick();
    private void handleClearClick();
    private void handleCardSelectionChange();
    private void handleMerchantSelectionChange();
    private void handleAmountChange();
  
    // Validation UI
    public void showFieldError(JComponent field, String message);
    public void clearFieldErrors();
    public void highlightInvalidField(JComponent field);
    public void removeFieldHighlight(JComponent field);
  
    // NFC Simulation
    private void showNFCAnimation();
    private void hideNFCAnimation();
    private void simulateCardTap();
  
    // Payment Processing UI
    public void showPaymentProgress();
    public void hidePaymentProgress();
    public void showPaymentResult(boolean success, String message);
  
    // Getters
    public Card getSelectedCard();
    public Merchant getSelectedMerchant();
    public BigDecimal getAmount();
    public String getDescription();
  
    // Utility Methods
    private void setupComboBoxRenderers();
    private void setupFieldValidation();
    private void setupTooltips();
}
```

---

## 🛠️ Utility Classes

### 1. Session.java

```java
public class Session {
    // Static Fields
    private static User currentUser;
    private static LocalDateTime loginTime;
    private static boolean isLoggedIn;
  
    // Session Management
    public static void setCurrentUser(User user);
    public static User getCurrentUser();
    public static boolean isLoggedIn();
    public static void logout();
    public static void updateLoginTime();
  
    // Session Information
    public static int getCurrentUserId();
    public static String getCurrentUserName();
    public static String getCurrentUserEmail();
    public static Duration getSessionDuration();
    public static boolean isSessionValid();
  
    // Session Validation
    public static boolean hasValidSession();
    public static boolean isSessionExpired();
    public static void refreshSession();
    public static void invalidateSession();
}
```

### 2. ValidationUtil.java

```java
public class ValidationUtil {
    // Email Validation
    public static boolean isValidEmail(String email);
    public static String getEmailError(String email);
  
    // Password Validation
    public static boolean isValidPassword(String password);
    public static List<String> getPasswordErrors(String password);
    public static boolean isStrongPassword(String password);
  
    // Phone Validation
    public static boolean isValidPhone(String phone);
    public static String formatPhone(String phone);
  
    // Amount Validation
    public static boolean isValidAmount(String amount);
    public static boolean isValidAmount(BigDecimal amount);
    public static BigDecimal parseAmount(String amount);
  
    // Card UID Validation
    public static boolean isValidCardUID(String cardUid);
    public static String formatCardUID(String cardUid);
  
    // General Validation
    public static boolean isNotEmpty(String value);
    public static boolean isValidLength(String value, int minLength, int maxLength);
    public static boolean containsOnlyAlphanumeric(String value);
    public static boolean matchesPattern(String value, String pattern);
  
    // Business Validation
    public static boolean isValidTransactionAmount(BigDecimal amount);
    public static boolean isValidDailyLimit(BigDecimal amount);
    public static boolean isValidCardName(String cardName);
    public static boolean isValidMerchantCode(String merchantCode);
}
```

This complete file structure provides every class, method, and component needed to implement your NFC Payment System exactly as described in your project proposal. Each component is designed to fulfill the functional and non-functional requirements you submitted.
