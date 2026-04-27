CREATE INDEX idx_orders_customer_created_at ON orders (customer_id, created_at DESC);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
