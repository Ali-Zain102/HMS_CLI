package com.hms.service;

import com.hms.dao.BillDAO;
import com.hms.dao.PaymentDAO;
import com.hms.exception.BillingException;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Bill;
import com.hms.model.entity.Payment;

import java.util.List;

/**
 * Billing and payment business logic service.
 */
public class BillingService {

    private final BillDAO    billDAO;
    private final PaymentDAO paymentDAO;

    public BillingService() {
        this.billDAO    = new BillDAO();
        this.paymentDAO = new PaymentDAO();
    }

    /**
     * Generate a bill manually.
     */
    public int generateBill(Bill bill) throws HospitalException {
        bill.setTotalAmount(bill.calculateTotal());
        return billDAO.insert(bill);
    }

    public Bill getBillById(int id) throws HospitalException {
        return billDAO.findById(id)
                .orElseThrow(() -> new BillingException(
                        "Bill not found with ID: " + id));
    }

    public List<Bill> getAllBills() throws HospitalException {
        return billDAO.findAll();
    }

    public List<Bill> getPendingBills() throws HospitalException {
        return billDAO.findPending();
    }

    public List<Bill> getBillsByPatient(int patientId) throws HospitalException {
        return billDAO.findByPatientId(patientId);
    }

    /**
     * Process payment (via stored procedure).
     * @return payment ID
     */
    public int processPayment(Payment payment) throws HospitalException {
        return paymentDAO.insert(payment);
    }

    /**
     * Get all payments for a bill.
     */
    public List<Payment> getPaymentsForBill(int billId) throws HospitalException {
        return paymentDAO.findByBillId(billId);
    }

    /**
     * Get outstanding balance for a bill.
     */
    public double getOutstandingBalance(int billId) throws HospitalException {
        Bill bill = getBillById(billId);
        double paid = paymentDAO.getTotalPaidForBill(billId);
        return Math.max(0, bill.getTotalAmount() - paid);
    }
}
