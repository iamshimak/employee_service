# Employee schema

# --- !Ups
create table `employee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` TEXT NOT NULL,
  `prefix` TEXT,
  `role` BIGINT NOT NULL
);

# --- !Downs
drop table `employee`