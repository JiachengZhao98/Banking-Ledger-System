Real-time Transaction System
===============================
## Overview
A simple bank ledger system that utilizes the [event sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) pattern to maintain a transaction history. The system should allow users to perform basic banking operations such as depositing funds, withdrawing funds, and checking balances. The ledger should maintain a complete and immutable record of all transactions, enabling auditability and reconstruction of account balances at any point in time.

## Details
The [included service.yml](service.yml) is the OpenAPI 3.0 schema to a service we would like you to create and host.

The service accepts two types of transactions:
1) Loads: Add money to a user (credit)

2) Authorizations: Conditionally remove money from a user (debit)

Every load or authorization PUT should return the updated balance following the transaction. Authorization declines should be saved, even if they do not impact balance calculation.

## Bootstrap instructions
### Prerequisites
Before you run this server locally, ensure you have the following installed:
- Java JDK 11 or newer
- Maven 4.0.0 or newer (for building the application)
- An IDE of your choice (Eclipse, IntelliJ IDEA, etc.)

### Running the Application Locally
1. **Clone the Repository:**

   ```
   gh repo clone JiachengZhao98/Banking-Ledger-System

   cd Banking-Ledger-System
   ```

2. **Build the Project:**

     `mvn clean install`

3. **Run the Application:**

	You can run the application using command line: `mvn spring-boot:run`, or using the built-in tools from the IDE of your choice. The server will be started on `http://localhost:8080`.

4. **Access the API:**
   You can access the API endpoints using any HTTP client like Postman or via curl:
    - for **ping**: `curl http://localhost:8080/api/ping`

   - for **load**:
   ```
   curl -X PUT http://localhost:8080/api/load \
     -H "Content-Type: application/json" \
     -d '{
     "userId": "123",
     "messageId": "456",
     "transactionAmount": {
     "amount": "100.00",
     "currency": "USD",
     "debitOrCredit": "CREDIT"
     }
     }'
   ```

   - for **authorization**:

   ```
   curl -X PUT http://localhost:8080/api/authorization \
   -H "Content-Type: application/json" \
   -d '{
   "userId": "123",
   "messageId": "456",
   "transactionAmount": {
   "amount": "50.00",
   "currency": "USD",
   "debitOrCredit": "DEBIT"
   }
   }'
    ```

   - for **checkBalance**:

    ```
   curl -X GET http://localhost:8080/api/checkBalance \
   -H "Content-Type: application/json" \
   -d '{
   "userId": "123",
   "messageId": "4568"
   }'
   ```

   **Format of TransactionRequest for load and authorization:**

    ```
    {
    "userId": "string",
    "messageId": "string",
    "transactionAmount": {
    "amount": "string",
    "currency": "string",
    "debitOrCredit": "string"
    }
    }
    ```

    **Format of checkBalanceRequest:**

    ```
    {
    "userId": "string",
    "messageId": "string"
    }
    ```

5. **Testing:**
	Run unit tests, integration test and sample test through your IDE or using Maven:
	* Command line: `mvn test` to execute all tests.
    * Command line: `mvn test -Dtest=SampleTest` to execute sample_tests from `src/test/java/resources/sample_tests`
    * Command line: `mvn test -Dtest=TransactionProcessorTest` to execute unit tests.
    * Command line: `mvn test -Dtest=TransactionControllerIntegrationTest` to execute integration tests.

---

## Design considerations

### Architecture
Utilizing the **Event Sourcing** pattern, the system ensures all state changes are stored as events, enabling audit trails, system resilience, and scalability.

### Key Aspects
- **Auditability & Consistency**: Ensures historical data integrity and aligns with domain-driven design.
- **Performance**: Optimizes performance with efficient event handling.
- **Precision**: Uses `BigDecimal` for accurate financial calculations, avoiding typical floating-point errors.
- **Optimistic Locking**: Manages concurrent transactions to enhance throughput and reduce lock contention.
- **In-Memory Database**: Simplifies development with fast setup and teardown, suitable for non-production use. Production environments should implement persistent storage.

---

## Assumptions
During the design and implementation of the banking ledger system, the following assumptions were made:

- **Security**: Basic security measures are implemented, assuming enhancement for public deployment.
- **User Transactions Without Initial Load**: The system allows users to check balances and attempt withdrawals even if they have not deposited (loaded) money initially. For such users, the balance returned for checking is 0. If a withdrawal attempt is made, the transaction is denied, and the response shows a balance of 0 with a response code of "DENIED".
- **Support for Checking Balance**: A new GET API endpoint "checkBalance" has been implemented to support balance checking operations as required by the system's functionality.

---

## Deployment considerations
- **Cloud Provider**: AWS for its robust and scalable infrastructure.
- **Containerization**: Docker for packaging the application, ensuring consistency across development, testing, and production environments.
- **Orchestration**: Kubernetes to manage the application deployment, scaling, and management.
- **Database**: Amazon RDS or DynamoDB depending on the consistency requirements and structure of the data.
- **CI/CD**: Jenkins or GitHub Actions for continuous integration and deployment, automating the build, test, and deployment process.
- **Monitoring and Logging**: Integration with tools like Prometheus for monitoring and ELK Stack for logging to ensure high availability and real-time monitoring of the application health.
