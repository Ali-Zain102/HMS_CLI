package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.Gender;
import com.hms.exception.HospitalException;
import com.hms.model.concrete.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Doctor Data Access Object.
 */
public class DoctorDAO implements GenericDAO<Doctor, Integer> {

    private final Connection conn;

    public DoctorDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Integer insert(Doctor d) throws HospitalException {
        String sql = "INSERT INTO doctors (first_name, last_name, gender, date_of_birth, "
                   + "phone, email, specialization, department_id, salary, hire_date) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getGender().name());
            ps.setDate(4, Date.valueOf(d.getDateOfBirth()));
            ps.setString(5, d.getPhone());
            ps.setString(6, d.getEmail());
            ps.setString(7, d.getSpecialization());
            ps.setInt(8, d.getDepartmentId());
            ps.setDouble(9, d.getSalary());
            ps.setDate(10, Date.valueOf(d.getHireDate()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new HospitalException("Failed to get generated doctor ID.");
        } catch (SQLException e) {
            throw new HospitalException("Failed to add doctor: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Doctor d) throws HospitalException {
        String sql = "UPDATE doctors SET first_name=?, last_name=?, gender=?, "
                   + "date_of_birth=?, phone=?, email=?, specialization=?, "
                   + "department_id=?, salary=?, is_active=? WHERE doctor_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getFirstName());
            ps.setString(2, d.getLastName());
            ps.setString(3, d.getGender().name());
            ps.setDate(4, Date.valueOf(d.getDateOfBirth()));
            ps.setString(5, d.getPhone());
            ps.setString(6, d.getEmail());
            ps.setString(7, d.getSpecialization());
            ps.setInt(8, d.getDepartmentId());
            ps.setDouble(9, d.getSalary());
            ps.setBoolean(10, d.isActive());
            ps.setInt(11, d.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update doctor: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete doctor: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Doctor> findById(Integer id) throws HospitalException {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find doctor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Doctor> findAll() throws HospitalException {
        String sql = "SELECT * FROM doctors ORDER BY doctor_id";
        List<Doctor> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list doctors: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find active doctors only.
     */
    public List<Doctor> findActive() throws HospitalException {
        String sql = "SELECT * FROM doctors WHERE is_active = TRUE ORDER BY last_name";
        List<Doctor> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list active doctors: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find doctors by specialization.
     */
    public List<Doctor> findBySpecialization(String spec) throws HospitalException {
        String sql = "SELECT * FROM doctors WHERE specialization LIKE ? AND is_active = TRUE";
        List<Doctor> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + spec + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Search failed: " + e.getMessage(), e);
        }
        return list;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getInt("doctor_id"));
        d.setFirstName(rs.getString("first_name"));
        d.setLastName(rs.getString("last_name"));
        d.setGender(Gender.fromString(rs.getString("gender")));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) d.setDateOfBirth(dob.toLocalDate());
        d.setPhone(rs.getString("phone"));
        d.setEmail(rs.getString("email"));
        d.setSpecialization(rs.getString("specialization"));
        d.setDepartmentId(rs.getInt("department_id"));
        d.setSalary(rs.getDouble("salary"));
        Date hd = rs.getDate("hire_date");
        if (hd != null) d.setHireDate(hd.toLocalDate());
        d.setActive(rs.getBoolean("is_active"));
        return d;
    }
}
