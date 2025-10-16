# 🏦 Dock Digital API

## 📌 Overview
The **Dock Digital API** is a microservice responsible for managing **Holders** (clients) and their **Digital Accounts**.  
It supports operations such as account creation, deposits, withdrawals, statement queries, blocking/unblocking, and account closure — all compliant with business rules such as non-negative balances and daily withdrawal limits.

The system follows the **Hexagonal Architecture (Ports and Adapters)**, ensuring clear separation between domain logic and external layers.

---

## 🛠️ Technologies Used
- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway** (Database migration)
- **Springdoc OpenAPI (Swagger)**
- **Docker Compose**
- **Lombok**

---

## 🚀 How to Run the Application

### Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### Run with Docker
```bash
docker-compose down -v
docker-compose up --build
```

**Services started:**
- PostgreSQL → `localhost:5432`
- Application → `localhost:8080`

Once started, open **Swagger UI** at:  
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---


## 📘 Endpoints

### 1️⃣ Holders (`/holders`)

#### ➕ Create a new Holder
**POST** `/holders`

**Request body:**
```json
{
  "cpf": "12345678900",
  "name": "John Doe"
}
```

**Response:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-1234-56789abcdef0",
  "cpf": "12345678900",
  "name": "John Doe"
}
```

#### 🔍 Find Holder by CPF
**GET** `/holders/{cpf}`

**Response:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-1234-56789abcdef0",
  "cpf": "12345678900",
  "name": "John Doe"
}
```

---

### 2️⃣ Accounts (`/accounts`)

#### 🏗️ Create a new Account
**POST** `/accounts`

**Request body:**
```json
{
  "holderCpf": "12345678900"
}
```

**Response:**
```json
{
  "id": "e8d9c2a3-456b-7890-1234-abcdef567890",
  "number": "12345678",
  "agency": "0001",
  "balance": 0.00,
  "status": "ACTIVE",
  "blocked": false
}
```

---

#### 💰 Deposit
**POST** `/accounts/{id}/deposit`

**Request body:**
```json
{
  "amount": 500.00
}
```

**Response:**
```json
{
  "id": "e8d9c2a3-456b-7890-1234-abcdef567890",
  "balance": 500.00
}
```

---

#### 💸 Withdraw
**POST** `/accounts/{id}/withdraw`

**Request body:**
```json
{
  "amount": 200.00
}
```

**Response:**
```json
{
  "id": "e8d9c2a3-456b-7890-1234-abcdef567890",
  "balance": 300.00
}
```

> ⚠️ Withdrawals are limited to **R$2,000 per day** and cannot exceed available balance.

---

#### 📄 Statement (by period)
**GET** `/accounts/{id}/statement?start=2025-10-01T00:00:00-03:00&end=2025-10-15T23:59:59-03:00`

**Response:**
```json
[
  {
    "id": "tx-1",
    "type": "DEPOSIT",
    "amount": 500.00,
    "timestamp": "2025-10-10T10:00:00-03:00",
    "description": "Initial deposit"
  },
  {
    "id": "tx-2",
    "type": "WITHDRAW",
    "amount": 200.00,
    "timestamp": "2025-10-12T15:30:00-03:00",
    "description": "ATM withdrawal"
  }
]
```

---

#### 🔒 Close Account
**POST** `/accounts/{id}/close`

Closes an account only if the **balance is zero**.

**Response:**
```json
{
  "id": "e8d9c2a3-456b-7890-1234-abcdef567890",
  "status": "CLOSED"
}
```

---

#### 💵 Check Balance
**GET** `/accounts/{id}/balance`

**Response:**
```json
{
  "id": "e8d9c2a3-456b-7890-1234-abcdef567890",
  "balance": 300.00,
  "status": "ACTIVE"
}
```

---


## ⚙️ Business Rules
- A **Holder** can have **only one account per CPF**.
- **Deposits** are allowed only for *active* and *unblocked* accounts.
- **Withdrawals** are limited to **R$2,000/day** and cannot exceed the available balance.
- **Account closure** is allowed only if the balance is zero.
- **Statements** can be queried by ISO 8601 date range (with offset).

---

## 🧪 Tests
To execute all unit tests:
```bash
mvn test
```

---

## 🧾 License
MIT License © 2025 Dock Digital
