-- ============================================================
-- Hospital Management System - Stored Procedures
-- 02_stored_procedures.sql
-- ============================================================

USE hms_cli_db;

DELIMITER $$

-- ============================================================
-- sp_RegisterPatient
-- Registers a new patient with error handling
-- ============================================================
DROP PROCEDURE IF EXISTS sp_RegisterPatient $$
CREATE PROCEDURE sp_RegisterPatient(
    IN  p_first_name            VARCHAR(50),
    IN  p_last_name             VARCHAR(50),
    IN  p_gender                VARCHAR(10),
    IN  p_dob                   DATE,
    IN  p_blood_group           VARCHAR(10),
    IN  p_phone                 VARCHAR(20),
    IN  p_email                 VARCHAR(100),
    IN  p_address               VARCHAR(255),
    IN  p_emergency_name        VARCHAR(100),
    IN  p_emergency_phone       VARCHAR(20),
    OUT p_patient_id            INT,
    OUT p_message               VARCHAR(255)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_patient_id = -1;
        SET p_message = 'Error: Failed to register patient.';
    END;

    START TRANSACTION;

    -- Validate phone not duplicate
    IF EXISTS (SELECT 1 FROM patients WHERE phone = p_phone) THEN
        SET p_patient_id = -1;
        SET p_message = 'Error: Phone number already registered.';
        ROLLBACK;
    ELSE
        INSERT INTO patients (
            first_name, last_name, gender, date_of_birth,
            blood_group, phone, email, address,
            emergency_contact_name, emergency_contact_phone, status
        ) VALUES (
            p_first_name, p_last_name, p_gender, p_dob,
            p_blood_group, p_phone, p_email, p_address,
            p_emergency_name, p_emergency_phone, 'REGISTERED'
        );

        SET p_patient_id = LAST_INSERT_ID();
        SET p_message = CONCAT('Patient registered successfully. ID: ', p_patient_id);
        COMMIT;
    END IF;
END $$

-- ============================================================
-- sp_AdmitPatient
-- Admits a patient: assigns bed, updates status, creates bill
-- ============================================================
DROP PROCEDURE IF EXISTS sp_AdmitPatient $$
CREATE PROCEDURE sp_AdmitPatient(
    IN  p_patient_id    INT,
    IN  p_bed_id        INT,
    IN  p_doctor_id     INT,
    OUT p_bill_id       INT,
    OUT p_message       VARCHAR(255)
)
BEGIN
    DECLARE v_bed_status    VARCHAR(20);
    DECLARE v_patient_status VARCHAR(20);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_bill_id = -1;
        SET p_message = 'Error: Transaction failed during admission.';
    END;

    START TRANSACTION;

    -- Check patient exists and status
    SELECT status INTO v_patient_status
    FROM patients WHERE patient_id = p_patient_id FOR UPDATE;

    IF v_patient_status IS NULL THEN
        SET p_bill_id = -1;
        SET p_message = 'Error: Patient not found.';
        ROLLBACK;
    ELSEIF v_patient_status = 'ADMITTED' THEN
        SET p_bill_id = -1;
        SET p_message = 'Error: Patient is already admitted.';
        ROLLBACK;
    ELSE
        -- Check bed availability
        SELECT status INTO v_bed_status
        FROM beds WHERE bed_id = p_bed_id FOR UPDATE;

        IF v_bed_status != 'AVAILABLE' THEN
            SET p_bill_id = -1;
            SET p_message = 'Error: Bed is not available.';
            ROLLBACK;
        ELSE
            -- Assign bed
            UPDATE beds
            SET status = 'OCCUPIED', patient_id = p_patient_id, assigned_at = NOW()
            WHERE bed_id = p_bed_id;

            -- Update patient status and doctor
            UPDATE patients
            SET status = 'ADMITTED', assigned_doctor_id = p_doctor_id
            WHERE patient_id = p_patient_id;

            -- Generate initial bill
            INSERT INTO bills (patient_id, bill_date, status)
            VALUES (p_patient_id, CURDATE(), 'PENDING');

            SET p_bill_id = LAST_INSERT_ID();
            SET p_message = CONCAT('Patient admitted. Bed assigned. Bill ID: ', p_bill_id);
            COMMIT;
        END IF;
    END IF;
END $$

-- ============================================================
-- sp_DischargePatient
-- Discharges a patient: frees bed, finalises bill
-- ============================================================
DROP PROCEDURE IF EXISTS sp_DischargePatient $$
CREATE PROCEDURE sp_DischargePatient(
    IN  p_patient_id    INT,
    IN  p_discount      DECIMAL(10,2),
    OUT p_total_bill    DECIMAL(10,2),
    OUT p_message       VARCHAR(255)
)
BEGIN
    DECLARE v_bill_id       INT;
    DECLARE v_bed_id        INT;
    DECLARE v_assigned_at   DATETIME;
    DECLARE v_daily_rate    DECIMAL(8,2);
    DECLARE v_days          INT;
    DECLARE v_bed_charges   DECIMAL(10,2);
    DECLARE v_med_charges   DECIMAL(10,2) DEFAULT 0;
    DECLARE v_lab_charges   DECIMAL(10,2) DEFAULT 0;
    DECLARE v_consult_fee   DECIMAL(10,2) DEFAULT 500.00;
    DECLARE v_total         DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_total_bill = -1;
        SET p_message = 'Error: Discharge failed.';
    END;

    START TRANSACTION;

    -- Get bed info
    SELECT bed_id, assigned_at, daily_rate
    INTO v_bed_id, v_assigned_at, v_daily_rate
    FROM beds WHERE patient_id = p_patient_id FOR UPDATE;

    -- Calculate bed days (minimum 1)
    SET v_days = GREATEST(1, DATEDIFF(NOW(), v_assigned_at));
    SET v_bed_charges = v_days * v_daily_rate;

    -- Get lab charges
    SELECT COALESCE(SUM(cost), 0) INTO v_lab_charges
    FROM lab_reports WHERE patient_id = p_patient_id AND status = 'COMPLETED';

    -- Get medicine charges from prescriptions
    SELECT COALESCE(SUM(p.quantity * m.unit_price), 0)
    INTO v_med_charges
    FROM prescriptions p
    JOIN medical_records mr ON p.record_id = mr.record_id
    JOIN medicines m ON p.medicine_id = m.medicine_id
    WHERE mr.patient_id = p_patient_id;

    SET v_total = v_consult_fee + v_bed_charges + v_med_charges + v_lab_charges - p_discount;
    SET p_total_bill = GREATEST(0, v_total);

    -- Update bill
    SELECT bill_id INTO v_bill_id
    FROM bills WHERE patient_id = p_patient_id AND status = 'PENDING'
    ORDER BY created_at DESC LIMIT 1;

    UPDATE bills
    SET consultation_fee  = v_consult_fee,
        bed_charges       = v_bed_charges,
        medicine_charges  = v_med_charges,
        lab_charges       = v_lab_charges,
        discount          = p_discount,
        total_amount      = p_total_bill,
        status            = 'PENDING'
    WHERE bill_id = v_bill_id;

    -- Free bed
    UPDATE beds
    SET status = 'AVAILABLE', patient_id = NULL, assigned_at = NULL
    WHERE bed_id = v_bed_id;

    -- Update patient status
    UPDATE patients
    SET status = 'DISCHARGED'
    WHERE patient_id = p_patient_id;

    SET p_message = CONCAT('Patient discharged. Total Bill: PKR ', p_total_bill);
    COMMIT;
END $$

-- ============================================================
-- sp_BookAppointment
-- Books appointment with time conflict check
-- ============================================================
DROP PROCEDURE IF EXISTS sp_BookAppointment $$
CREATE PROCEDURE sp_BookAppointment(
    IN  p_patient_id    INT,
    IN  p_doctor_id     INT,
    IN  p_date          DATE,
    IN  p_time          TIME,
    IN  p_reason        VARCHAR(255),
    OUT p_appt_id       INT,
    OUT p_message       VARCHAR(255)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_appt_id = -1;
        SET p_message = 'Error: Failed to book appointment.';
    END;

    START TRANSACTION;

    -- Check for time conflict (30-minute slots)
    IF EXISTS (
        SELECT 1 FROM appointments
        WHERE doctor_id = p_doctor_id
          AND appointment_date = p_date
          AND ABS(TIMESTAMPDIFF(MINUTE,
                CONCAT(p_date, ' ', p_time),
                CONCAT(appointment_date, ' ', appointment_time))) < 30
          AND status = 'SCHEDULED'
    ) THEN
        SET p_appt_id = -1;
        SET p_message = 'Error: Doctor has a conflicting appointment at that time.';
        ROLLBACK;
    ELSE
        INSERT INTO appointments
            (patient_id, doctor_id, appointment_date, appointment_time, reason, status)
        VALUES
            (p_patient_id, p_doctor_id, p_date, p_time, p_reason, 'SCHEDULED');

        SET p_appt_id = LAST_INSERT_ID();
        SET p_message = CONCAT('Appointment booked. ID: ', p_appt_id);
        COMMIT;
    END IF;
END $$

-- ============================================================
-- sp_ProcessPayment
-- Records payment and updates bill status
-- ============================================================
DROP PROCEDURE IF EXISTS sp_ProcessPayment $$
CREATE PROCEDURE sp_ProcessPayment(
    IN  p_bill_id       INT,
    IN  p_amount        DECIMAL(10,2),
    IN  p_method        VARCHAR(20),
    IN  p_reference     VARCHAR(100),
    OUT p_payment_id    INT,
    OUT p_message       VARCHAR(255)
)
BEGIN
    DECLARE v_total     DECIMAL(10,2);
    DECLARE v_paid      DECIMAL(10,2);
    DECLARE v_remaining DECIMAL(10,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_payment_id = -1;
        SET p_message = 'Error: Payment processing failed.';
    END;

    START TRANSACTION;

    SELECT total_amount INTO v_total
    FROM bills WHERE bill_id = p_bill_id FOR UPDATE;

    IF v_total IS NULL THEN
        SET p_payment_id = -1;
        SET p_message = 'Error: Bill not found.';
        ROLLBACK;
    ELSE
        SELECT COALESCE(SUM(amount_paid), 0) INTO v_paid
        FROM payments WHERE bill_id = p_bill_id;

        SET v_remaining = v_total - v_paid;

        IF p_amount <= 0 THEN
            SET p_payment_id = -1;
            SET p_message = 'Error: Payment amount must be positive.';
            ROLLBACK;
        ELSEIF p_amount > v_remaining THEN
            SET p_payment_id = -1;
            SET p_message = CONCAT('Error: Amount exceeds remaining balance of PKR ', v_remaining);
            ROLLBACK;
        ELSE
            INSERT INTO payments (bill_id, amount_paid, payment_method, reference_no)
            VALUES (p_bill_id, p_amount, p_method, p_reference);

            SET p_payment_id = LAST_INSERT_ID();

            -- Update bill status
            IF (v_paid + p_amount) >= v_total THEN
                UPDATE bills SET status = 'PAID' WHERE bill_id = p_bill_id;
                SET p_message = 'Payment complete. Bill fully paid.';
            ELSE
                UPDATE bills SET status = 'PARTIAL' WHERE bill_id = p_bill_id;
                SET p_message = CONCAT('Partial payment recorded. Remaining: PKR ',
                                       (v_total - v_paid - p_amount));
            END IF;
            COMMIT;
        END IF;
    END IF;
END $$

DELIMITER ;
