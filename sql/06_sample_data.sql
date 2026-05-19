-- ============================================================
-- Hospital Management System - Sample Data
-- 06_sample_data.sql
-- ============================================================

USE hms_cli_db;

-- ============================================================
-- DEPARTMENTS
-- ============================================================
INSERT INTO departments (name, location, phone) VALUES
('Cardiology',        'Block A, Floor 2', '091-1111-0001'),
('Orthopedics',       'Block B, Floor 1', '091-1111-0002'),
('Neurology',         'Block A, Floor 3', '091-1111-0003'),
('General Medicine',  'Block C, Floor 1', '091-1111-0004'),
('Pediatrics',        'Block D, Floor 1', '091-1111-0005'),
('Emergency',         'Block E, Ground',  '091-1111-0006'),
('Pharmacy',          'Block C, Ground',  '091-1111-0007'),
('Laboratory',        'Block B, Ground',  '091-1111-0008');

-- ============================================================
-- DOCTORS
-- ============================================================
INSERT INTO doctors
    (first_name, last_name, gender, date_of_birth, phone, email,
     specialization, department_id, salary, hire_date)
VALUES
('Ahmed',   'Khan',     'MALE',   '1978-03-15', '0300-1111001',
 'ahmed.khan@hms.pk',     'Cardiologist',   1, 250000.00, '2015-01-10'),
('Fatima',  'Siddiqui', 'FEMALE', '1982-07-22', '0300-1111002',
 'fatima.s@hms.pk',       'Orthopedic Surgeon', 2, 230000.00, '2016-03-01'),
('Tariq',   'Mehmood',  'MALE',   '1975-11-05', '0300-1111003',
 'tariq.m@hms.pk',        'Neurologist',    3, 240000.00, '2014-06-15'),
('Sara',    'Ali',      'FEMALE', '1985-09-18', '0300-1111004',
 'sara.ali@hms.pk',       'General Physician',4, 180000.00, '2018-08-20'),
('Hassan',  'Raza',     'MALE',   '1980-04-30', '0300-1111005',
 'hassan.r@hms.pk',       'Pediatrician',   5, 200000.00, '2017-02-14');

-- ============================================================
-- NURSES
-- ============================================================
INSERT INTO nurses
    (first_name, last_name, gender, date_of_birth, phone,
     department_id, ward, salary, hire_date)
VALUES
('Zainab',  'Bibi',    'FEMALE', '1995-06-12', '0301-2222001', 1, 'Cardiac Ward',  60000.00, '2020-01-15'),
('Alia',    'Noor',    'FEMALE', '1993-09-25', '0301-2222002', 2, 'Ortho Ward',    58000.00, '2019-06-01'),
('Rashid',  'Malik',   'MALE',   '1990-02-14', '0301-2222003', 6, 'Emergency',     65000.00, '2018-11-20'),
('Sana',    'Pervez',  'FEMALE', '1996-12-03', '0301-2222004', 5, 'Pediatric Ward',57000.00, '2021-03-10'),
('Imran',   'Sheikh',  'MALE',   '1988-07-19', '0301-2222005', 4, 'General Ward',  62000.00, '2017-09-05');

-- ============================================================
-- BEDS
-- ============================================================
INSERT INTO beds (bed_number, ward, bed_type, status, daily_rate) VALUES
('G-101', 'General Ward',  'GENERAL',   'AVAILABLE', 1500.00),
('G-102', 'General Ward',  'GENERAL',   'AVAILABLE', 1500.00),
('G-103', 'General Ward',  'GENERAL',   'AVAILABLE', 1500.00),
('P-201', 'Private Ward',  'PRIVATE',   'AVAILABLE', 3500.00),
('P-202', 'Private Ward',  'PRIVATE',   'AVAILABLE', 3500.00),
('I-301', 'ICU',           'ICU',       'AVAILABLE', 8000.00),
('I-302', 'ICU',           'ICU',       'AVAILABLE', 8000.00),
('E-001', 'Emergency',     'EMERGENCY', 'AVAILABLE', 2000.00),
('E-002', 'Emergency',     'EMERGENCY', 'AVAILABLE', 2000.00),
('C-401', 'Cardiac Ward',  'PRIVATE',   'AVAILABLE', 4000.00),
('O-501', 'Ortho Ward',    'GENERAL',   'AVAILABLE', 2000.00),
('K-601', 'Pediatric Ward','GENERAL',   'AVAILABLE', 1800.00);

