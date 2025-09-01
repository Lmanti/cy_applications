DROP TABLE IF EXISTS applications CASCADE;
DROP TABLE IF EXISTS loan_status CASCADE;
DROP TABLE IF EXISTS loan_type CASCADE;

CREATE TABLE loan_status (
    loan_status_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE loan_type (
    loan_type_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    min_amount NUMERIC(12, 2) NOT NULL,
    max_amount NUMERIC(12, 2) NOT NULL,
    interest_rate NUMERIC(5, 2) NOT NULL,
    auto_validation BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE applications (
    application_id UUID PRIMARY KEY,
    user_id_number BIGINT NOT NULL,
    loan_amount NUMERIC(12, 2) NOT NULL,
    loan_term NUMERIC(5, 2) NOT NULL,
    loan_type_id INTEGER NOT NULL,
    loan_status_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_type_id) REFERENCES loan_type (loan_type_id),
    FOREIGN KEY (loan_status_id) REFERENCES loan_status (loan_status_id)
);