-- ============================================================
-- Hospital Management System - Functions
-- 03_functions.sql
-- ============================================================

USE hms_cli_db;

DELIMITER $$

-- ============================================================
-- fn_CalculateAge
-- Returns age in years from a date of birth
-- ============================================================
DROP FUNCTION IF EXISTS fn_CalculateAge $$
CREATE FUNCTION fn_CalculateAge(p_dob DATE)
RETURNS INT
DETERMINISTIC
BEGIN
    RETURN TIMESTAMPDIFF(YEAR, p_dob, CURDATE());
END $$

-- ============================================================
-- fn_CalculateTotalBill
-- Returns total outstanding bill for a patient
-- ============================================================
DROP FUNCTION IF EXISTS fn_CalculateTotalBill $$
CREATE FUNCTION fn_CalculateTotalBill(p_patient_id INT)
RETURNS DECIMAL(10,2)
READS SQL DATA
BEGIN
    DECLARE v_total     DECIMAL(10,2) DEFAULT 0.00;
    DECLARE v_paid      DECIMAL(10,2) DEFAULT 0.00;

    SELECT COALESCE(SUM(total_amount), 0)
    INTO v_total
    FROM bills
    WHERE patient_id = p_patient_id AND status != 'PAID';

    SELECT COALESCE(SUM(p.amount_paid), 0)
    INTO v_paid
    FROM payments p
    JOIN bills b ON p.bill_id = b.bill_id
    WHERE b.patient_id = p_patient_id AND b.status != 'PAID';

    RETURN GREATEST(0, v_total - v_paid);
END $$

-- ============================================================
-- fn_GetDoctorSchedule (Table-valued simulation via VIEW)
-- Returns count of today's appointments for a doctor
-- ============================================================
DROP FUNCTION IF EXISTS fn_GetAppointmentCount $$
CREATE FUNCTION fn_GetAppointmentCount(p_doctor_id INT, p_date DATE)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count
    FROM appointments
    WHERE doctor_id = p_doctor_id
      AND appointment_date = p_date
      AND status = 'SCHEDULED';
    RETURN v_count;
END $$

-- ============================================================
-- fn_IsBedAvailable
-- Returns 1 if bed is available, 0 otherwise
-- ============================================================
DROP FUNCTION IF EXISTS fn_IsBedAvailable $$
CREATE FUNCTION fn_IsBedAvailable(p_bed_id INT)
RETURNS BOOLEAN
READS SQL DATA
BEGIN
    DECLARE v_status VARCHAR(20);
    SELECT status INTO v_status FROM beds WHERE bed_id = p_bed_id;
    RETURN (v_status = 'AVAILABLE');
END $$

-- ============================================================
-- fn_GetPatientBillStatus
-- Returns readable bill summary
-- ============================================================
DROP FUNCTION IF EXISTS fn_GetPatientBillStatus $$
CREATE FUNCTION fn_GetPatientBillStatus(p_patient_id INT)
RETURNS VARCHAR(100)
READS SQL DATA
BEGIN
    DECLARE v_pending   DECIMAL(10,2);
    DECLARE v_result    VARCHAR(100);

    SET v_pending = fn_CalculateTotalBill(p_patient_id);

    IF v_pending = 0 THEN
        SET v_result = 'All bills paid';
    ELSE
        SET v_result = CONCAT('Outstanding: PKR ', FORMAT(v_pending, 2));
    END IF;

    RETURN v_result;
END $$

DELIMITER ;
