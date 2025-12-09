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
    c_created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    c_updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    c_deadline            TIMESTAMP
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
       ('ROLE_PLANNER'),
       ('ROLE_STUDENT');

CREATE TABLE storage.t_task_status
(
    c_id     SERIAL PRIMARY KEY,
    c_status VARCHAR(64) UNIQUE NOT NULL
);

-- Заполнение task_status
INSERT INTO storage.t_task_status (c_status)
VALUES ('Completed'),
       ('Planner'),
       ('In process'),
       ('Delayed');

CREATE TABLE storage.t_reviewer_status
(
    c_id     SERIAL PRIMARY KEY,
    c_status VARCHAR(64) UNIQUE NOT NULL
);

-- Заполнение teacher_status
INSERT INTO storage.t_reviewer_status (c_status)
VALUES ('Accepted'),
       ('Rejected'),
       ('None');


CREATE TABLE storage.t_project_component
(
    c_id                          SERIAL PRIMARY KEY,
    c_title                       VARCHAR(512) NOT NULL,
    c_description                 TEXT,
    c_project_id                  INTEGER      NOT NULL REFERENCES storage.t_project (c_id),
    c_project_component_parent_id INTEGER REFERENCES storage.t_project_component (c_id),
    c_deadline                    TIMESTAMP    NOT NULL,
    c_start_date                  TIMESTAMP    NOT NULL,
    c_creator_id                  INTEGER REFERENCES storage.t_project_membership (c_id),
    c_created_at                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    c_pos                         INTEGER
);


CREATE TABLE storage.t_student_task_status
(
    c_id                   SERIAL PRIMARY KEY,
    c_student_id           INTEGER NOT NULL REFERENCES storage.t_project_membership (c_id),
    c_project_component_id INTEGER NOT NULL REFERENCES storage.t_project_component (c_id),
    c_task_status_id       INTEGER NOT NULL REFERENCES storage.t_task_status (c_id),

    CONSTRAINT uk_project_component_student_unique UNIQUE (c_project_component_id)
);

CREATE TABLE storage.t_reviewer_task_status
(
    c_id                   SERIAL PRIMARY KEY,
    c_reviewer_id          INTEGER NOT NULL REFERENCES storage.t_project_membership (c_id),
    c_project_component_id INTEGER NOT NULL REFERENCES storage.t_project_component (c_id),
    c_task_status_id       INTEGER NOT NULL REFERENCES storage.t_reviewer_status (c_id),

    CONSTRAINT uk_project_component_reviewer_unique UNIQUE (c_project_component_id)
);

CREATE TABLE storage.t_task_maker
(
    c_id                   SERIAL PRIMARY KEY,
    c_membership_id        INTEGER NOT NULL REFERENCES storage.t_project_membership (c_id),
    c_project_component_id INTEGER NOT NULL REFERENCES storage.t_project_component (c_id),

    CONSTRAINT uk_membership_project_unique UNIQUE (c_membership_id, c_project_component_id)
);

CREATE TABLE storage.t_comment
(
    c_id                   SERIAL PRIMARY KEY,
    c_comment              TEXT    NOT NULL,
    c_commenter            INTEGER NOT NULL REFERENCES storage.t_project_membership (c_id),
    c_project_component_id INTEGER NOT NULL REFERENCES storage.t_project_component (c_id),
    c_created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    c_updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

