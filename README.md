# Payment Service

Spring Boot 3 (Java 17) service that:
1. Listens to topic `order-events`
2. Creates a `Payment` row (with the `orderId`)
3. Publishes the result to topic `payment-events` (key = orderId, status = COMPLETED or FAILED)

## Run
1. Start Kafka on localhost:9092 (or `docker compose up -d`).
2. `mvn spring-boot:run`   (or open in IntelliJ / Eclipse and run PaymentServiceApplication)

Service runs on port 8087.

## Check
- GET http://localhost:8087/payments
- GET http://localhost:8087/payments/order/{orderId}
- H2 console: http://localhost:8087/h2-console  (JDBC URL: jdbc:h2:mem:paymentdb, user: sa)

## Mock rule
Amount <= 100000 -> COMPLETED, otherwise FAILED. Change it in PaymentProcessor.

## Important
Event field names must match your Order Service event:
OrderEvent(orderId, customerEmail, amount)
If your Order Service uses different names, change the record in `dto/OrderEvent.java`.

## Other services
- Order Service: listen to `payment-events`, update order status.
- Email Service: listen to `payment-events`, send emails.
