# AurumX - CES Reward Redemption Platform

**Production-Grade Internal Banking Reward System**

AurumX is a comprehensive internal bank reward redemption system designed for Customer Executive Support (CES) users to manage rewards on behalf of customers. This is NOT a demo project - it's built with enterprise-level architecture, configuration-driven design, and production-ready code quality.

---

## 🏗️ Technology Stack

### Backend
```export JAVA_HOME=$(/usr/libexec/java_home -v 22); cd backend && mvn spring-boot:run
```
- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: MySQL 8
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + JWT
- **Validation**: Spring Validation

### Frontend (Planned)
```npm run start
```
- **Framework**: Angular 17
- **Language**: TypeScript
- **Styling**: CSS

---

## 🎯 Core Features

### 8 Complete EPICs Implemented

1. **Authentication & Authorization** - JWT-based login with role-based access control
2. **CES User Management** - Admin tool for creating/deleting CES users
3. **Customer Management** - Full CRUD with soft delete, pagination, and search
4. **Credit Card Management** - Multiple cards per customer with uniqueness validation
5. **Transaction Management** - Configurable random transaction generation
6. **Reward Processing** - Idempotent transaction processing with dynamic reward calculation
7. **Reward Catalog & Redemption** - 6 categories, 30 items, cart-based all-or-nothing redemption
8. **Customer Profile** - Unified profile view with complete history

---

## 📋 Business Rules (Configuration-Driven)

All business logic is externalized to `application.yml` for zero-code changes:

| Rule | Configuration Key | Default Value |
|------|-------------------|---------------|
| Premium Customer Threshold | `aurumx.customer.premium-association-years` | 3 years |
| Transaction Min Amount | `aurumx.transaction.min-amount` | ₹500 |
| Transaction Max Amount | `aurumx.transaction.max-amount` | ₹50,000 |
| Transaction Generation Count | `aurumx.transaction.generation-count` | 50 |
| Regular Customer Reward | `aurumx.reward.regular-percentage` | 5% |
| Premium Customer Reward | `aurumx.reward.premium-percentage` | 10% |

**Key Point**: Change these values in `application.yml` and **restart the application** - NO code deployment required.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- MySQL 8+
- Maven 3.8+
- Node.js 18+ (for Angular frontend)

### Backend Setup

1. **Clone and navigate to backend**:
   ```bash
   cd /Users/ashutoshkumar/aurumx/backend
   ```

2. **Configure MySQL database**:
   - Create database: `aurumx_db`
   - Update credentials in `src/main/resources/application.yml`:
     ```yaml
     spring:
       datasource:
         username: your_mysql_username
         password: your_mysql_password
     ```

3. **Build and run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Verify startup**:
   - Application runs on `http://localhost:8080/api`
   - Check logs for successful database initialization
   - Default users are created automatically

### Default User Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `password123` | ADMIN_CES |
| `cesuser` | `password123` | CES_USER |

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### POST /auth/login
Login for Admin CES or CES User.

**Request**:
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ROLE_ADMIN_CES"
}
```

**Usage**: Include token in all subsequent requests:
```
Authorization: Bearer <token>
```

### CES User Management (Admin Only)

#### POST /ces-users
Create a new CES user.

**Request**:
```json
{
  "username": "newuser",
  "password": "pass123",
  "role": "ROLE_CES_USER"
}
```

#### DELETE /ces-users/{id}
Delete a CES user. **Cannot delete yourself**.

#### GET /ces-users
List all CES users.

### Customer Management

#### POST /customers
Create a new customer. Customer type (REGULAR/PREMIUM) is auto-calculated based on association date.

**Request**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "associationDate": "2020-01-15"
}
```

