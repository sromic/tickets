# --- !Ups

CREATE TABLE events (
  id      INTEGER AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR,
  start   DATETIME,
  end     DATETIME,
  address VARCHAR,
  city    VARCHAR,
  state   VARCHAR,
  country CHAR(2)
);

CREATE TABLE ticket_blocks (
  id           INTEGER AUTO_INCREMENT PRIMARY KEY,
  event_id     INTEGER,
  name         VARCHAR,
  product_code VARCHAR(40),
  price        DECIMAL,
  initial_size INTEGER,
  sale_start   DATETIME,
  sale_end     DATETIME,
  FOREIGN KEY (event_id) REFERENCES events (id)
);

INSERT INTO events
(name, start, end, address, city, state, country)
VALUES
  ('Kojella2', '2014-04-17 8:00:00-07:00', '2014-04-19 23:00:00-07:00',
   '124 Paper St.', 'Palm Desert', 'CA', 'US');

INSERT INTO events
(name, start, end, address, city, state, country)
VALUES
  ('Kojella', '2014-04-17 8:00:00-07:00', '2014-04')

# --- !Downs

DROP TABLE IF EXISTS ticket_blocks;
DROP TABLE IF EXISTS events;