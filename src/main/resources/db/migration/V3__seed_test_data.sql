-- Seed test data to populate H2 or Railway PostgreSQL database for development and testing

-- 1. Insert families
INSERT INTO erp_family (id, external_id, tutor_name, tutor_email, active) VALUES
(1, 'FAM-JUAN01', 'Juan Perez', 'juan.perez@example.com', true),
(2, 'FAM-MARIA02', 'Maria Lopez', 'maria.lopez@example.com', true);

-- Adjust the sequence to start after the hardcoded IDs for serial key auto-generation
SELECT setval('erp_family_id_seq', 2);

-- 2. Insert student references
INSERT INTO erp_student_ref (id, family_id, external_id, full_name, active) VALUES
(1, 1, 'STU-CARLOS01', 'Carlos Perez Lopez', true),
(2, 1, 'STU-SOFIA02', 'Sofía Perez Lopez', true),
(3, 2, 'STU-LUCIA03', 'Lucía Lopez Lopez', true);

SELECT setval('erp_student_ref_id_seq', 3);

-- 3. Insert initial debt account statuses
INSERT INTO erp_account_status (id, family_id, period_code, expected_amount, paid_amount, debt_amount, status, due_date) VALUES
(1, 1, '2026-05', 1200.00, 0.00, 1200.00, 'PENDING', '2026-05-10'),
(2, 1, '2026-04', 1200.00, 1200.00, 0.00, 'PAID', '2026-04-10'),
(3, 2, '2026-05', 1500.00, 0.00, 1500.00, 'PENDING', '2026-05-10');

SELECT setval('erp_account_status_id_seq', 3);

-- 4. Insert some initial daily collection facts for BI
INSERT INTO bi_collection_fact_daily (id, business_date, payment_method, amount, external_payment_id) VALUES
(1, '2026-04-09', 'CREDIT_CARD', 1200.00, 'PAY-TX-99001');

SELECT setval('bi_collection_fact_daily_id_seq', 1);

-- 5. Insert some initial delinquency snapshots for BI
INSERT INTO bi_delinquency_snapshot (id, snapshot_date, families_in_arrears, total_debt) VALUES
(1, '2026-05-15', 2, 2700.00);

SELECT setval('bi_delinquency_snapshot_id_seq', 1);
