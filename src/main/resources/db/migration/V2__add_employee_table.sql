CREATE TABLE erp_employee (
    id BIGSERIAL PRIMARY KEY,
    erp_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Insert a default admin for testing. Password is 'admin123'
-- using BCrypt hash for 'admin123' -> $2a$10$aS1p0EPSAz82JjeKRqjMaes0TEiwS3DUTMYf1BEib6vAgg435iRaC
INSERT INTO erp_employee (erp_code, name, email, password_hash, role, active)
VALUES ('EMP-ADMIN01', 'Administrador Principal', 'admin@edupay.com', '$2a$10$aS1p0EPSAz82JjeKRqjMaes0TEiwS3DUTMYf1BEib6vAgg435iRaC', 'ADMIN', true);
