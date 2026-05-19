package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.LabStatus;
import com.hms.exception.HospitalException;
import com.hms.model.entity.LabReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Lab Report Data Access Object.
 */
public class LabReportDAO implements GenericDAO<LabReport, Integer> {

    private final Connection conn;

    public LabReportDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Integer insert(LabReport lr) throws HospitalException {
        String sql = "INSERT INTO lab_reports (patient_id, doctor_id, test_name, "
                   + "test_date, cost, notes) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, lr.getPatientId());
            ps.setInt(2, lr.getDoctorId());
            ps.setString(3, lr.getTestName());
            ps.setDate(4, Date.valueOf(lr.getTestDate()));
            ps.setDouble(5, lr.getCost());
            ps.setString(6, lr.getNotes());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new HospitalException("Failed to get generated report ID.");
        } catch (SQLException e) {
            throw new HospitalException("Failed to create lab report: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(LabReport lr) throws HospitalException {
        String sql = "UPDATE lab_reports SET result=?, result_date=?, status=?, "
                   + "cost=?, notes=? WHERE report_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lr.getResult());
            if (lr.getResultDate() != null) {
                ps.setDate(2, Date.valueOf(lr.getResultDate()));
            } else {
                ps.setNull(2, Types.DATE);
            }
            ps.setString(3, lr.getStatus().name());
            ps.setDouble(4, lr.getCost());
            ps.setString(5, lr.getNotes());
            ps.setInt(6, lr.getReportId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update lab report: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM lab_reports WHERE report_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete lab report: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<LabReport> findById(Integer id) throws HospitalException {
        String sql = "SELECT lr.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM lab_reports lr "
                   + "JOIN patients p ON lr.patient_id=p.patient_id "
                   + "WHERE lr.report_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find lab report: " + e.getMessage(), e);
        }
    }

    @Override
    public List<LabReport> findAll() throws HospitalException {
        String sql = "SELECT lr.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM lab_reports lr "
                   + "JOIN patients p ON lr.patient_id=p.patient_id "
                   + "ORDER BY lr.report_id DESC";
        List<LabReport> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list lab reports: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find lab reports by patient ID.
     */
    public List<LabReport> findByPatientId(int patientId) throws HospitalException {
        String sql = "SELECT lr.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM lab_reports lr "
                   + "JOIN patients p ON lr.patient_id=p.patient_id "
                   + "WHERE lr.patient_id=? ORDER BY lr.test_date DESC";
        List<LabReport> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to find lab reports: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find pending lab reports.
     */
    public List<LabReport> findPending() throws HospitalException {
        String sql = "SELECT lr.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM lab_reports lr "
                   + "JOIN patients p ON lr.patient_id=p.patient_id "
                   + "WHERE lr.status IN ('REQUESTED','IN_PROGRESS') ORDER BY lr.test_date";
        List<LabReport> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list pending reports: " + e.getMessage(), e);
        }
        return list;
    }

    private LabReport mapRow(ResultSet rs) throws SQLException {
        LabReport lr = new LabReport();
        lr.setReportId(rs.getInt("report_id"));
        lr.setPatientId(rs.getInt("patient_id"));
        lr.setDoctorId(rs.getInt("doctor_id"));
        try { lr.setPatientName(rs.getString("patient_name")); }
        catch (SQLException ignored) {}
        lr.setTestName(rs.getString("test_name"));
        Date td = rs.getDate("test_date");
        if (td != null) lr.setTestDate(td.toLocalDate());
        lr.setResult(rs.getString("result"));
        Date rd = rs.getDate("result_date");
        if (rd != null) lr.setResultDate(rd.toLocalDate());
        String st = rs.getString("status");
        if (st != null) lr.setStatus(LabStatus.valueOf(st));
        lr.setCost(rs.getDouble("cost"));
        lr.setNotes(rs.getString("notes"));
        return lr;
    }
}
