package com.hms.service;

import com.hms.dao.DoctorDAO;
import com.hms.exception.DoctorNotFoundException;
import com.hms.exception.HospitalException;
import com.hms.model.concrete.Doctor;

import java.util.List;

/**
 * Doctor business logic service.
 */
public class DoctorService {

    private final DoctorDAO doctorDAO;

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    public int addDoctor(Doctor doctor) throws HospitalException {
        return doctorDAO.insert(doctor);
    }

    public Doctor getDoctorById(int id) throws HospitalException {
        return doctorDAO.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(
                        "Doctor not found with ID: " + id));
    }

    public List<Doctor> getAllDoctors() throws HospitalException {
        return doctorDAO.findAll();
    }

    public List<Doctor> getActiveDoctors() throws HospitalException {
        return doctorDAO.findActive();
    }

    public List<Doctor> findBySpecialization(String spec) throws HospitalException {
        return doctorDAO.findBySpecialization(spec);
    }

    public void updateDoctor(Doctor doctor) throws HospitalException {
        getDoctorById(doctor.getId());
        doctorDAO.update(doctor);
    }

    public void deleteDoctor(int id) throws HospitalException {
        getDoctorById(id);
        doctorDAO.delete(id);
    }
}
