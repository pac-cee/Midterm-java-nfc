# 🏗️ Simplified File Structure (No Impl Pattern)

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
│       │           │   ├── UserDAO.java
│       │           │   ├── WalletDAO.java
│       │           │   ├── CardDAO.java
│       │           │   ├── TransactionDAO.java
│       │           │   └── MerchantDAO.java
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
├── docker/
│   └── docker-compose.yml
├── lib/
│   ├── postgresql-42.6.0.jar
│   └── flatlaf-3.2.5.jar
├── docs/
│   ├── README.md
│   ├── system-design.md
│   ├── database-schema.md
│   ├── implementation-plan.md
│   └── user-manual.md
└── run.bat
```

## 🔧 DAO Classes (Direct Implementation with PreparedStatements)

### UserDAO.java
```java
public class UserDAO {
    private DatabaseConnection dbConnection;
    
    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // Create Operations
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password_hash, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getPhone());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Read Operations
    public User getUserById(int userId) { /* PreparedStatement implementation */ }
    public User getUserByEmail(String email) { /* PreparedStatement implementation */ }
    public List<User> getAllUsers() { /* PreparedStatement implementation */ }
    
    // Update Operations
    public boolean updateUser(User user) { /* PreparedStatement implementation */ }
    public boolean updateLastLogin(int userId) { /* PreparedStatement implementation */ }
    
    // Delete Operations
    public boolean deleteUser(int userId) { /* PreparedStatement implementation */ }
    
    // Authentication
    public User authenticateUser(String email, String passwordHash) { /* PreparedStatement implementation */ }
    
    // Validation
    public boolean emailExists(String email) { /* PreparedStatement implementation */ }
}
```

### CardDAO.java
```java
public class CardDAO {
    private DatabaseConnection dbConnection;
    
    public CardDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    // Create Operations
    public boolean createCard(Card card) {
        String sql = "INSERT INTO cards (user_id, card_uid, card_name, card_type) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, card.getUserId());
            pstmt.setString(2, card.getCardUid());
            pstmt.setString(3, card.getCardName());
            pstmt.setString(4, card.getCardType().toString());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Read Operations
    public Card getCardById(int cardId) { /* PreparedStatement implementation */ }
    public List<Card> getCardsByUserId(int userId) { /* PreparedStatement implementation */ }
    public List<Card> getActiveCardsByUserId(int userId) { /* PreparedStatement implementation */ }
    
    // Update Operations
    public boolean updateCard(Card card) { /* PreparedStatement implementation */ }
    public boolean activateCard(int cardId) { /* PreparedStatement implementation */ }
    public boolean deactivateCard(int cardId) { /* PreparedStatement implementation */ }
    
    // Delete Operations
    public boolean deleteCard(int cardId) { /* PreparedStatement implementation */ }
    
    // Validation
    public boolean cardUidExists(String cardUid) { /* PreparedStatement implementation */ }
    public boolean cardBelongsToUser(int cardId, int userId) { /* PreparedStatement implementation */ }
}
```

## 📊 Total File Count

| Layer | Files | Description |
|-------|-------|-------------|
| **Model** | 9 files | 5 POJOs + 4 enums |
| **DAO** | 6 files | DatabaseConnection + 5 DAO classes |
| **Service** | 8 files | Business logic classes |
| **Controller** | 7 files | Event handlers |
| **View** | 18 files | 3 frames + 6 panels + 5 components + 4 dialogs |
| **Util** | 7 files | Helper utilities |
| **Resources** | 6 files | SQL + config + icons |
| **Config** | 2 files | Docker + run script |
| **Total** | **63 files** | Complete implementation |

---

# 🚀 Next Steps - Implementation Plan

## Phase 1: Foundation (Start Here)

### Step 1: Create Project Structure
```bash
# Create all directories
mkdir -p src/main/java/com/nfcpay/{model,dao,service,controller,view,util}
mkdir -p src/main/java/com/nfcpay/model/enums
mkdir -p src/main/java/com/nfcpay/view/{frames,panels,components,dialogs}
mkdir -p src/main/resources/{database,config,icons}
mkdir -p docker lib docs
```

### Step 2: Setup Database (Docker + PostgreSQL)
1. **Create docker-compose.yml**
2. **Create schema.sql** (all 5 tables)
3. **Create sample_data.sql** (test merchants)
4. **Start PostgreSQL container**
5. **Test database connection**

### Step 3: Download Dependencies
1. **PostgreSQL JDBC Driver** - `postgresql-42.6.0.jar`
2. **FlatLaf UI Theme** - `flatlaf-3.2.5.jar`

## Phase 2: Core Implementation

### Step 4: Model Layer (Day 1)
1. **Create all enums** (CardType, TransactionType, etc.)
2. **Create all POJOs** (User, Card, Wallet, etc.)
3. **Add constructors, getters, setters**
4. **Test model classes**

### Step 5: DAO Layer (Day 2)
1. **DatabaseConnection.java** - Singleton pattern
2. **All 5 DAO classes** with PreparedStatements
3. **Test database operations**
4. **Verify CRUD functionality**

### Step 6: Service Layer (Day 3)
1. **ValidationService** - All 10 validation rules
2. **AuthService** - Login/register logic
3. **PaymentService** - Payment processing
4. **Other services** - Business logic

### Step 7: View Layer (Days 4-5)
1. **Custom components** - Buttons, text fields, tables
2. **Login/Register frames** - Authentication UI
3. **Main frame + panels** - Core application UI
4. **Wire everything together**

## Phase 3: Integration & Testing (Day 6)

### Step 8: Controller Layer
1. **Connect services to UI**
2. **Handle all user events**
3. **Implement error handling**

### Step 9: Final Testing
1. **Test all CRUD operations**
2. **Test all validation rules**
3. **Test complete user workflows**
4. **Polish UI and fix bugs**

---

# 🎯 What to Start With RIGHT NOW

## Immediate Action Plan:

### 1. **Create Docker Setup** (15 minutes)
- Create `docker-compose.yml`
- Create `schema.sql` with all 5 tables
- Start PostgreSQL container

### 2. **Download JARs** (5 minutes)
- Download PostgreSQL JDBC driver
- Download FlatLaf JAR
- Place in `lib/` folder

### 3. **Create Model Classes** (30 minutes)
- Start with enums (CardType, TransactionStatus, etc.)
- Create User.java POJO
- Test compilation

**Ready to start? Which step should we tackle first?**

1. **Database setup** (Docker + PostgreSQL)
2. **Model classes** (POJOs + enums)
3. **Project structure creation**

Choose one and we'll implement it step by step!