-- ============================================================
-- PATIENTS
-- ============================================================
INSERT INTO patients
    (first_name, last_name, gender, date_of_birth, blood_group,
     phone, email, address, emergency_contact_name,
     emergency_contact_phone, status)
VALUES
('Muhammad', 'Usman',   'MALE',   '1990-05-20', 'B_POS',
 '0321-3333001', 'usman@gmail.com',  'Hayatabad, Peshawar',
 'Ali Usman',   '0321-3333010', 'REGISTERED'),

('Ayesha',   'Tariq',   'FEMALE', '1985-11-10', 'A_POS',
 '0321-3333002', 'ayesha@gmail.com', 'Gulbahar, Peshawar',
 'Tariq Ahmed', '0321-3333011', 'REGISTERED'),

('Bilal',    'Hussain', 'MALE',   '1972-03-28', 'O_NEG',
 '0321-3333003', NULL,              'University Town, Peshawar',
 'Amina Bibi',  '0321-3333012', 'REGISTERED'),

('Nadia',    'Shah',    'FEMALE', '2005-08-15', 'AB_POS',
 '0321-3333004', 'nadia@yahoo.com', 'Saddar, Peshawar',
 'Shah Zaman',  '0321-3333013', 'REGISTERED'),

('Kamran',   'Iqbal',   'MALE',   '1965-12-01', 'A_NEG',
 '0321-3333005', NULL,              'Dalazak Road, Peshawar',
 'Iqbal Khan',  '0321-3333014', 'REGISTERED');

-- ============================================================
-- MEDICINES
-- ============================================================
INSERT INTO medicines
    (name, category, unit_price, stock_quantity, reorder_level,
     expiry_date, manufacturer)
VALUES
('Paracetamol 500mg',  'Analgesic',     15.00,  500, 50,  '2026-12-31', 'GSK Pakistan'),
('Amoxicillin 250mg',  'Antibiotic',    45.00,  300, 30,  '2026-06-30', 'Abbott Labs'),
('Metformin 500mg',    'Antidiabetic',  20.00,  400, 40,  '2027-01-31', 'Getz Pharma'),
('Atorvastatin 20mg',  'Cardiac',       85.00,  200, 20,  '2026-09-30', 'Pfizer'),
('Omeprazole 20mg',    'Antacid',       30.00,  350, 35,  '2026-11-30', 'Ferozsons'),
('Aspirin 75mg',       'Analgesic',     12.00,  600, 60,  '2027-03-31', 'Reckitt'),
('Ciprofloxacin 500mg','Antibiotic',    55.00,  150, 25,  '2026-08-31', 'Searle'),
('Ibuprofen 400mg',    'NSAID',         25.00,  280, 30,  '2026-10-31', 'Highnoon Labs'),
('Diazepam 5mg',       'Sedative',      40.00,   80, 15,  '2026-07-31', 'Roche'),
('Insulin Glargine',   'Antidiabetic', 550.00,   60, 10,  '2025-12-31', 'Sanofi');

-- ============================================================
-- SAMPLE APPOINTMENTS
-- ============================================================
INSERT INTO appointments
    (patient_id, doctor_id, appointment_date, appointment_time, reason, status)
VALUES
(1, 4, CURDATE(),              '09:00:00', 'Routine checkup',       'SCHEDULED'),
(2, 1, CURDATE(),              '10:00:00', 'Chest pain evaluation',  'SCHEDULED'),
(3, 3, CURDATE(),              '11:00:00', 'Headache & dizziness',   'SCHEDULED'),
(4, 5, DATE_ADD(CURDATE(),INTERVAL 1 DAY),'09:30:00','Fever follow-up','SCHEDULED'),
(5, 1, DATE_ADD(CURDATE(),INTERVAL 1 DAY),'14:00:00','BP management', 'SCHEDULED');