**Response**:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210",
  "associationDate": "2020-01-15",
  "customerType": "PREMIUM",
  "rewardBalance": 0.00
}
```

#### GET /customers?page=0&size=20
Get paginated list of customers (soft-deleted customers excluded).

#### GET /customers/search/name?name=John
Search customers by name.

#### GET /customers/search/card?cardNumber=1234
Search customers by credit card number.

#### DELETE /customers/{id}
Soft delete customer.

### Credit Card Management

#### POST /credit-cards
Add credit card to customer.

**Request**:
```json
{
  "customerId": 1,
  "cardNumber": "4532123456789012",
  "cardHolderName": "JOHN DOE",
  "expiryDate": "2026-12-31"
}
```

Card numbers must be unique across the system.

#### GET /credit-cards/customer/{customerId}
List all credit cards for a customer.

### Transaction Management

#### POST /transactions/generate
Generate random transactions for a credit card (count configured in `application.yml`).

**Request**:
```json
{
  "creditCardId": 1
}
```

**Response**: Array of 50 (or configured count) transactions with random amounts (₹500-₹50,000) and merchants.

#### GET /transactions/card/{cardId}
View all transactions for a credit card.

### Reward Processing

#### POST /rewards/process/{customerId}
Process all unprocessed transactions for a customer and calculate rewards.
- Regular customers: 5% (configurable)
- Premium customers: 10% (configurable)
- Transactions can only be processed ONCE

**Response**:
```json
{
  "customerId": 1,
  "customerName": "John Doe",
  "pointsBalance": 12500.50,
  "lifetimeEarned": 12500.50
}
```

#### GET /rewards/balance/{customerId}
Get current reward balance for a customer.

### Reward Catalog

#### GET /catalog/categories
Get all reward categories (ordered by display order).

**Response**:
```json
[
  {
    "id": 1,
    "name": "Gift Cards",
    "description": "Digital and physical gift cards for popular services",
    "displayOrder": 1
  },
  ...
]
```

#### GET /catalog/items?categoryId=1
Get available reward items by category (or all items if no categoryId).

### Cart & Redemption

#### POST /cart/add
Add reward item to cart.

**Request**:
```json
{
  "customerId": 1,
  "rewardItemId": 5,
  "quantity": 2
}
```

#### GET /cart/{customerId}
View customer's cart.

#### DELETE /cart/item/{cartItemId}
Remove item from cart.

#### POST /cart/redeem/{customerId}
Redeem all cart items (all-or-nothing redemption).
- Validates sufficient reward balance
- Deducts points atomically
- Creates redemption history
- Clears cart

**Response**:
```json
{
  "redemptionId": 1,
  "customerId": 1,
  "totalPointsUsed": 15000.00,
  "redeemedAt": "2025-12-27T12:00:00",
  "items": [...]
}
```

#### GET /cart/redemption-history/{customerId}
View customer's redemption history.

---

## 🎁 Reward Catalog

### 6 Categories, 30 Items

| Category | Items | Point Range |
|----------|-------|-------------|
| **Gift Cards** | Google Play, Apple, Amazon, Flipkart, Swiggy, Zomato | 3,500 - 6,000 |
| **Travel & Holidays** | Manali, Kanyakumari, Goa, Jaipur, Ooty | 28,000 - 45,000 |
| **Shopping & Electronics** | Headphones, Smart Watch, Earbuds, Smartphone Voucher, Laptop Bag | 6,000 - 22,000 |
| **Dining & Lifestyle** | Dinner for Two, Café Voucher, Movie Tickets, Spa Voucher | 4,000 - 10,000 |
| **Health & Fitness** | Gym Membership, Yoga Classes, Fitness Band, Nutrition Consultation | 6,000 - 20,000 |
| **Learning & Subscriptions** | Online Course, E-Book, Coding Platform, Music Subscription | 4,000 - 12,000 |

All reward costs are stored in the database and can be modified via SQL updates without code changes.

---

## 🔧 Configuration Change Examples

### Example 1: Change Reward Percentages

**Scenario**: Bank wants to increase rewards to 7% (Regular) and 12% (Premium).

**Steps**:
1. Edit `src/main/resources/application.yml`:
   ```yaml
   aurumx:
     reward:
       regular-percentage: 7
       premium-percentage: 12
   ```
2. Restart application: `mvn spring-boot:run`
3. **No code deployment required**

**Result**: All new reward calculations use 7% and 12%.

### Example 2: Generate 100 Transactions Instead of 50

**Scenario**: CES users need to test with more transactions.

**Steps**:
1. Edit `src/main/resources/application.yml`:
   ```yaml
   aurumx:
     transaction:
       generation-count: 100
   ```
2. Restart application
3. Call `POST /transactions/generate`

**Result**: System generates 100 transactions per request.

### Example 3: Change Premium Customer Threshold to 5 Years

**Scenario**: Bank policy change - customers need 5 years for Premium status.

**Steps**:
1. Edit `src/main/resources/application.yml`:
   ```yaml
   aurumx:
     customer:
       premium-association-years: 5
   ```
2. Restart application
3. Create new customers

**Result**: Only customers with ≥5 years association are marked PREMIUM.

### Example 4: Add New Reward Item

**Scenario**: Add "Netflix Subscription" to Gift Cards category.

**Steps**:
1. Connect to MySQL:
   ```bash
   mysql -u root -p aurumx_db
   ```
2. Insert new item:
   ```sql
   INSERT INTO reward_item (category_id, name, description, points_cost, available)
   VALUES ((SELECT id FROM reward_category WHERE name = 'Gift Cards'), 
           'Netflix', '6-month Netflix Premium subscription', 5000, true);
   ```
3. **No application restart needed**

**Result**: New item appears in `/catalog/items` immediately.

---

## 🏛️ Architecture

### Layered Architecture

```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic + Configuration)
    ↓
