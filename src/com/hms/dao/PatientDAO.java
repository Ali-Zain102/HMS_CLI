package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.BloodGroup;
import com.hms.enums.Gender;
import com.hms.enums.PatientStatus;
import com.hms.exception.HospitalException;
import com.hms.model.concrete.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Patient Data Access Object.
 * Implements GenericDAO for Patient CRUD + stored procedure calls.
 */
public class PatientDAO implements GenericDAO<Patient, Integer> {

    private final Connection conn;

    public PatientDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    // ── CRUD via GenericDAO ───────────────────────────────────

    @Override
    public Integer insert(Patient p) throws HospitalException {
        // Uses stored procedure sp_RegisterPatient
        String sql = "{CALL sp_RegisterPatient(?,?,?,?,?,?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, p.getFirstName());
            cs.setString(2, p.getLastName());
            cs.setString(3, p.getGender().name());
            cs.setDate(4, Date.valueOf(p.getDateOfBirth()));
            cs.setString(5, p.getBloodGroup() != null ? p.getBloodGroup().name() : null);
            cs.setString(6, p.getPhone());
            cs.setString(7, p.getEmail());
            cs.setString(8, p.getAddress() != null ? p.getAddress().getFullAddress() : null);
            cs.setString(9, p.getEmergencyContactName());
            cs.setString(10, p.getEmergencyContactPhone());
            cs.registerOutParameter(11, Types.INTEGER);
            cs.registerOutParameter(12, Types.VARCHAR);
            cs.execute();

            int patientId = cs.getInt(11);
            String message = cs.getString(12);

            if (patientId == -1) {
                throw new HospitalException(message);
            }
            return patientId;
        } catch (SQLException e) {
            throw new HospitalException("Failed to register patient: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Patient p) throws HospitalException {
        String sql = "UPDATE patients SET first_name=?, last_name=?, gender=?, "
                   + "date_of_birth=?, blood_group=?, phone=?, email=?, address=?, "
                   + "emergency_contact_name=?, emergency_contact_phone=? "
                   + "WHERE patient_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setString(3, p.getGender().name());
            ps.setDate(4, Date.valueOf(p.getDateOfBirth()));
            ps.setString(5, p.getBloodGroup() != null ? p.getBloodGroup().name() : null);
            ps.setString(6, p.getPhone());
            ps.setString(7, p.getEmail());
            ps.setString(8, p.getAddress() != null ? p.getAddress().getFullAddress() : null);
            ps.setString(9, p.getEmergencyContactName());
            ps.setString(10, p.getEmergencyContactPhone());
            ps.setInt(11, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update patient: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete patient: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Patient> findById(Integer id) throws HospitalException {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find patient: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> findAll() throws HospitalException {
        String sql = "SELECT * FROM patients ORDER BY patient_id";
        List<Patient> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new HospitalException("Failed to list patients: " + e.getMessage(), e);
        }
        return list;
    }

    // ── Additional queries ────────────────────────────────────

    public List<Patient> findByStatus(PatientStatus status) throws HospitalException {
        String sql = "SELECT * FROM patients WHERE status = ? ORDER BY patient_id";
        List<Patient> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new HospitalException("Failed to find patients by status: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Patient> searchByName(String name) throws HospitalException {
        String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ?";
        List<Patient> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + name + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new HospitalException("Search failed: " + e.getMessage(), e);
        }
        return list;
    }

    // ── Stored Procedure Calls ────────────────────────────────

    /**
     * Admit a patient via sp_AdmitPatient.
     * @return result message from stored procedure
     */
    public String admitPatient(int patientId, int bedId, int doctorId) throws HospitalException {
        String sql = "{CALL sp_AdmitPatient(?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, patientId);
            cs.setInt(2, bedId);
            cs.setInt(3, doctorId);
            cs.registerOutParameter(4, Types.INTEGER);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.execute();

            int billId = cs.getInt(4);
            String message = cs.getString(5);
            if (billId == -1) {
                throw new HospitalException(message);
            }
            return message;
        } catch (SQLException e) {
            throw new HospitalException("Admission failed: " + e.getMessage(), e);
        }
    }

    /**
     * Discharge a patient via sp_DischargePatient.
     * @return result message from stored procedure
     */
    public String dischargePatient(int patientId, double discount) throws HospitalException {
        String sql = "{CALL sp_DischargePatient(?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, patientId);
            cs.setDouble(2, discount);
            cs.registerOutParameter(3, Types.DECIMAL);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.execute();

            double totalBill = cs.getDouble(3);
            String message = cs.getString(4);
            if (totalBill == -1) {
                throw new HospitalException(message);
            }
            return message;
        } catch (SQLException e) {
            throw new HospitalException("Discharge failed: " + e.getMessage(), e);
        }
    }

    // ── Row Mapper ────────────────────────────────────────────

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("patient_id"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setGender(Gender.fromString(rs.getString("gender")));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) p.setDateOfBirth(dob.toLocalDate());

        String bg = rs.getString("blood_group");
        if (bg != null && !bg.isEmpty()) {
            try { p.setBloodGroup(BloodGroup.valueOf(bg)); }
            catch (IllegalArgumentException ignored) {}
        }

        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        p.setEmergencyContactName(rs.getString("emergency_contact_name"));
        p.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));

        String status = rs.getString("status");
        if (status != null) {
            p.setStatus(PatientStatus.valueOf(status));
        }

        p.setAssignedDoctorId(rs.getInt("assigned_doctor_id"));
        return p;
    }
}
