-- ============================================================
-- Hospital Management System - Database Schema
-- 01_schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS hms_cli_db;
USE hms_cli_db;

-- ============================================================
-- DEPARTMENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS departments (
    department_id   INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    location        VARCHAR(100),
    phone           VARCHAR(20),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- DOCTORS
-- ============================================================
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id       INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    gender          ENUM('MALE','FEMALE','OTHER') NOT NULL,
    date_of_birth   DATE         NOT NULL,
    phone           VARCHAR(20)  NOT NULL UNIQUE,
    email           VARCHAR(100) UNIQUE,
    specialization  VARCHAR(100) NOT NULL,
    department_id   INT,
    salary          DECIMAL(10,2) DEFAULT 0.00,
    hire_date       DATE         NOT NULL,
    is_active       BOOLEAN      DEFAULT TRUE,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_doctor_dept FOREIGN KEY (department_id)
        REFERENCES departments(department_id) ON DELETE SET NULL
);

-- ============================================================
-- NURSES
-- ============================================================
CREATE TABLE IF NOT EXISTS nurses (
    nurse_id        INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    gender          ENUM('MALE','FEMALE','OTHER') NOT NULL,
    date_of_birth   DATE         NOT NULL,
    phone           VARCHAR(20)  NOT NULL UNIQUE,
    email           VARCHAR(100) UNIQUE,
    department_id   INT,
    ward            VARCHAR(50),
    salary          DECIMAL(10,2) DEFAULT 0.00,
    hire_date       DATE         NOT NULL,
    is_active       BOOLEAN      DEFAULT TRUE,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_nurse_dept FOREIGN KEY (department_id)
        REFERENCES departments(department_id) ON DELETE SET NULL
);

-- ============================================================
-- PATIENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS patients (
    patient_id      INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    gender          ENUM('MALE','FEMALE','OTHER') NOT NULL,
    date_of_birth   DATE         NOT NULL,
    blood_group     ENUM('A_POS','A_NEG','B_POS','B_NEG',
                         'AB_POS','AB_NEG','O_POS','O_NEG'),
    phone           VARCHAR(20)  NOT NULL,
    email           VARCHAR(100),
    address         VARCHAR(255),
    emergency_contact_name  VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    status          ENUM('REGISTERED','ADMITTED','DISCHARGED') DEFAULT 'REGISTERED',
    assigned_doctor_id INT,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_doctor FOREIGN KEY (assigned_doctor_id)
        REFERENCES doctors(doctor_id) ON DELETE SET NULL
);

-- ============================================================
-- BEDS
-- ============================================================
CREATE TABLE IF NOT EXISTS beds (
    bed_id          INT AUTO_INCREMENT PRIMARY KEY,
    bed_number      VARCHAR(20)  NOT NULL UNIQUE,
    ward            VARCHAR(50)  NOT NULL,
    bed_type        ENUM('GENERAL','PRIVATE','ICU','EMERGENCY') DEFAULT 'GENERAL',
    status          ENUM('AVAILABLE','OCCUPIED','MAINTENANCE') DEFAULT 'AVAILABLE',
    daily_rate      DECIMAL(8,2) NOT NULL DEFAULT 500.00,
    patient_id      INT          NULL,
    assigned_at     DATETIME     NULL,
    CONSTRAINT fk_bed_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE SET NULL
);

-- ============================================================
-- APPOINTMENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id      INT AUTO_INCREMENT PRIMARY KEY,
    patient_id          INT          NOT NULL,
    doctor_id           INT          NOT NULL,
    appointment_date    DATE         NOT NULL,
    appointment_time    TIME         NOT NULL,
    reason              VARCHAR(255),
    status              ENUM('SCHEDULED','COMPLETED','CANCELLED','NO_SHOW')
                            DEFAULT 'SCHEDULED',
    notes               TEXT,
    created_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_appt_doctor  FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)  ON DELETE CASCADE
);

-- ============================================================
-- MEDICAL RECORDS
-- ============================================================
CREATE TABLE IF NOT EXISTS medical_records (
    record_id       INT AUTO_INCREMENT PRIMARY KEY,
    patient_id      INT          NOT NULL,
    doctor_id       INT          NOT NULL,
    diagnosis       TEXT         NOT NULL,
    treatment       TEXT,
    notes           TEXT,
    record_date     DATE         NOT NULL DEFAULT (CURDATE()),
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mr_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_doctor  FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)  ON DELETE CASCADE
);

