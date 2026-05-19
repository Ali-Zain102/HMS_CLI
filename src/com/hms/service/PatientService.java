package com.hms.service;

import com.hms.dao.PatientDAO;
import com.hms.enums.PatientStatus;
import com.hms.exception.HospitalException;
import com.hms.exception.PatientNotFoundException;
import com.hms.model.concrete.Patient;

import java.util.List;

/**
 * Patient business logic service.
 */
public class PatientService {

    private final PatientDAO patientDAO;

    public PatientService() {
        this.patientDAO = new PatientDAO();
    }

    /**
     * Register a new patient (via stored procedure).
     */
    public int registerPatient(Patient patient) throws HospitalException {
        return patientDAO.insert(patient);
    }

    /**
     * Get patient by ID or throw exception.
     */
    public Patient getPatientById(int id) throws HospitalException {
        return patientDAO.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Patient not found with ID: " + id));
    }

    /**
     * Get all patients.
     */
    public List<Patient> getAllPatients() throws HospitalException {
        return patientDAO.findAll();
    }

    /**
     * Get patients by status.
     */
    public List<Patient> getPatientsByStatus(PatientStatus status) throws HospitalException {
        return patientDAO.findByStatus(status);
    }

    /**
     * Search patients by name.
     */
    public List<Patient> searchPatients(String name) throws HospitalException {
        return patientDAO.searchByName(name);
    }

    /**
     * Update patient info.
     */
    public void updatePatient(Patient patient) throws HospitalException {
        // Verify exists
        getPatientById(patient.getId());
        patientDAO.update(patient);
    }

    /**
     * Admit patient (via stored procedure).
     */
    public String admitPatient(int patientId, int bedId, int doctorId)
            throws HospitalException {
        return patientDAO.admitPatient(patientId, bedId, doctorId);
    }

    /**
     * Discharge patient (via stored procedure).
     */
    public String dischargePatient(int patientId, double discount)
            throws HospitalException {
        return patientDAO.dischargePatient(patientId, discount);
    }

    /**
     * Delete patient.
     */
    public void deletePatient(int id) throws HospitalException {
        getPatientById(id); // verify exists
        patientDAO.delete(id);
    }
}