Repository Layer (Data Access)
    ↓
Database Layer (MySQL)
```

### Configuration-Driven Design

Every business rule is injected via `@ConfigurationProperties`:
- `CustomerConfig` - Customer type thresholds
- `TransactionConfig` - Transaction generation rules
- `RewardConfig` - Reward calculation percentages
- `JwtConfig` - JWT expiration settings
- `PaginationConfig` - Pagination defaults

### Security Architecture

- **JWT Authentication**: Stateless token-based authentication
- **Password Encoding**: BCrypt with strength 10
- **Role-Based Access**: `@PreAuthorize` annotations on endpoints
- **CORS Configuration**: Configured for Angular frontend (localhost:4200)

### Database Design

- **10 Entities**: CesUser, Customer, CreditCard, Transaction, Reward, RewardCategory, RewardItem, CartItem, RedemptionHistory, RedemptionItem
- **Soft Delete**: Customers are soft-deleted to maintain referential integrity
- **Idempotency**: Transactions have `processed` flag for one-time reward processing
- **Reward Balance**: Customer-level balance (not per card)
- **Audit Trail**: Redemption history stores point costs at redemption time

---

## 📁 Project Structure

```
backend/
├── src/main/java/com/aurumx/
│   ├── config/              # Configuration classes
│   │   ├── CustomerConfig.java
│   │   ├── TransactionConfig.java
│   │   ├── RewardConfig.java
│   │   ├── JwtConfig.java
│   │   ├── PaginationConfig.java
│   │   └── SecurityConfig.java
│   ├── constants/           # Application constants
│   ├── enums/               # Enumerations
│   ├── entity/              # JPA Entities (10 entities)
│   ├── dto/                 # Request/Response DTOs
│   │   ├── request/
│   │   └── response/
│   ├── repository/          # Spring Data JPA Repositories (9 repositories)
│   ├── service/             # Business Logic (8 services)
│   ├── controller/          # REST Controllers (8 controllers)
│   ├── security/            # JWT & Security
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   └── exception/           # Custom Exceptions & Global Handler
├── src/main/resources/
│   ├── application.yml      # Main configuration
│   └── data/
│       ├── seed-reward-catalog.sql  # Reward catalog seed
│       └── init-users.sql           # Default users
└── pom.xml
```

---

## 🔒 Security Features

1. **JWT Token Expiration**: 24 hours (configurable via `jwt.expiration`)
2. **Self-Deletion Prevention**: Admin CES users cannot delete themselves
3. **Role-Based Endpoints**: Admin-only endpoints enforced via `@PreAuthorize`
4. **Input Validation**: `@Valid` annotations on all request DTOs
5. **Password Encryption**: BCrypt for all user passwords
6. **Transaction Integrity**: `@Transactional` on critical operations

---

## 🧪 Testing the Application

### 1. Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### 2. Create Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Smith",
    "email": "alice@example.com",
    "phone": "9876543210",
    "associationDate": "2019-06-15"
  }'
```

