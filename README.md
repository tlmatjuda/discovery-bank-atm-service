# Discovery Bank ATM Service

Spring Boot 3.5 (Java 17) REST API for:
- Transactional balance queries
- Foreign currency balance queries (with ZAR conversion)
- ATM withdrawals with denomination-aware dispensing
- SQL reporting deliverables

## Tech Stack
- Java 17
- Spring Boot 3.5.11
- Spring Web
- Spring Validation
- Spring JDBC Client (via Spring Data JDBC infrastructure)
- Flyway
- H2 Database
- springdoc-openapi (Swagger UI)

## How To Run

### 1. Build
```bash
./mvnw clean package
```

### 2. Start service
```bash
./mvnw spring-boot:run
```

Service defaults:
- Base URL: `http://localhost:8088/discovery-atm`
- Swagger UI: `http://localhost:8088/discovery-atm/swagger-ui`
- OpenAPI JSON: `http://localhost:8088/discovery-atm/api-docs`

## Database Initialization

On startup, Flyway runs automatically against H2 using:
- `src/main/resources/db/migration/V1__schema.sql`
- `src/main/resources/db/migration/V2__data.sql`

Default runtime DB is file-based H2:
- `src/main/resources/data/discovery_atm.mv.db`

This means local data persists across app restarts unless DB files are removed.

Test profile uses in-memory H2:
- `src/test/resources/application-test.yaml`

## API Endpoints

### 1. Transactional balances
- `GET /queryTransactionalBalances?clientId=<id>`
- Response shape: `client`, `accounts`, `result`
- `curl`:
```bash
curl --location 'http://localhost:8088/discovery-atm/queryTransactionalBalances?clientId=1'
```
- Sample response:
```json
{
  "client": {
    "clientId": 1,
    "title": "Ms",
    "name": "Marylou",
    "surname": "Melcher"
  },
  "accounts": [
    {
      "accountNumber": "4067342946",
      "typeCode": "CHQ",
      "accountTypeDescription": "Cheque Account",
      "currencyCode": "ZAR",
      "conversionRate": 1.0,
      "balance": 13603.55,
      "zarBalance": 13603.55,
      "accountLimit": 10000.0
    }
  ],
  "result": {
    "success": true,
    "statusCode": 200,
    "statusReason": "Success"
  }
}
```

### 2. Currency balances
- `GET /queryCcyBalances?clientId=<id>`
- Response shape: `client`, `accounts`, `result`
- `curl`:
```bash
curl --location 'http://localhost:8088/discovery-atm/queryCcyBalances?clientId=1'
```
- Sample response:
```json
{
  "client": {
    "clientId": 1,
    "title": "Ms",
    "name": "Marylou",
    "surname": "Melcher"
  },
  "accounts": [
    {
      "accountNumber": "9164010053",
      "typeCode": "CFCA",
      "accountTypeDescription": "Customer Foreign Currency Account",
      "currencyCode": "AUD",
      "conversionRate": 0.113,
      "ccyBalance": 41693.22,
      "zarBalance": 367664.903,
      "accountLimit": 0.0
    }
  ],
  "result": {
    "success": true,
    "statusCode": 200,
    "statusReason": "Success"
  }
}
```

### 3. Withdraw
- `POST /withdraw`
- Request body:
```json
{
  "clientId": 6,
  "atmId": 1,
  "accountNumber": "4078108908",
  "requiredAmount": 150.00
}
```
- Response shape: `client`, `account`, `denomination`, `result`
- `curl`:
```bash
curl --location 'http://localhost:8088/discovery-atm/withdraw' \
--header 'Content-Type: application/json' \
--data '{
  "clientId": 6,
  "atmId": 1,
  "accountNumber": "4078108908",
  "requiredAmount": 150.00
}'
```
- Sample response:
```json
{
  "client": {
    "clientId": 6,
    "title": "Mrs",
    "name": "Christeen",
    "surname": "Clever"
  },
  "account": {
    "accountNumber": "4078108908",
    "typeCode": "CHQ",
    "accountTypeDescription": "Cheque Account",
    "currencyCode": "ZAR",
    "conversionRate": 1.0,
    "balance": 22169.08,
    "zarBalance": 22169.08,
    "accountLimit": 10000.0
  },
  "denomination": [
    {
      "denominationId": 4,
      "denominationValue": 100.0,
      "count": 1
    },
    {
      "denominationId": 3,
      "denominationValue": 50.0,
      "count": 1
    }
  ],
  "result": {
    "success": true,
    "statusCode": 200,
    "statusReason": "Success"
  }
}
```

## SQL Reporting Deliverables

- `src/main/resources/reports/report_highest_transactional_account.sql`
- `src/main/resources/reports/report_aggregate_financial_position.sql`

## Running Tests

```bash
./mvnw test
```

Focused integration tests cover:
- no accounts case
- sorting correctness (transactional desc, currency asc by ZAR)
- successful withdrawal
- insufficient funds
- ATM not registered/unfunded
- invalid client/account

## Assumptions And Deviations

- CHQ overdraft limit is applied as fixed `R10000`.
- Withdrawal dispensing is note-only (`DENOMINATION_TYPE_CODE = 'N'`), no coin dispensing.
- If exact amount cannot be dispensed, API returns:
  - `Amount not available, would you like to draw <R x>`
  where `x` is best lower dispensable amount based on ATM note inventory.
- Implementation uses `JdbcClient` + explicit SQL/repository mapping rather than JPA entity modeling.
- No authentication/authorization was added (not specified in assignment scope).
