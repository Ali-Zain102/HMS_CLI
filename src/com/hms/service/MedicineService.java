package com.hms.service;

import com.hms.dao.MedicineDAO;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Medicine;

import java.util.List;

/**
 * Medicine/Pharmacy business logic service.
 */
public class MedicineService {

    private final MedicineDAO medicineDAO;

    public MedicineService() {
        this.medicineDAO = new MedicineDAO();
    }

    public int addMedicine(Medicine medicine) throws HospitalException {
        return medicineDAO.insert(medicine);
    }

    public Medicine getMedicineById(int id) throws HospitalException {
        return medicineDAO.findById(id)
                .orElseThrow(() -> new HospitalException(
                        "Medicine not found with ID: " + id));
    }

    public List<Medicine> getAllMedicines() throws HospitalException {
        return medicineDAO.findAll();
    }

    public List<Medicine> getLowStockMedicines() throws HospitalException {
        return medicineDAO.findLowStock();
    }

    public List<Medicine> searchMedicines(String name) throws HospitalException {
        return medicineDAO.searchByName(name);
    }

    public void updateMedicine(Medicine medicine) throws HospitalException {
        getMedicineById(medicine.getMedicineId());
        medicineDAO.update(medicine);
    }

    public void updateStock(int medicineId, int newQuantity) throws HospitalException {
        getMedicineById(medicineId);
        medicineDAO.updateStock(medicineId, newQuantity);
    }
}
