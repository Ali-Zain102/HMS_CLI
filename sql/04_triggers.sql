-- ============================================================
-- Hospital Management System - Triggers
-- 04_triggers.sql
-- ============================================================

USE hms_cli_db;

DELIMITER $$

-- ============================================================
-- trg_UpdateBedOnAdmit
-- AFTER UPDATE on patients: when status → ADMITTED,
-- the bed update is handled by sp_AdmitPatient.
-- This trigger logs the admission to audit_log.
-- ============================================================
DROP TRIGGER IF EXISTS trg_AuditPatientAdmit $$
CREATE TRIGGER trg_AuditPatientAdmit
AFTER UPDATE ON patients
FOR EACH ROW
BEGIN
    IF NEW.status = 'ADMITTED' AND OLD.status != 'ADMITTED' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, new_values)
        VALUES (
            'patients',
            NEW.patient_id,
            'UPDATE',
            CONCAT('status=', OLD.status),
            CONCAT('status=', NEW.status, ', doctor_id=', COALESCE(NEW.assigned_doctor_id, 'NULL'))
        );
    END IF;
END $$

-- ============================================================
-- trg_FreeBedOnDischarge
-- AFTER UPDATE on patients: when status → DISCHARGED,
-- logs discharge to audit_log.
-- ============================================================
DROP TRIGGER IF EXISTS trg_AuditPatientDischarge $$
CREATE TRIGGER trg_AuditPatientDischarge
AFTER UPDATE ON patients
FOR EACH ROW
BEGIN
    IF NEW.status = 'DISCHARGED' AND OLD.status = 'ADMITTED' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, new_values)
        VALUES (
            'patients',
            NEW.patient_id,
            'UPDATE',
            CONCAT('status=', OLD.status),
            CONCAT('status=', NEW.status)
        );
    END IF;
END $$

-- ============================================================
-- trg_AutoGenerateBill
-- AFTER INSERT on lab_reports: updates bill with lab cost
-- ============================================================
DROP TRIGGER IF EXISTS trg_AutoAddLabToBill $$
CREATE TRIGGER trg_AutoAddLabToBill
AFTER UPDATE ON lab_reports
FOR EACH ROW
BEGIN
    DECLARE v_bill_id INT;

    IF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
        SELECT bill_id INTO v_bill_id
        FROM bills
        WHERE patient_id = NEW.patient_id AND status IN ('PENDING', 'PARTIAL')
        ORDER BY created_at DESC
        LIMIT 1;

        IF v_bill_id IS NOT NULL THEN
            UPDATE bills
            SET lab_charges = lab_charges + NEW.cost,
                total_amount = consultation_fee + bed_charges +
                               medicine_charges + (lab_charges + NEW.cost) +
                               other_charges - discount
            WHERE bill_id = v_bill_id;
        END IF;

        INSERT INTO audit_log (table_name, record_id, action, new_values)
        VALUES ('lab_reports', NEW.report_id, 'UPDATE',
                CONCAT('status=COMPLETED, cost=', NEW.cost));
    END IF;
END $$

-- ============================================================
-- trg_AuditPatientRecord
-- BEFORE DELETE on patients: logs deletion attempt
-- ============================================================
DROP TRIGGER IF EXISTS trg_AuditPatientDelete $$
CREATE TRIGGER trg_AuditPatientDelete
BEFORE DELETE ON patients
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (table_name, record_id, action, old_values)
    VALUES (
        'patients',
        OLD.patient_id,
        'DELETE',
        CONCAT('name=', OLD.first_name, ' ', OLD.last_name,
               ', status=', OLD.status)
    );
END $$

-- ============================================================
-- trg_LowStockAlert
-- AFTER UPDATE on medicines: logs when stock hits reorder level
-- ============================================================
DROP TRIGGER IF EXISTS trg_LowStockAlert $$
CREATE TRIGGER trg_LowStockAlert
AFTER UPDATE ON medicines
FOR EACH ROW
BEGIN
    IF NEW.stock_quantity <= NEW.reorder_level
       AND OLD.stock_quantity > OLD.reorder_level THEN
        INSERT INTO audit_log (table_name, record_id, action, new_values)
        VALUES (
            'medicines',
            NEW.medicine_id,
            'UPDATE',
            CONCAT('LOW_STOCK_ALERT: ', NEW.name,
                   ' stock=', NEW.stock_quantity,
                   ' reorder_level=', NEW.reorder_level)
        );
    END IF;
END $$

-- ============================================================
-- trg_DeductMedicineStock
-- AFTER INSERT on prescriptions: deducts stock
-- ============================================================
DROP TRIGGER IF EXISTS trg_DeductMedicineStock $$
CREATE TRIGGER trg_DeductMedicineStock
AFTER INSERT ON prescriptions
FOR EACH ROW
BEGIN
    UPDATE medicines
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE medicine_id = NEW.medicine_id;
END $$

DELIMITER ;
