package com.hms.service;

import com.hms.dao.AppointmentDAO;
import com.hms.exception.AppointmentException;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Appointment;

import java.util.List;

/**
 * Appointment business logic service.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * Book an appointment (via stored procedure with conflict check).
     */
    public int bookAppointment(Appointment appointment) throws HospitalException {
        return appointmentDAO.insert(appointment);
    }

    public Appointment getAppointmentById(int id) throws HospitalException {
        return appointmentDAO.findById(id)
                .orElseThrow(() -> new AppointmentException(
                        "Appointment not found with ID: " + id));
    }

    public List<Appointment> getAllAppointments() throws HospitalException {
        return appointmentDAO.findAll();
    }

    public List<Appointment> getTodayAppointments() throws HospitalException {
        return appointmentDAO.findToday();
    }

    public List<Appointment> getPatientAppointments(int patientId) throws HospitalException {
        return appointmentDAO.findByPatientId(patientId);
    }

    public void updateAppointment(Appointment appointment) throws HospitalException {
        getAppointmentById(appointment.getAppointmentId());
        appointmentDAO.update(appointment);
    }

    public void cancelAppointment(int appointmentId) throws HospitalException {
        getAppointmentById(appointmentId); // verify exists
        appointmentDAO.cancelAppointment(appointmentId);
    }
}
