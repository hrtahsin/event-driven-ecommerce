# Figma Blueprint

This file is intentionally stored outside `docs/` so it can be committed even when `docs/*.md` and the root `README.md` are ignored for the first remote push.

## Repo-Tracked Blueprint

```mermaid
flowchart LR
    Customer["Customer App"] --> Gateway["API Gateway"]
    Admin["Admin App"] --> Gateway

    subgraph RequestLayer["Request Layer"]
        Gateway --> Identity["Identity Service\nUsers, roles, auth"]
        Gateway --> Catalog["Catalog Service\nProducts, categories"]
        Gateway --> Order["Order Service\nOrders, outbox"]
        Gateway --> Inventory["Inventory Service\nInventory reads and restock"]
    end

    subgraph EventLayer["Event Layer"]
        Kafka["Kafka Topics"]
        Payment["Payment Service\nPayment simulation"]
        Notification["Notification Service\nNotification audit"]
    end

    Order -->|"order.created"| Kafka
    Kafka -->|"reserve or reject"| Inventory
    Inventory -->|"inventory.reserved / inventory.rejected"| Kafka
    Kafka -->|"payment on reservation"| Payment
    Payment -->|"payment.succeeded / payment.failed"| Kafka
    Kafka -->|"confirm or cancel"| Order
    Kafka -->|"notify on business outcome"| Notification
    Kafka -->|"release reservation on failure"| Inventory

    Identity --> IdentityDb[("identity_db")]
    Catalog --> CatalogDb[("catalog_db")]
    Order --> OrderDb[("order_db")]
    Inventory --> InventoryDb[("inventory_db")]
    Payment --> PaymentDb[("payment_db")]
    Notification --> NotificationDb[("notification_db")]
```

## FigJam Source

- [Open FigJam diagram](https://www.figma.com/online-whiteboard/create-diagram/180f4244-b3ad-454c-a1bd-01fd12483d6b?utm_source=chatgpt&utm_content=edit_in_figjam&oai_id=&request_id=18f600e3-5444-4fd5-b502-4e5ef09549dd)
- [Preview image](https://s3-alpha.figma.com/thumbnails/209a6fab-5097-46d0-ba04-a119413064a3?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAQ4GOSFWC4EJV4ZNX%2F20260420%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20260420T004004Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=ae8c23e8feceda32a3b5c6f4e0cc7df0cbd82844f187cec5b43226d464a8c371)
