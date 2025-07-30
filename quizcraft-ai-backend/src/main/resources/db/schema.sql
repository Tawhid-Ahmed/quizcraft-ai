-- Create enum for role types
CREATE TYPE role_type AS ENUM ('ADMIN', 'TEACHER', 'STUDENT');

-- Create roles table
CREATE TABLE roles
(
    id         BIGSERIAL PRIMARY KEY,
    name       role_type   NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE users
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(255)        NOT NULL,
    email             VARCHAR(255) UNIQUE NOT NULL,
    password          VARCHAR(255)        NOT NULL,
    profile_image_url VARCHAR(255),
    enabled           BOOLEAN                      DEFAULT true,
    created_at        TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMPTZ
);

-- Create user_roles junction table
CREATE TABLE user_roles
(
    user_id    BIGINT      NOT NULL,
    role_id    BIGINT      NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Create password_reset_tokens table
CREATE TABLE password_reset_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255) UNIQUE NOT NULL,
    user_id    BIGINT              NOT NULL,
    expires_at TIMESTAMPTZ         NOT NULL,
    used       BOOLEAN                      DEFAULT false,
    created_at TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create function to update updated_at timestamp
CREATE
OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
language 'plpgsql';

-- Create triggers to automatically update updated_at
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_roles_updated_at
    BEFORE UPDATE
    ON roles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_roles_updated_at
    BEFORE UPDATE
    ON user_roles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_password_reset_tokens_updated_at
    BEFORE UPDATE
    ON password_reset_tokens
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert default roles
INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('TEACHER'),
       ('STUDENT');

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users (email) WHERE deleted_at IS NULL;
CREATE INDEX idx_password_reset_tokens_token ON password_reset_tokens (token) WHERE used = false;
CREATE INDEX idx_password_reset_tokens_user ON password_reset_tokens (user_id);
CREATE INDEX idx_user_roles_user ON user_roles (user_id);
CREATE INDEX idx_user_roles_role ON user_roles (role_id);


-- 1. QUIZZES
CREATE TABLE quizzes
(
    id          BIGSERIAL PRIMARY KEY,
    title       TEXT        NOT NULL,
    description TEXT,
    language    VARCHAR(10) NOT NULL,
    published   BOOLEAN     DEFAULT FALSE,
    created_by  BIGINT      NOT NULL REFERENCES users (id),
    created_at  TIMESTAMPTZ DEFAULT now(),
    updated_at  TIMESTAMPTZ DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);

-- 2. QUESTIONS
-- CREATE TABLE questions
-- (
--     id          BIGSERIAL PRIMARY KEY,
--     quiz_id     BIGINT      NOT NULL REFERENCES quizzes (id) ON DELETE CASCADE,
--     type        VARCHAR(20) NOT NULL CHECK (type IN ('MCQ', 'TF', 'FILL_BLANK', 'DESCRIPTIVE')),
--     text        TEXT        NOT NULL,
--     explanation TEXT,
--     marks       INT         NOT NULL DEFAULT 1,
--     created_at  TIMESTAMPTZ          DEFAULT now(),
--     updated_at  TIMESTAMPTZ          DEFAULT now(),
--     deleted_at  TIMESTAMPTZ
-- );

CREATE TABLE questions
(
    id             BIGSERIAL PRIMARY KEY,
    quiz_id        BIGINT      NOT NULL REFERENCES quizzes (id) ON DELETE CASCADE,
    type           VARCHAR(20) NOT NULL CHECK (type IN ('MCQ', 'TF', 'FILL_BLANK', 'DESCRIPTIVE')),
    text           TEXT        NOT NULL,
    explanation    TEXT,
    marks          INT         NOT NULL DEFAULT 1,
    language       VARCHAR(10)          DEFAULT 'en',
    question_order INT,
    correct_answer TEXT,
    created_at     TIMESTAMPTZ          DEFAULT now(),
    updated_at     TIMESTAMPTZ          DEFAULT now(),
    deleted_at     TIMESTAMPTZ
);


-- 3. QUESTION OPTIONS (only for MCQ and TF)
-- CREATE TABLE question_options
-- (
--     id          BIGSERIAL PRIMARY KEY,
--     question_id BIGINT NOT NULL REFERENCES questions (id) ON DELETE CASCADE,
--     text        TEXT   NOT NULL,
--     is_correct  BOOLEAN DEFAULT FALSE
-- );

CREATE TABLE question_options
(
    id          BIGSERIAL PRIMARY KEY,
    question_id BIGINT  NOT NULL REFERENCES questions (id) ON DELETE CASCADE,
    text        TEXT    NOT NULL,
    is_correct  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ      DEFAULT now(),
    updated_at  TIMESTAMPTZ      DEFAULT now(),
    deleted_at  TIMESTAMPTZ
);


-- 4. SUBMISSIONS
CREATE TABLE submissions
(
    id              BIGSERIAL PRIMARY KEY,
    quiz_id         BIGINT NOT NULL REFERENCES quizzes (id),
    user_id         BIGINT NOT NULL REFERENCES users (id),
    submitted_at    TIMESTAMPTZ DEFAULT now(),
    score_total     FLOAT,
    graded_by_ai    BOOLEAN     DEFAULT FALSE,
    graded_by_human BOOLEAN     DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT now(),
    updated_at      TIMESTAMPTZ DEFAULT now()
);

-- 5. ANSWERS
CREATE TABLE answers
(
    id                 BIGSERIAL PRIMARY KEY,
    submission_id      BIGINT NOT NULL REFERENCES submissions (id) ON DELETE CASCADE,
    question_id        BIGINT NOT NULL REFERENCES questions (id),
    selected_option_id BIGINT REFERENCES question_options (id),
    text_answer        TEXT,
    score              FLOAT,
    review_status      VARCHAR(10) DEFAULT 'PENDING' CHECK (review_status IN ('PENDING', 'GRADED')),
    ai_feedback        TEXT,
    created_at         TIMESTAMPTZ DEFAULT now(),
    updated_at         TIMESTAMPTZ DEFAULT now()
);

-- 6. PROMPT TEMPLATES (optional AI templates for generating quizzes/questions)
CREATE TABLE prompt_templates
(
    id         BIGSERIAL PRIMARY KEY,
    name       TEXT        NOT NULL,
    template   TEXT        NOT NULL,
    type       VARCHAR(30) NOT NULL, -- e.g., "QUIZ_GENERATION", "ANSWER_GRADING"
    language   VARCHAR(10) DEFAULT 'en',
    created_by BIGINT REFERENCES users (id),
    created_at TIMESTAMPTZ DEFAULT now()
);

