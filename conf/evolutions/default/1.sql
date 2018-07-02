# Employee schema

# --- !Ups
CREATE TABLE employee (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  prefix TEXT,
  role TEXT NOT NULL,
  is_deleted BOOL DEFAULT FALSE
);

# --- !Downs
DROP TABLE IF EXISTS employee