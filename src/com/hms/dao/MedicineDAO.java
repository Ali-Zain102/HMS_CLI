package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Medicine Data Access Object.
 */
public class MedicineDAO implements GenericDAO<Medicine, Integer> {

    private final Connection conn;

    public MedicineDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Integer insert(Medicine m) throws HospitalException {
        String sql = "INSERT INTO medicines (name, category, unit_price, stock_quantity, "
                   + "reorder_level, expiry_date, manufacturer) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getUnitPrice());
            ps.setInt(4, m.getStockQuantity());
            ps.setInt(5, m.getReorderLevel());
            if (m.getExpiryDate() != null) {
                ps.setDate(6, Date.valueOf(m.getExpiryDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, m.getManufacturer());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new HospitalException("Failed to get generated medicine ID.");
        } catch (SQLException e) {
            throw new HospitalException("Failed to add medicine: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Medicine m) throws HospitalException {
        String sql = "UPDATE medicines SET name=?, category=?, unit_price=?, "
                   + "stock_quantity=?, reorder_level=?, expiry_date=?, manufacturer=? "
                   + "WHERE medicine_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setDouble(3, m.getUnitPrice());
            ps.setInt(4, m.getStockQuantity());
            ps.setInt(5, m.getReorderLevel());
            if (m.getExpiryDate() != null) {
                ps.setDate(6, Date.valueOf(m.getExpiryDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, m.getManufacturer());
            ps.setInt(8, m.getMedicineId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update medicine: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete medicine: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Medicine> findById(Integer id) throws HospitalException {
        String sql = "SELECT * FROM medicines WHERE medicine_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find medicine: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Medicine> findAll() throws HospitalException {
        String sql = "SELECT * FROM medicines ORDER BY name";
        List<Medicine> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list medicines: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find medicines with low stock (at or below reorder level).
     */
    public List<Medicine> findLowStock() throws HospitalException {
        String sql = "SELECT * FROM medicines WHERE stock_quantity <= reorder_level ORDER BY stock_quantity";
        List<Medicine> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list low-stock medicines: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Search medicines by name.
     */
    public List<Medicine> searchByName(String name) throws HospitalException {
        String sql = "SELECT * FROM medicines WHERE name LIKE ?";
        List<Medicine> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Search failed: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Update stock quantity only.
     */
    public void updateStock(int medicineId, int newQuantity) throws HospitalException {
        String sql = "UPDATE medicines SET stock_quantity = ? WHERE medicine_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, medicineId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update stock: " + e.getMessage(), e);
        }
    }

    private Medicine mapRow(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setMedicineId(rs.getInt("medicine_id"));
        m.setName(rs.getString("name"));
        m.setCategory(rs.getString("category"));
        m.setUnitPrice(rs.getDouble("unit_price"));
        m.setStockQuantity(rs.getInt("stock_quantity"));
        m.setReorderLevel(rs.getInt("reorder_level"));
        Date exp = rs.getDate("expiry_date");
        if (exp != null) m.setExpiryDate(exp.toLocalDate());
        m.setManufacturer(rs.getString("manufacturer"));
        return m;
    }
}
