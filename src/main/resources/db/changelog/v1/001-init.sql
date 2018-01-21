--liquibase formatted sql
--changeset init:001
CREATE TABLE content_kind (
  str_id        TEXT PRIMARY KEY,
  name          TEXT,
  creation_date TIMESTAMP
);

CREATE TABLE content_field (
  id   SERIAL8 PRIMARY KEY,
  name TEXT,
  kind TEXT NOT NULL REFERENCES content_kind (str_id),
  type TEXT,
  ord INTEGER
);

CREATE TABLE campaign (
  id         SERIAL8 PRIMARY KEY,
  kind       TEXT NOT NULL REFERENCES content_kind (str_id),
  data       JSONB,
  name       TEXT NOT NULL,
  start_date TIMESTAMP,
  end_date   TIMESTAMP,
  CHECK (name <> '')
);

CREATE TABLE place (
  id   SERIAL8 PRIMARY KEY,
  name TEXT NOT NULL UNIQUE,
  CHECK (name <> '')
);

INSERT INTO place (name) VALUES ('Мой помощник'), ('Предложение банка'), ('Экран успеха');

CREATE TABLE user_info (
  id       SERIAL8 PRIMARY KEY,
  name     TEXT    NOT NULL,
  login    TEXT    NOT NULL UNIQUE,
  password TEXT    NOT NULL,
  role     TEXT    NOT NULL,
  locked   BOOLEAN NOT NULL
);