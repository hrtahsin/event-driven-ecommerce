CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    order_id UUID,
    notification_type VARCHAR(50) NOT NULL,
    destination VARCHAR(255),
    payload JSONB NOT NULL,
    status VARCHAR(30) NOT NULL,
    correlation_id VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMPTZ
);

CREATE TABLE delivery_attempts (
    id UUID PRIMARY KEY,
    notification_id UUID NOT NULL REFERENCES notifications (id),
    attempt_number INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    error_message VARCHAR(255),
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE processed_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    consumer_group VARCHAR(100) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (event_id, consumer_group)
);
