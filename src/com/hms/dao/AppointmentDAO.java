package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.AppointmentStatus;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Appointment Data Access Object.
 * Uses stored procedure sp_BookAppointment for conflict-checked booking.
 */
public class AppointmentDAO implements GenericDAO<Appointment, Integer> {

    private final Connection conn;

    public AppointmentDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Insert via stored procedure sp_BookAppointment.
     */
    @Override
    public Integer insert(Appointment a) throws HospitalException {
        String sql = "{CALL sp_BookAppointment(?,?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, a.getPatientId());
            cs.setInt(2, a.getDoctorId());
            cs.setDate(3, Date.valueOf(a.getAppointmentDate()));
            cs.setTime(4, Time.valueOf(a.getAppointmentTime()));
            cs.setString(5, a.getReason());
            cs.registerOutParameter(6, Types.INTEGER);
            cs.registerOutParameter(7, Types.VARCHAR);
            cs.execute();

            int apptId = cs.getInt(6);
            String message = cs.getString(7);
            if (apptId == -1) {
                throw new HospitalException(message);
            }
            return apptId;
        } catch (SQLException e) {
            throw new HospitalException("Failed to book appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Appointment a) throws HospitalException {
        String sql = "UPDATE appointments SET appointment_date=?, appointment_time=?, "
                   + "reason=?, status=?, notes=? WHERE appointment_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(a.getAppointmentDate()));
            ps.setTime(2, Time.valueOf(a.getAppointmentTime()));
            ps.setString(3, a.getReason());
            ps.setString(4, a.getStatus().name());
            ps.setString(5, a.getNotes());
            ps.setInt(6, a.getAppointmentId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Appointment> findById(Integer id) throws HospitalException {
        String sql = "SELECT a.*, "
                   + "CONCAT(p.first_name,' ',p.last_name) AS patient_name, "
                   + "CONCAT(d.first_name,' ',d.last_name) AS doctor_name "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id=p.patient_id "
                   + "JOIN doctors d ON a.doctor_id=d.doctor_id "
                   + "WHERE a.appointment_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appointment> findAll() throws HospitalException {
        String sql = "SELECT a.*, "
                   + "CONCAT(p.first_name,' ',p.last_name) AS patient_name, "
                   + "CONCAT(d.first_name,' ',d.last_name) AS doctor_name "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id=p.patient_id "
                   + "JOIN doctors d ON a.doctor_id=d.doctor_id "
                   + "ORDER BY a.appointment_date DESC, a.appointment_time";
        List<Appointment> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list appointments: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find today's appointments (from the vw_DoctorScheduleToday view).
     */
    public List<Appointment> findToday() throws HospitalException {
        String sql = "SELECT a.*, "
                   + "CONCAT(p.first_name,' ',p.last_name) AS patient_name, "
                   + "CONCAT(d.first_name,' ',d.last_name) AS doctor_name "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id=p.patient_id "
                   + "JOIN doctors d ON a.doctor_id=d.doctor_id "
                   + "WHERE a.appointment_date = CURDATE() "
                   + "ORDER BY a.appointment_time";
        List<Appointment> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list today's appointments: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find appointments by patient ID.
     */
    public List<Appointment> findByPatientId(int patientId) throws HospitalException {
        String sql = "SELECT a.*, "
                   + "CONCAT(p.first_name,' ',p.last_name) AS patient_name, "
                   + "CONCAT(d.first_name,' ',d.last_name) AS doctor_name "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id=p.patient_id "
                   + "JOIN doctors d ON a.doctor_id=d.doctor_id "
                   + "WHERE a.patient_id=? ORDER BY a.appointment_date DESC";
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to find appointments: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Cancel an appointment (update status).
     */
    public void cancelAppointment(int appointmentId) throws HospitalException {
        String sql = "UPDATE appointments SET status='CANCELLED' WHERE appointment_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to cancel appointment: " + e.getMessage(), e);
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setPatientName(rs.getString("patient_name"));
        a.setDoctorName(rs.getString("doctor_name"));
        Date d = rs.getDate("appointment_date");
        if (d != null) a.setAppointmentDate(d.toLocalDate());
        Time t = rs.getTime("appointment_time");
        if (t != null) a.setAppointmentTime(t.toLocalTime());
        a.setReason(rs.getString("reason"));
        String st = rs.getString("status");
        if (st != null) a.setStatus(AppointmentStatus.valueOf(st));
        a.setNotes(rs.getString("notes"));
        return a;
    }
}
