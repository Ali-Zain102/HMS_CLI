-- ============================================================
-- Hospital Management System - Views
-- 05_views.sql
-- ============================================================

USE hms_cli_db;

-- ============================================================
-- vw_DoctorScheduleToday
-- Today's scheduled appointments with doctor & patient names
-- ============================================================
CREATE OR REPLACE VIEW vw_DoctorScheduleToday AS
SELECT
    a.appointment_id,
    a.appointment_time,
    CONCAT(d.first_name, ' ', d.last_name)  AS doctor_name,
    d.specialization,
    CONCAT(p.first_name, ' ', p.last_name)  AS patient_name,
    p.phone                                  AS patient_phone,
    a.reason,
    a.status
FROM appointments a
JOIN doctors  d ON a.doctor_id  = d.doctor_id
JOIN patients p ON a.patient_id = p.patient_id
WHERE a.appointment_date = CURDATE()
ORDER BY a.appointment_time;

-- ============================================================
-- vw_PatientSummary
-- All patients with age, assigned doctor, bed, and bill status
-- ============================================================
CREATE OR REPLACE VIEW vw_PatientSummary AS
SELECT
    p.patient_id,
    CONCAT(p.first_name, ' ', p.last_name)          AS patient_name,
    p.gender,
    fn_CalculateAge(p.date_of_birth)                 AS age,
    p.blood_group,
    p.phone,
    p.status                                         AS patient_status,
    CONCAT(d.first_name, ' ', d.last_name)           AS assigned_doctor,
    b.bed_number,
    b.ward,
    fn_GetPatientBillStatus(p.patient_id)            AS bill_status
FROM patients p
LEFT JOIN doctors d ON p.assigned_doctor_id = d.doctor_id
LEFT JOIN beds    b ON b.patient_id = p.patient_id;

-- ============================================================
-- vw_AvailableBeds
-- All beds currently available for assignment
-- ============================================================
CREATE OR REPLACE VIEW vw_AvailableBeds AS
SELECT
    bed_id,
    bed_number,
    ward,
    bed_type,
    daily_rate
FROM beds
WHERE status = 'AVAILABLE'
ORDER BY ward, bed_number;

-- ============================================================
-- vw_PendingBills
-- All unpaid or partially paid bills with patient info
-- ============================================================
CREATE OR REPLACE VIEW vw_PendingBills AS
SELECT
    b.bill_id,
    CONCAT(p.first_name, ' ', p.last_name)  AS patient_name,
    p.phone                                  AS patient_phone,
    b.bill_date,
    b.total_amount,
    COALESCE((SELECT SUM(py.amount_paid) FROM payments py
              WHERE py.bill_id = b.bill_id), 0)     AS amount_paid,
    b.total_amount - COALESCE((SELECT SUM(py.amount_paid) FROM payments py
              WHERE py.bill_id = b.bill_id), 0)     AS amount_due,
    b.status
FROM bills b
JOIN patients p ON b.patient_id = p.patient_id
WHERE b.status IN ('PENDING','PARTIAL')
ORDER BY b.bill_date;

-- ============================================================
-- vw_DoctorList
-- Active doctors with department info
-- ============================================================
CREATE OR REPLACE VIEW vw_DoctorList AS
SELECT
    d.doctor_id,
    CONCAT(d.first_name, ' ', d.last_name)  AS doctor_name,
    d.specialization,
    d.phone,
    d.email,
    dept.name                               AS department,
    fn_GetAppointmentCount(d.doctor_id, CURDATE()) AS appointments_today
FROM doctors d
LEFT JOIN departments dept ON d.department_id = dept.department_id
WHERE d.is_active = TRUE
ORDER BY d.last_name;

-- ============================================================
-- vw_BedOccupancy
-- Summary of bed usage per ward
-- ============================================================
CREATE OR REPLACE VIEW vw_BedOccupancy AS
SELECT
    ward,
    COUNT(*)                                    AS total_beds,
    SUM(CASE WHEN status = 'AVAILABLE'   THEN 1 ELSE 0 END) AS available,
    SUM(CASE WHEN status = 'OCCUPIED'    THEN 1 ELSE 0 END) AS occupied,
    SUM(CASE WHEN status = 'MAINTENANCE' THEN 1 ELSE 0 END) AS maintenance,
    ROUND(SUM(CASE WHEN status = 'OCCUPIED' THEN 1 ELSE 0 END)
          / COUNT(*) * 100, 1)                  AS occupancy_pct
FROM beds
GROUP BY ward
ORDER BY ward;

-- ============================================================
-- vw_LowStockMedicines
-- Medicines at or below reorder level
-- ============================================================
CREATE OR REPLACE VIEW vw_LowStockMedicines AS
SELECT
    medicine_id,
    name,
    category,
    stock_quantity,
    reorder_level,
    unit_price,
    expiry_date
FROM medicines
WHERE stock_quantity <= reorder_level
ORDER BY stock_quantity ASC;
