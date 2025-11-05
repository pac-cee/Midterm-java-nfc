# 🏗️ System Design Document

## 1. Architecture Overview

### 1.1 System Architecture Pattern
**MVC + DAO + Service Layer Architecture**

```
┌─────────────────────────────────────────────┐
│                View Layer                   │
│  LoginFrame, DashboardPanel, CardPanel     │
│  TransactionPanel, PaymentPanel            │
└─────────────────┬───────────────────────────┘
                  │ User Events
┌─────────────────▼───────────────────────────┐
│              Controller Layer               │
│  AuthController, CardController,            │
│  PaymentController, TransactionController   │
└─────────────────┬───────────────────────────┘
                  │ Business Logic Calls
┌─────────────────▼───────────────────────────┐
│               Service Layer                 │
│  AuthService, PaymentService,               │
│  ValidationService, NFCService              │
└─────────────────┬───────────────────────────┘
                  │ Data Operations
┌─────────────────▼───────────────────────────┐
│                DAO Layer                    │
│  UserDAO, CardDAO, TransactionDAO,          │
│  WalletDAO, MerchantDAO                     │
└─────────────────┬───────────────────────────┘
                  │ JDBC Calls
┌─────────────────▼───────────────────────────┐
│            PostgreSQL Database              │
│  users, cards, wallets, transactions,       │
│  merchants tables                           │
└─────────────────────────────────────────────┘
```

### 1.2 Design Principles
- **Separation of Concerns**: Each layer has distinct responsibilities
- **Dependency Injection**: Services injected into controllers
- **Single Responsibility**: Each class has one primary function
- **Open/Closed Principle**: Extensible without modification

## 2. Component Design

### 2.1 Model Layer (POJOs)

#### User.java
```java
public class User {
    private int userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
```

#### Card.java
```java
public class Card {
    private int cardId;
    private int userId;
    private String cardUid;
    private String cardName;
    private CardType cardType;
    private boolean isActive;
    private LocalDateTime createdAt;
}
```

#### Transaction.java
```java
public class Transaction {
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
}
```

### 2.2 DAO Layer Design

#### Interface Pattern
```java
public interface UserDAO {
    boolean createUser(User user);
    User getUserById(int userId);
    User getUserByEmail(String email);
    boolean updateUser(User user);
    boolean deleteUser(int userId);
    List<User> getAllUsers();
}
```

#### Implementation Pattern
```java
public class UserDAOImpl implements UserDAO {
    private DatabaseConnection dbConnection;
    
    public UserDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, phone) VALUES (?, ?, ?, ?)";
        // Implementation with PreparedStatement
    }
}
```

### 2.3 Service Layer Design

#### AuthService.java
```java
public class AuthService {
    private UserDAO userDAO;
    private ValidationService validationService;
    
    public User authenticateUser(String email, String password) {
        // 1. Validate input
        // 2. Get user from database
        // 3. Verify password
        // 4. Update last login
        // 5. Return user or null
    }
    
    public boolean registerUser(String fullName, String email, String password, String phone) {
        // 1. Validate all inputs
        // 2. Check if email exists
        // 3. Hash password
        // 4. Create user
        // 5. Create wallet
    }
}
```

#### PaymentService.java
```java
public class PaymentService {
    private TransactionDAO transactionDAO;
    private WalletDAO walletDAO;
    private CardDAO cardDAO;
    
    public Transaction processPayment(int userId, int cardId, int merchantId, BigDecimal amount, String description) {
        // 1. Validate payment request
        // 2. Check sufficient balance
        // 3. Verify card is active
        // 4. Generate transaction reference
        // 5. Deduct from wallet
        // 6. Create transaction record
        // 7. Return transaction
    }
}
```

### 2.4 Controller Layer Design

#### AuthController.java
```java
public class AuthController {
    private AuthService authService;
    private LoginFrame loginFrame;
    private RegisterFrame registerFrame;
    private MainFrame mainFrame;
    
    public void handleLogin(String email, String password) {
        try {
            User user = authService.authenticateUser(email, password);
            if (user != null) {
                Session.setCurrentUser(user);
                showMainFrame();
            } else {
                showErrorMessage("Invalid credentials");
            }
        } catch (Exception e) {
            showErrorMessage("Login failed: " + e.getMessage());
        }
    }
}
```

## 3. Database Design

### 3.1 Entity Relationship Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    users    │    │   wallets   │    │    cards    │
├─────────────┤    ├─────────────┤    ├─────────────┤
│ user_id (PK)│◄──►│ wallet_id   │    │ card_id (PK)│
│ full_name   │    │ user_id (FK)│    │ user_id (FK)│◄──┐
│ email       │    │ balance     │    │ card_uid    │   │
│ password_hash│   │ currency    │    │ card_name   │   │
│ phone       │    │ created_at  │    │ card_type   │   │
│ is_active   │    └─────────────┘    │ is_active   │   │
│ created_at  │                       │ created_at  │   │
│ last_login  │                       └─────────────┘   │
└─────────────┘                                         │
       │                                                │
       │    ┌─────────────┐    ┌─────────────┐         │
       │    │ merchants   │    │transactions │         │
       │    ├─────────────┤    ├─────────────┤         │
       │    │merchant_id  │◄──►│transaction_id│         │
       │    │merchant_name│    │ user_id (FK)│◄────────┘
       │    │merchant_code│    │ card_id (FK)│◄─────────┘
       │    │ category    │    │merchant_id  │
       │    │ is_active   │    │ amount      │
       │    │ created_at  │    │ type        │
       │    └─────────────┘    │ status      │
       │                       │ reference   │
       └──────────────────────►│ description │
                               │ created_at  │
                               └─────────────┘
