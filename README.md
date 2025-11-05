# 🚀 NFC Payment System

A professional-grade NFC payment simulation application built with Java Swing, PostgreSQL, and MVC architecture.

## 📋 Project Overview

**NFC Pay** is a desktop application that simulates a complete NFC payment ecosystem with user management, card operations, transaction processing, and wallet functionality.

### 🎯 Key Features
- **User Authentication** - Secure registration and login system
- **Card Management** - Add, edit, activate/deactivate NFC cards
- **Payment Processing** - Simulate NFC tap-to-pay transactions
- **Transaction History** - View, search, and filter payment records
- **Wallet Management** - Balance tracking and fund management
- **Modern GUI** - Professional Swing interface with FlatLaf theme

### 🏗️ Architecture
- **Pattern**: MVC (Model-View-Controller) + DAO (Data Access Object)
- **Database**: PostgreSQL with Docker
- **UI Framework**: Java Swing with FlatLaf Look & Feel
- **Security**: Password hashing with BCrypt

## 🛠️ Technology Stack

| Component | Technology |
|-----------|------------|
| **Frontend** | Java Swing + FlatLaf |
| **Backend** | Java 11+ |
| **Database** | PostgreSQL 15+ |
| **Container** | Docker |
| **Build Tool** | Maven/Gradle |
| **Security** | BCrypt Password Hashing |

## 📁 Project Structure

```
NFC_Payment_System/
├── docs/                           # Documentation
│   ├── system-design.md
│   ├── database-schema.md
│   ├── api-documentation.md
│   └── diagrams/
├── src/main/java/com/nfcpay/
│   ├── model/                      # Data Models (POJOs)
│   ├── dao/                        # Data Access Layer
│   ├── service/                    # Business Logic Layer
│   ├── controller/                 # Controller Layer
│   ├── view/                       # GUI Components
│   └── util/                       # Utility Classes
├── src/main/resources/
│   ├── database/
│   │   └── schema.sql
│   └── config/
├── docker/
│   └── docker-compose.yml
├── lib/                            # External JARs
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Docker and Docker Compose
- Git

### 1. Clone Repository
```bash
git clone <repository-url>
cd NFC_Payment_System
```

### 2. Start Database
```bash
cd docker
docker-compose up -d
```

### 3. Setup Database Schema
```bash
# Connect to PostgreSQL container
docker exec -it nfc_postgres psql -U nfcuser -d nfc_payment_db

# Run schema file
\i /docker-entrypoint-initdb.d/schema.sql
```

### 4. Configure Database Connection
Edit `src/main/resources/config/database.properties`:
```properties
db.url=jdbc:postgresql://localhost:5432/nfc_payment_db
db.username=nfcuser
db.password=nfcpass
db.driver=org.postgresql.Driver
```

### 5. Run Application
```bash
# Compile and run
javac -cp "lib/*:src" src/com/nfcpay/Main.java
java -cp "lib/*:src" com.nfcpay.Main
```

## 📊 Database Schema

### Core Tables
- **users** - User accounts and authentication
- **wallets** - User balance and currency management
- **cards** - NFC card information
- **merchants** - Payment recipients
- **transactions** - Payment records and history

### Relationships
```
users (1) ←→ (1) wallets
users (1) ←→ (*) cards
users (1) ←→ (*) transactions
merchants (1) ←→ (*) transactions
cards (1) ←→ (*) transactions
```

## 🔧 System Architecture

### Layer Structure
```
┌─────────────────────────────────────┐
│           View Layer                │  ← GUI Components (Swing)
├─────────────────────────────────────┤
│         Controller Layer            │  ← Event Handlers
├─────────────────────────────────────┤
│          Service Layer              │  ← Business Logic
├─────────────────────────────────────┤
│            DAO Layer                │  ← Data Access
├─────────────────────────────────────┤
│          Database Layer             │  ← PostgreSQL
└─────────────────────────────────────┘
```

## 🔐 Security Features

### Authentication
- Password hashing using BCrypt
- Session management
- Input validation and sanitization

### Data Protection
- SQL injection prevention with PreparedStatements
- XSS protection in GUI inputs
- Secure password storage

## 📱 User Interface

### Main Screens
1. **Login/Register** - User authentication
2. **Dashboard** - Overview and quick actions
3. **Card Management** - NFC card operations
4. **Transactions** - Payment history and search
5. **Payment** - Process new payments

### Design Principles
- Clean, modern interface using FlatLaf
- Responsive layout with proper spacing
- Consistent color scheme and typography
- Intuitive navigation and user flow

## 🧪 Testing

### Test Categories
- **Unit Tests** - Individual component testing
- **Integration Tests** - Database and service layer testing
- **GUI Tests** - User interface validation
- **Security Tests** - Authentication and authorization

### Sample Test Data
```sql
-- Test users
INSERT INTO users (full_name, email, password_hash) VALUES 
('John Doe', 'john@test.com', '$2a$10$...');

-- Test merchants
INSERT INTO merchants (merchant_name, merchant_code, category) VALUES 
('Test Store', 'TST001', 'Retail');
```

## 📈 Performance Considerations

### Database Optimization
- Indexed columns for fast queries
- Connection pooling
- Prepared statement caching

### GUI Performance
- Lazy loading for large datasets
- Background threading for database operations
- Efficient table models for JTable

## 🔄 Development Workflow

### Git Workflow
```bash
# Feature development
git checkout -b feature/payment-processing
git commit -m "Add payment validation logic"
git push origin feature/payment-processing
```

### Code Standards
- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Maintain consistent indentation (4 spaces)

## 📚 API Documentation

### Service Layer Methods
- `AuthService.authenticateUser(email, password)`
- `PaymentService.processPayment(userId, cardId, merchantId, amount)`
- `CardService.addCard(userId, cardName, cardType)`

### DAO Layer Methods
- `UserDAO.createUser(User user)`
- `TransactionDAO.getTransactionsByUserId(int userId)`
- `CardDAO.getActiveCardsByUserId(int userId)`

## 🐛 Troubleshooting

### Common Issues

**Database Connection Failed**
```bash
# Check Docker container status
docker ps
docker logs nfc_postgres

# Verify connection settings
psql -h localhost -p 5432 -U nfcuser -d nfc_payment_db
```

**GUI Not Loading**
```bash
# Check FlatLaf dependency
java -cp "lib/*" -version
# Verify main class path
```

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Create feature branch
3. Follow coding standards
4. Add tests for new features
5. Submit pull request

### Code Review Process
- All changes require review
- Tests must pass
- Documentation must be updated

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Developer**: Pacific
- **Course**: INSY 7312 - Java Programming
- **Instructor**: Dr. SEBAGENZI Jason & Jeremie U. Tuyisenge

## 📞 Support

For questions or issues:
- Create GitHub issue
- Email: support@nfcpay.com
- Documentation: [docs/](docs/)

---

**Built with ❤️ for INSY 7312 Mid-Semester Project**