### 3. Add Credit Card
```bash
curl -X POST http://localhost:8080/api/credit-cards \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "cardNumber": "4532123456789012",
    "cardHolderName": "ALICE SMITH",
    "expiryDate": "2027-12-31"
  }'
```

### 4. Generate Transactions
```bash
curl -X POST http://localhost:8080/api/transactions/generate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"creditCardId": 1}'
```

### 5. Process Rewards
```bash
curl -X POST http://localhost:8080/api/rewards/process/1 \
  -H "Authorization: Bearer <token>"
```

### 6. View Reward Catalog
```bash
curl -X GET http://localhost:8080/api/catalog/categories \
  -H "Authorization: Bearer <token>"
```

### 7. Redeem Rewards
```bash
# Add item to cart
curl -X POST http://localhost:8080/api/cart/add \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "rewardItemId": 5,
    "quantity": 1
  }'

# Redeem cart
curl -X POST http://localhost:8080/api/cart/redeem/1 \
  -H "Authorization: Bearer <token>"
```

---

## 🎯 Key Design Decisions

### 1. **Configuration Over Code**
- All business rules in `application.yml`
- Changes require only restart, not redeployment
- Supports rapid policy adjustments

### 2. **Soft Delete for Customers**
- Preserves referential integrity
- Maintains transaction history
- Allows for potential restoration

### 3. **Idempotent Transaction Processing**
- `processed` flag prevents duplicate rewards
- Ensures data integrity
- Supports safe retries

### 4. **Customer-Level Rewards**
- Simplifies balance management
- Matches real-world bank behavior
- Easier to track lifetime earnings

### 5. **All-or-Nothing Redemption**
- Prevents partial failures
- Atomic transaction guarantees
- Better user experience

### 6. **JWT Stateless Authentication**
- Scalable architecture
- No server-side session storage
- Mobile-friendly

---

## 🐛 Edge Cases Handled

1. **Duplicate Credit Card**: Validates uniqueness across system
2. **Duplicate Email**: Prevents duplicate customer emails
3. **Self-Deletion**: Admin cannot delete own account
4. **Negative Balance**: Redemption validation prevents overdraft
5. **Empty Cart**: Cannot redeem empty cart
6. **Already Processed**: Transactions can only contribute to rewards once
7. **Soft-Deleted Customer**: Excluded from searches and operations

---

## 📝 Future Enhancements

- Angular frontend with role-based UI
- Customer dashboard with analytics
- Email notifications for redemptions
- Reward item inventory management
- Multi-currency support
- Export reports (PDF/Excel)
- Audit logging for all operations
- Batch transaction upload (CSV)

---

## 👨‍💻 Developer Notes

### Key Files to Understand
1. `SecurityConfig.java` - Security setup
2. `CustomerService.java` - Customer type calculation
3. `RewardService.java` - Reward calculation logic
4. `CartService.java` - Redemption business rules
5. `application.yml` - All configuration

### Adding New Business Rules
1. Add property to `application.yml`
2. Create/update `@ConfigurationProperties` class
3. Inject config into service
4. Use config value in business logic

### Debugging Tips
- Check `application.log` for errors
- Enable SQL logging: `spring.jpa.show-sql=true`
- Test APIs with Postman collection
- Use `DEBUG` logging level for security issues

---

## 📞 Support

For technical assistance or questions about AurumX:
- Review this README thoroughly
- Check API documentation above
- Inspect configuration examples
- Verify database seed scripts executed

---

**Built with ❤️ for production-grade enterprise banking systems.**