```

### 3.2 Table Specifications

#### users table
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);
```

#### wallets table
```sql
CREATE TABLE wallets (
    wallet_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    balance DECIMAL(10,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'USD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### cards table
```sql
CREATE TABLE cards (
    card_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    card_uid VARCHAR(50) UNIQUE NOT NULL,
    card_name VARCHAR(50) NOT NULL,
    card_type VARCHAR(20) DEFAULT 'VIRTUAL',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### transactions table
```sql
CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id),
    card_id INTEGER NOT NULL REFERENCES cards(card_id),
    merchant_id INTEGER NOT NULL REFERENCES merchants(merchant_id),
    amount DECIMAL(10,2) NOT NULL,
    transaction_type VARCHAR(20) DEFAULT 'PAYMENT',
    status VARCHAR(20) DEFAULT 'PENDING',
    reference_code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### merchants table
```sql
CREATE TABLE merchants (
    merchant_id SERIAL PRIMARY KEY,
    merchant_name VARCHAR(100) NOT NULL,
    merchant_code VARCHAR(20) UNIQUE NOT NULL,
    category VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 4. Security Design

### 4.1 Authentication Flow
```
User Input → Validation → Password Hash → Database Lookup → Session Creation
```

### 4.2 Password Security
- **Hashing Algorithm**: BCrypt with salt
- **Minimum Requirements**: 6 characters, alphanumeric
- **Storage**: Never store plain text passwords

### 4.3 SQL Injection Prevention
- **PreparedStatements**: All database queries use parameterized statements
- **Input Validation**: Server-side validation for all inputs
- **Escaping**: Special characters properly escaped

### 4.4 Session Management
```java
public class Session {
    private static User currentUser;
    private static LocalDateTime loginTime;
    
    public static void setCurrentUser(User user) {
        currentUser = user;
        loginTime = LocalDateTime.now();
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
```

## 5. Performance Considerations

### 5.1 Database Performance
- **Connection Pooling**: Reuse database connections
- **Indexing**: Primary keys and foreign keys indexed
- **Query Optimization**: Efficient SQL queries with proper joins

### 5.2 GUI Performance
- **Background Threading**: Database operations in separate threads
- **Lazy Loading**: Load data only when needed
- **Caching**: Cache frequently accessed data

### 5.3 Memory Management
- **Object Pooling**: Reuse expensive objects
- **Proper Disposal**: Close resources in finally blocks
- **Garbage Collection**: Minimize object creation in loops

## 6. Error Handling Strategy

### 6.1 Exception Hierarchy
```
Exception
├── DatabaseException
│   ├── ConnectionException
│   └── QueryException
├── ValidationException
│   ├── InvalidInputException
│   └── BusinessRuleException
└── AuthenticationException
    ├── InvalidCredentialsException
    └── SessionExpiredException
```

### 6.2 Error Handling Pattern
```java
public class PaymentController {
    public void handlePayment(PaymentRequest request) {
        try {
            // Process payment
            Transaction result = paymentService.processPayment(request);
            showSuccessMessage("Payment successful!");
        } catch (ValidationException e) {
            showValidationError(e.getMessage());
        } catch (DatabaseException e) {
            showErrorMessage("Database error occurred");
            logger.error("Database error", e);
        } catch (Exception e) {
            showErrorMessage("Unexpected error occurred");
            logger.error("Unexpected error", e);
        }
    }
}
```

## 7. Validation Framework

### 7.1 Business Validations
1. **User Authentication**: Valid credentials required
2. **Card Ownership**: Card must belong to user
3. **Payment Amount**: Must not exceed balance
4. **Transaction Limits**: Daily/monthly limits enforced
5. **Card Status**: Must be active for transactions

### 7.2 Technical Validations
1. **Email Format**: Valid email pattern
2. **Password Strength**: Minimum requirements
3. **Phone Format**: Valid phone number pattern
4. **Amount Format**: Valid decimal format
5. **Card UID Format**: Valid UUID format

### 7.3 Validation Implementation
```java
public class ValidationService {
    public List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();
        
        if (!isValidEmail(user.getEmail())) {
            errors.add("Invalid email format");
        }
        
        if (!isValidPassword(user.getPassword())) {
            errors.add("Password must be at least 6 characters");
        }
        
        return errors;
    }
}
```

This system design provides a robust foundation for the NFC Payment System with clear separation of concerns, proper error handling, and scalable architecture.