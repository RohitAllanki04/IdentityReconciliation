# 🔗 Bitespeed Identity Reconciliation Service

A backend REST API service built with **Spring Boot** that identifies and consolidates customer contact information across multiple purchases on FluxKart.com.

---

## 📌 Problem Statement

Customers often place orders using different email addresses or phone numbers. Bitespeed needs to track and link these contacts to recognize the same customer across multiple purchases.

This service exposes a `/identify` endpoint that:
- Links contacts that share an email or phone number
- Maintains one **primary** contact per customer cluster
- Converts newer primary contacts to **secondary** when clusters merge
- Creates new contacts when new information is discovered

---

## 🚀 Live Demo

**Base URL:** `https://your-app-name.onrender.com`

**Endpoint:** `POST /identify`

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|-------|
| Java 17 | Programming Language |
| Spring Boot 3.x | Backend Framework |
| Spring Data JPA | ORM / Database Layer |
| Hibernate | JPA Implementation |
| MySQL | Relational Database |
| Lombok | Boilerplate Reduction |
| Maven | Build Tool |
| Docker | Containerization |
| Render | Cloud Deployment |

---

## 📁 Project Structure

```
src/main/java/com/bitespeed/identity/
├── IdentityApplication.java        # Main entry point
├── controller/
│   └── ContactController.java      # REST endpoint
├── service/
│   └── ContactService.java         # Business logic
├── repository/
│   └── ContactRepository.java      # DB queries
├── model/
│   └── Contact.java                # Entity class
└── dto/
    ├── IdentifyRequest.java         # Request DTO
    └── IdentifyResponse.java        # Response DTO
```

---

## 📊 Database Schema

```sql
CREATE TABLE contact (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    phone_number     VARCHAR(255),
    email            VARCHAR(255),
    linked_id        INT,                          -- ID of primary contact
    link_precedence  ENUM('primary', 'secondary'),
    created_at       DATETIME,
    updated_at       DATETIME,
    deleted_at       DATETIME
);
```

---

## 📬 API Reference

### POST `/identify`

Identifies and consolidates a customer's contact information.

**Request Body:**
```json
{
  "email": "string (optional)",
  "phoneNumber": "string (optional)"
}
```

**Response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["primary@email.com", "secondary@email.com"],
    "phoneNumbers": ["123456", "789012"],
    "secondaryContactIds": [2, 3]
  }
}
```

---

## 🧪 Example Scenarios

### Scenario 1 — New Customer
**Request:**
```json
{
  "email": "lorraine@hillvalley.edu",
  "phoneNumber": "123456"
}
```
**Response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["lorraine@hillvalley.edu"],
    "phoneNumbers": ["123456"],
    "secondaryContactIds": []
  }
}
```

---

### Scenario 2 — Returning Customer with New Email
**Request:**
```json
{
  "email": "mcfly@hillvalley.edu",
  "phoneNumber": "123456"
}
```
**Response:**
```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["lorraine@hillvalley.edu", "mcfly@hillvalley.edu"],
    "phoneNumbers": ["123456"],
    "secondaryContactIds": [2]
  }
}
```

---

### Scenario 3 — Merging Two Primary Contacts
When a request links two previously separate contact clusters, the **older one stays primary** and the newer one becomes secondary.

**Request:**
```json
{
  "email": "george@hillvalley.edu",
  "phoneNumber": "717171"
}
```
**Response:**
```json
{
  "contact": {
    "primaryContatctId": 11,
    "emails": ["george@hillvalley.edu", "biffsucks@hillvalley.edu"],
    "phoneNumbers": ["919191", "717171"],
    "secondaryContactIds": [27]
  }
}
```

---

## ⚙️ Running Locally

### Prerequisites
- Java 17+
- Maven
- MySQL running on port 3306

### 1. Clone the repository
```bash
git clone https://github.com/RohitAllanki04/IdentityReconciliation.git
```

### 2. Create MySQL database
```sql
CREATE DATABASE identity;
```

### 3. Configure `application.properties`
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/identity
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

### 4. Build and Run
```bash
mvn clean package -DskipTests
java -jar target/identity-0.0.1-SNAPSHOT.jar
```

### 5. Test
```bash
curl -X POST http://localhost:8080/identify \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","phoneNumber":"123456"}'
```

---

## 🐳 Running with Docker

### Build image
```bash
docker build -t bitespeed-identity .
```

### Run container
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/identity \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  identity
```

---

## 🔄 Core Logic Flow

```
POST /identify { email, phone }
        │
        ▼
  Find contacts matching email OR phone
        │
   ┌────┴────┐
   │         │
No match   Matches found
   │         │
Create     Collect full contact cluster
primary    │
contact    Find oldest → stays as PRIMARY
           │
           Demote newer primaries → SECONDARY
           │
           New info in request? → create new SECONDARY
           │
           Build and return consolidated response
```

---

## 🌐 Deployment (Render + Railway)

This project is deployed using:
- **Render** — hosts the Spring Boot Docker container
- **Railway** — hosts the MySQL database

### Environment Variables on Render:

| Key | Value |
|-----|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<railway-host>:3306/railway` |
| `SPRING_DATASOURCE_USERNAME` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | `<your-password>` |

---
