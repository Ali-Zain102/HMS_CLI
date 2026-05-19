package com.hms.service;

import com.hms.dao.BedDAO;
import com.hms.exception.BedNotAvailableException;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Bed;

import java.util.List;

/**
 * Bed management business logic service.
 */
public class BedService {

    private final BedDAO bedDAO;

    public BedService() {
        this.bedDAO = new BedDAO();
    }

    public int addBed(Bed bed) throws HospitalException {
        return bedDAO.insert(bed);
    }

    public Bed getBedById(int id) throws HospitalException {
        return bedDAO.findById(id)
                .orElseThrow(() -> new BedNotAvailableException(
                        "Bed not found with ID: " + id));
    }

    public List<Bed> getAllBeds() throws HospitalException {
        return bedDAO.findAll();
    }

    public List<Bed> getAvailableBeds() throws HospitalException {
        return bedDAO.findAvailable();
    }

    public void updateBed(Bed bed) throws HospitalException {
        getBedById(bed.getBedId());
        bedDAO.update(bed);
    }

    /**
     * Get bed occupancy summary for all wards.
     */
    public List<String[]> getOccupancySummary() throws HospitalException {
        return bedDAO.getBedOccupancySummary();
    }
}
