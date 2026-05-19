package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.Gender;
import com.hms.exception.HospitalException;
import com.hms.model.concrete.Nurse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Nurse Data Access Object.
 */
public class NurseDAO implements GenericDAO<Nurse, Integer> {

    private final Connection conn;

    public NurseDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Integer insert(Nurse n) throws HospitalException {
        String sql = "INSERT INTO nurses (first_name, last_name, gender, date_of_birth, "
                   + "phone, email, department_id, ward, salary, hire_date) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getFirstName());
            ps.setString(2, n.getLastName());
            ps.setString(3, n.getGender().name());
            ps.setDate(4, Date.valueOf(n.getDateOfBirth()));
            ps.setString(5, n.getPhone());
            ps.setString(6, n.getEmail());
            ps.setInt(7, n.getDepartmentId());
            ps.setString(8, n.getWard());
            ps.setDouble(9, n.getSalary());
            ps.setDate(10, Date.valueOf(n.getHireDate()));
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new HospitalException("Failed to get generated nurse ID.");
        } catch (SQLException e) {
            throw new HospitalException("Failed to add nurse: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Nurse n) throws HospitalException {
        String sql = "UPDATE nurses SET first_name=?, last_name=?, gender=?, "
                   + "date_of_birth=?, phone=?, email=?, department_id=?, "
                   + "ward=?, salary=?, is_active=? WHERE nurse_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, n.getFirstName());
            ps.setString(2, n.getLastName());
            ps.setString(3, n.getGender().name());
            ps.setDate(4, Date.valueOf(n.getDateOfBirth()));
            ps.setString(5, n.getPhone());
            ps.setString(6, n.getEmail());
            ps.setInt(7, n.getDepartmentId());
            ps.setString(8, n.getWard());
            ps.setDouble(9, n.getSalary());
            ps.setBoolean(10, n.isActive());
            ps.setInt(11, n.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update nurse: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM nurses WHERE nurse_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete nurse: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Nurse> findById(Integer id) throws HospitalException {
        String sql = "SELECT * FROM nurses WHERE nurse_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find nurse: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Nurse> findAll() throws HospitalException {
        String sql = "SELECT * FROM nurses ORDER BY nurse_id";
        List<Nurse> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list nurses: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Nurse> findByWard(String ward) throws HospitalException {
        String sql = "SELECT * FROM nurses WHERE ward LIKE ? AND is_active = TRUE";
        List<Nurse> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + ward + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Search failed: " + e.getMessage(), e);
        }
        return list;
    }

    private Nurse mapRow(ResultSet rs) throws SQLException {
        Nurse n = new Nurse();
        n.setId(rs.getInt("nurse_id"));
        n.setFirstName(rs.getString("first_name"));
        n.setLastName(rs.getString("last_name"));
        n.setGender(Gender.fromString(rs.getString("gender")));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) n.setDateOfBirth(dob.toLocalDate());
        n.setPhone(rs.getString("phone"));
        n.setEmail(rs.getString("email"));
        n.setDepartmentId(rs.getInt("department_id"));
        n.setWard(rs.getString("ward"));
        n.setSalary(rs.getDouble("salary"));
        Date hd = rs.getDate("hire_date");
        if (hd != null) n.setHireDate(hd.toLocalDate());
        n.setActive(rs.getBoolean("is_active"));
        return n;
    }
}
