CREATE TABLE IF NOT EXISTS organisations_schema.orders
(
    id                  UUID            PRIMARY KEY,
    ver                 INTEGER         NOT NULL,
    created_datetime    TIMESTAMP       NOT NULL,
    order_status        VARCHAR(30)     NOT NULL,
    order_amount        NUMERIC(19,4)   NOT NULL,
    shipped_amount      NUMERIC(19,4)   NOT NULL,
    merchant_id         UUID            NOT NULL
);
