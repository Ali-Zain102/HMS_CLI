package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.BillStatus;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Bill Data Access Object.
 */
public class BillDAO implements GenericDAO<Bill, Integer> {

    private final Connection conn;

    public BillDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Integer insert(Bill b) throws HospitalException {
        String sql = "INSERT INTO bills (patient_id, bill_date, consultation_fee, bed_charges, "
                   + "medicine_charges, lab_charges, other_charges, discount, total_amount, "
                   + "status, notes) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getPatientId());
            ps.setDate(2, Date.valueOf(b.getBillDate()));
            ps.setDouble(3, b.getConsultationFee());
            ps.setDouble(4, b.getBedCharges());
            ps.setDouble(5, b.getMedicineCharges());
            ps.setDouble(6, b.getLabCharges());
            ps.setDouble(7, b.getOtherCharges());
            ps.setDouble(8, b.getDiscount());
            ps.setDouble(9, b.getTotalAmount());
            ps.setString(10, b.getStatus().name());
            ps.setString(11, b.getNotes());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new HospitalException("Failed to get generated bill ID.");
        } catch (SQLException e) {
            throw new HospitalException("Failed to create bill: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Bill b) throws HospitalException {
        String sql = "UPDATE bills SET consultation_fee=?, bed_charges=?, medicine_charges=?, "
                   + "lab_charges=?, other_charges=?, discount=?, total_amount=?, "
                   + "status=?, notes=? WHERE bill_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, b.getConsultationFee());
            ps.setDouble(2, b.getBedCharges());
            ps.setDouble(3, b.getMedicineCharges());
            ps.setDouble(4, b.getLabCharges());
            ps.setDouble(5, b.getOtherCharges());
            ps.setDouble(6, b.getDiscount());
            ps.setDouble(7, b.getTotalAmount());
            ps.setString(8, b.getStatus().name());
            ps.setString(9, b.getNotes());
            ps.setInt(10, b.getBillId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update bill: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete bill: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Bill> findById(Integer id) throws HospitalException {
        String sql = "SELECT b.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM bills b JOIN patients p ON b.patient_id=p.patient_id "
                   + "WHERE b.bill_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find bill: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Bill> findAll() throws HospitalException {
        String sql = "SELECT b.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM bills b JOIN patients p ON b.patient_id=p.patient_id "
                   + "ORDER BY b.bill_id DESC";
        List<Bill> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list bills: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find pending/partial bills (from vw_PendingBills).
     */
    public List<Bill> findPending() throws HospitalException {
        String sql = "SELECT b.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM bills b JOIN patients p ON b.patient_id=p.patient_id "
                   + "WHERE b.status IN ('PENDING','PARTIAL') ORDER BY b.bill_date";
        List<Bill> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list pending bills: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find bills by patient ID.
     */
    public List<Bill> findByPatientId(int patientId) throws HospitalException {
        String sql = "SELECT b.*, CONCAT(p.first_name,' ',p.last_name) AS patient_name "
                   + "FROM bills b JOIN patients p ON b.patient_id=p.patient_id "
                   + "WHERE b.patient_id=? ORDER BY b.bill_date DESC";
        List<Bill> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to find bills: " + e.getMessage(), e);
        }
        return list;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setPatientId(rs.getInt("patient_id"));
        try { b.setPatientName(rs.getString("patient_name")); }
        catch (SQLException ignored) {}
        Date d = rs.getDate("bill_date");
        if (d != null) b.setBillDate(d.toLocalDate());
        b.setConsultationFee(rs.getDouble("consultation_fee"));
        b.setBedCharges(rs.getDouble("bed_charges"));
        b.setMedicineCharges(rs.getDouble("medicine_charges"));
        b.setLabCharges(rs.getDouble("lab_charges"));
        b.setOtherCharges(rs.getDouble("other_charges"));
        b.setDiscount(rs.getDouble("discount"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        String st = rs.getString("status");
        if (st != null) b.setStatus(BillStatus.valueOf(st));
        b.setNotes(rs.getString("notes"));
        return b;
    }
}