-- ============================================================
-- MEDICINES
-- ============================================================
CREATE TABLE IF NOT EXISTS medicines (
    medicine_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    category        VARCHAR(50),
    unit_price      DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    stock_quantity  INT          NOT NULL DEFAULT 0,
    reorder_level   INT          NOT NULL DEFAULT 10,
    expiry_date     DATE,
    manufacturer    VARCHAR(100),
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- PRESCRIPTIONS
-- ============================================================
CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id       INT          NOT NULL,
    medicine_id     INT          NOT NULL,
    dosage          VARCHAR(100) NOT NULL,
    frequency       VARCHAR(100),
    duration_days   INT,
    quantity        INT          NOT NULL DEFAULT 1,
    CONSTRAINT fk_presc_record   FOREIGN KEY (record_id)
        REFERENCES medical_records(record_id) ON DELETE CASCADE,
    CONSTRAINT fk_presc_medicine FOREIGN KEY (medicine_id)
        REFERENCES medicines(medicine_id) ON DELETE RESTRICT
);

-- ============================================================
-- LAB REPORTS
-- ============================================================
CREATE TABLE IF NOT EXISTS lab_reports (
    report_id       INT AUTO_INCREMENT PRIMARY KEY,
    patient_id      INT          NOT NULL,
    doctor_id       INT          NOT NULL,
    test_name       VARCHAR(100) NOT NULL,
    test_date       DATE         NOT NULL DEFAULT (CURDATE()),
    result          TEXT,
    result_date     DATE,
    status          ENUM('REQUESTED','IN_PROGRESS','COMPLETED') DEFAULT 'REQUESTED',
    cost            DECIMAL(8,2) DEFAULT 0.00,
    notes           TEXT,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lab_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_doctor  FOREIGN KEY (doctor_id)
        REFERENCES doctors(doctor_id)  ON DELETE CASCADE
);

-- ============================================================
-- BILLS
-- ============================================================
CREATE TABLE IF NOT EXISTS bills (
    bill_id             INT AUTO_INCREMENT PRIMARY KEY,
    patient_id          INT          NOT NULL,
    bill_date           DATE         NOT NULL DEFAULT (CURDATE()),
    consultation_fee    DECIMAL(10,2) DEFAULT 0.00,
    bed_charges         DECIMAL(10,2) DEFAULT 0.00,
    medicine_charges    DECIMAL(10,2) DEFAULT 0.00,
    lab_charges         DECIMAL(10,2) DEFAULT 0.00,
    other_charges       DECIMAL(10,2) DEFAULT 0.00,
    discount            DECIMAL(10,2) DEFAULT 0.00,
    total_amount        DECIMAL(10,2) DEFAULT 0.00,
    status              ENUM('PENDING','PARTIAL','PAID') DEFAULT 'PENDING',
    notes               TEXT,
    created_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_patient FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id) ON DELETE CASCADE
);

-- ============================================================
-- PAYMENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    bill_id         INT          NOT NULL,
    amount_paid     DECIMAL(10,2) NOT NULL,
    payment_date    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method  ENUM('CASH','CARD','INSURANCE','ONLINE') DEFAULT 'CASH',
    reference_no    VARCHAR(100),
    notes           TEXT,
    CONSTRAINT fk_pay_bill FOREIGN KEY (bill_id)
        REFERENCES bills(bill_id) ON DELETE CASCADE
);

-- ============================================================
-- AUDIT LOG
-- ============================================================
CREATE TABLE IF NOT EXISTS audit_log (
    log_id          INT AUTO_INCREMENT PRIMARY KEY,
    table_name      VARCHAR(50)  NOT NULL,
    record_id       INT          NOT NULL,
    action          ENUM('INSERT','UPDATE','DELETE') NOT NULL,
    changed_by      VARCHAR(100) DEFAULT 'SYSTEM',
    changed_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    old_values      TEXT,
    new_values      TEXT
);

-- ============================================================
-- INDEXES for performance
-- ============================================================
CREATE INDEX idx_patient_status      ON patients(status);
CREATE INDEX idx_patient_name        ON patients(last_name, first_name);
CREATE INDEX idx_appointment_date    ON appointments(appointment_date);
CREATE INDEX idx_appointment_status  ON appointments(status);
CREATE INDEX idx_appointment_patient ON appointments(patient_id);
CREATE INDEX idx_appointment_doctor  ON appointments(doctor_id);
CREATE INDEX idx_bill_status         ON bills(status);
CREATE INDEX idx_bill_patient        ON bills(patient_id);
CREATE INDEX idx_lab_patient         ON lab_reports(patient_id);
CREATE INDEX idx_bed_status          ON beds(status);
CREATE INDEX idx_medicine_stock      ON medicines(stock_quantity);
