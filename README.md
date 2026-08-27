Client
  |
  | POST /orders
  ↓
Order Service
  |
  | save order = PENDING
  |
  | Kafka send
  ↓
Kafka
  |
  | product-stock-check
  ↓
Product Service
  |
  | check database
  |
  | Kafka send
  ↓
Kafka
  |
  | product-stock-response
  ↓
Order Service
  |
  | update order
  ↓
CONFIRMED / OUT_OF_STOCK
