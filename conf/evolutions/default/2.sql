# --- !Ups

CREATE TABLE orders (
  id              INTEGER AUTO_INCREMENT PRIMARY KEY,
  ticket_block_id INTEGER,
  customer_name   VARCHAR,
  customer_email  VARCHAR,
  ticket_quantity INTEGER,
  timestamp       DATETIME,
  FOREIGN KEY (ticket_block_id) REFERENCES ticket_blocks (id)
);

INSERT INTO orders (ticket_block_id, customer_name, customer_email, ticket_quantity, timestamp)
VALUES (1, 'Test Testic', 'test@test.com', 4, '2008-01-01 00:00:01');

# --- !Downs

DROP TABLE IF EXISTS orders;