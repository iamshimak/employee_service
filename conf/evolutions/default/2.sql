# Report schema

# --- !Ups
CREATE TABLE report (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  employee_id BIGINT REFERENCES employee(id),
  is_deleted BOOL DEFAULT FALSE
);

# --- !Downs
DROP TABLE IF EXISTS report