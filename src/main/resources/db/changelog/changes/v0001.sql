--liquibase formatted sql
--changeset adven:0001
CREATE TABLE content_kind (
  str_id        TEXT PRIMARY KEY,
  name          TEXT,
  creation_date TIMESTAMP
);

CREATE TABLE content_field (
  id   INTEGER PRIMARY KEY,
  name TEXT,
  kind TEXT NOT NULL REFERENCES content_kind (str_id),
  type TEXT
);

CREATE TABLE campaign (
  id   INTEGER PRIMARY KEY,
  kind TEXT NOT NULL REFERENCES content_kind (str_id),
  data JSONB
);