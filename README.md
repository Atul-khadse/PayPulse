# PayPulse ⚡

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)

**PayPulse** is a production-grade, high-concurrency FinTech wallet ledger backend. It is designed to safely process financial transfers under heavy load by enforcing strict ACID properties, mathematically preventing deadlocks, and guaranteeing idempotency to eliminate double-spending.

## 🚀 Key Engineering Features

* **Double-Entry Accounting:** Every transfer generates immutable DEBIT and CREDIT ledger entries within an atomic database transaction. No money is ever created or destroyed.
* **Deadlock Prevention via Ordered Locks:** Prevents circular wait deadlocks during high-contention transfers by mathematically sorting Wallet IDs before acquiring row-level database locks.
* **Strict Concurrency Control:** Implements JPA Pessimistic Write Locking (`SELECT FOR UPDATE`) to queue simultaneous transfer requests safely at the database level, preventing race conditions and balance drift.
* **Idempotency Framework:** Protects against network retry storms and duplicate charges. Every request requires an `Idempotency-Key` header, backed by database-level unique constraints for sub-millisecond duplicate detection.
* **Automated Concurrency Testing:** Includes JUnit 5 multithreaded test suites using `ExecutorService` and `CountDownLatch` to prove balance integrity under aggressive parallel workloads.

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.3.0
* **Data Access:** Spring Data JPA / Hibernate
* **Database:** PostgreSQL 16
* **Containerization:** Docker & Docker Compose
* **API Documentation:** OpenAPI / Swagger UI
* **Testing:** JUnit 5, H2 In-Memory Database

---

## 🚦 Getting Started

### Prerequisites
* Docker and Docker Compose installed.
* Java 21 & Maven (if running locally outside of Docker).

### Run via Docker (Recommended)
The easiest way to spin up the application and the PostgreSQL database is via Docker Compose:

```bash
# Clone the repository
git clone [https://github.com/yourusername/paypulse.git](https://github.com/yourusername/paypulse.git)
cd paypulse

# Build and start the containers
docker-compose up --build -d
