package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.PaymentMethod;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Payment Data Access Object.
 * Uses stored procedure sp_ProcessPayment for validated payments.
 */
public class PaymentDAO implements GenericDAO<Payment, Integer> {

    private final Connection conn;

    public PaymentDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Process payment via stored procedure sp_ProcessPayment.
     */
    @Override
    public Integer insert(Payment p) throws HospitalException {
        String sql = "{CALL sp_ProcessPayment(?,?,?,?,?,?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, p.getBillId());
            cs.setDouble(2, p.getAmountPaid());
            cs.setString(3, p.getPaymentMethod().name());
            cs.setString(4, p.getReferenceNo());
            cs.registerOutParameter(5, Types.INTEGER);
            cs.registerOutParameter(6, Types.VARCHAR);
            cs.execute();

            int paymentId = cs.getInt(5);
            String message = cs.getString(6);
            if (paymentId == -1) {
                throw new HospitalException(message);
            }
            return paymentId;
        } catch (SQLException e) {
            throw new HospitalException("Payment failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Payment p) throws HospitalException {
        throw new HospitalException("Payments cannot be updated once processed.");
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        throw new HospitalException("Payments cannot be deleted.");
    }

    @Override
    public Optional<Payment> findById(Integer id) throws HospitalException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find payment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> findAll() throws HospitalException {
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        List<Payment> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list payments: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find payments for a specific bill.
     */
    public List<Payment> findByBillId(int billId) throws HospitalException {
        String sql = "SELECT * FROM payments WHERE bill_id = ? ORDER BY payment_date";
        List<Payment> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to find payments: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Get total amount paid for a bill.
     */
    public double getTotalPaidForBill(int billId) throws HospitalException {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) AS total_paid "
                   + "FROM payments WHERE bill_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total_paid");
            return 0;
        } catch (SQLException e) {
            throw new HospitalException("Failed to get total paid: " + e.getMessage(), e);
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setBillId(rs.getInt("bill_id"));
        p.setAmountPaid(rs.getDouble("amount_paid"));
        Timestamp ts = rs.getTimestamp("payment_date");
        if (ts != null) p.setPaymentDate(ts.toLocalDateTime());
        String method = rs.getString("payment_method");
        if (method != null) p.setPaymentMethod(PaymentMethod.valueOf(method));
        p.setReferenceNo(rs.getString("reference_no"));
        p.setNotes(rs.getString("notes"));
        return p;
    }
}
