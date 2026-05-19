package com.hms.dao;

import com.hms.database.DatabaseConnection;
import com.hms.enums.BedStatus;
import com.hms.enums.BedType;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Bed;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Bed Data Access Object.
 */
public class BedDAO implements GenericDAO<Bed, Integer> {

    private final Connection conn;

    public BedDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Integer insert(Bed b) throws HospitalException {
        String sql = "INSERT INTO beds (bed_number, ward, bed_type, status, daily_rate) "
                   + "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getBedNumber());
            ps.setString(2, b.getWard());
            ps.setString(3, b.getBedType().name());
            ps.setString(4, b.getStatus().name());
            ps.setDouble(5, b.getDailyRate());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            throw new HospitalException("Failed to get generated bed ID.");
        } catch (SQLException e) {
            throw new HospitalException("Failed to add bed: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Bed b) throws HospitalException {
        String sql = "UPDATE beds SET bed_number=?, ward=?, bed_type=?, "
                   + "status=?, daily_rate=?, patient_id=? WHERE bed_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBedNumber());
            ps.setString(2, b.getWard());
            ps.setString(3, b.getBedType().name());
            ps.setString(4, b.getStatus().name());
            ps.setDouble(5, b.getDailyRate());
            if (b.getPatientId() > 0) {
                ps.setInt(6, b.getPatientId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, b.getBedId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to update bed: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Integer id) throws HospitalException {
        String sql = "DELETE FROM beds WHERE bed_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new HospitalException("Failed to delete bed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Bed> findById(Integer id) throws HospitalException {
        String sql = "SELECT * FROM beds WHERE bed_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new HospitalException("Failed to find bed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Bed> findAll() throws HospitalException {
        String sql = "SELECT * FROM beds ORDER BY ward, bed_number";
        List<Bed> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list beds: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Find available beds (uses vw_AvailableBeds concept).
     */
    public List<Bed> findAvailable() throws HospitalException {
        String sql = "SELECT * FROM beds WHERE status = 'AVAILABLE' ORDER BY ward, bed_number";
        List<Bed> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new HospitalException("Failed to list available beds: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Get bed occupancy summary per ward.
     */
    public List<String[]> getBedOccupancySummary() throws HospitalException {
        String sql = "SELECT * FROM vw_BedOccupancy";
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{
                    rs.getString("ward"),
                    String.valueOf(rs.getInt("total_beds")),
                    String.valueOf(rs.getInt("available")),
                    String.valueOf(rs.getInt("occupied")),
                    String.valueOf(rs.getInt("maintenance")),
                    rs.getString("occupancy_pct") + "%"
                });
            }
        } catch (SQLException e) {
            throw new HospitalException("Failed to get occupancy: " + e.getMessage(), e);
        }
        return rows;
    }

    private Bed mapRow(ResultSet rs) throws SQLException {
        Bed b = new Bed();
        b.setBedId(rs.getInt("bed_id"));
        b.setBedNumber(rs.getString("bed_number"));
        b.setWard(rs.getString("ward"));
        String type = rs.getString("bed_type");
        if (type != null) b.setBedType(BedType.valueOf(type));
        String status = rs.getString("status");
        if (status != null) b.setStatus(BedStatus.valueOf(status));
        b.setDailyRate(rs.getDouble("daily_rate"));
        b.setPatientId(rs.getInt("patient_id"));
        return b;
    }
}
