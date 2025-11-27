-- Создание схемы если не существует
CREATE SCHEMA IF NOT EXISTS storage;

-- Создание таблицы пользователей
CREATE TABLE storage.t_user
(
    c_id       SERIAL PRIMARY KEY,
    c_email    VARCHAR(512)  NOT NULL UNIQUE,
    c_password VARCHAR(1024) NOT null
);

-- Создание таблицы проектов
CREATE TABLE storage.t_project
(
    c_id                  SERIAL PRIMARY KEY,
    c_project_name        VARCHAR(512) NOT NULL,
    c_project_description TEXT,
    c_project_owner       INTEGER REFERENCES storage.t_user (c_id),
    c_deadline            DATE
);

-- Создание таблицы ролей в проекте
CREATE TABLE storage.t_project_user_role
(
    c_id        SERIAL PRIMARY KEY,
    c_role_name VARCHAR(128) NOT NULL
);

-- Создание таблицы связи пользователей с проектами и ролями
CREATE TABLE storage.t_project_membership
(
    c_id        SERIAL PRIMARY KEY,
    c_user_role INTEGER NOT NULL REFERENCES storage.t_project_user_role (c_id),
    c_user      INTEGER NOT NULL REFERENCES storage.t_user (c_id),
    c_project   INTEGER NOT NULL REFERENCES storage.t_project (c_id),
    UNIQUE (c_user, c_project) -- Один пользователь может иметь только одну роль в проекте
);

-- Заполнение ролей
INSERT INTO storage.t_project_user_role (c_role_name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_VIEWER'),
       ('ROLE_REVIEWER'),
       ('ROLE_STUDENT');