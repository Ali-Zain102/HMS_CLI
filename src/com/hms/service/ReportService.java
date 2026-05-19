package com.hms.service;

import com.hms.dao.LabReportDAO;
import com.hms.exception.HospitalException;
import com.hms.model.entity.LabReport;

import java.util.List;

/**
 * Lab Report / Report business logic service.
 */
public class ReportService {

    private final LabReportDAO labReportDAO;

    public ReportService() {
        this.labReportDAO = new LabReportDAO();
    }

    /**
     * Request a new lab test.
     */
    public int requestLabTest(LabReport report) throws HospitalException {
        return labReportDAO.insert(report);
    }

    public LabReport getReportById(int id) throws HospitalException {
        return labReportDAO.findById(id)
                .orElseThrow(() -> new HospitalException(
                        "Lab report not found with ID: " + id));
    }

    public List<LabReport> getAllReports() throws HospitalException {
        return labReportDAO.findAll();
    }

    public List<LabReport> getPatientReports(int patientId) throws HospitalException {
        return labReportDAO.findByPatientId(patientId);
    }

    public List<LabReport> getPendingReports() throws HospitalException {
        return labReportDAO.findPending();
    }

    /**
     * Enter lab results (update status to COMPLETED with results).
     */
    public void enterResults(LabReport report) throws HospitalException {
        getReportById(report.getReportId()); // verify exists
        labReportDAO.update(report);
    }
}
