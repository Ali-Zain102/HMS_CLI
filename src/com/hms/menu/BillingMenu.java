package com.hms.menu;

import com.hms.enums.PaymentMethod;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Bill;
import com.hms.model.entity.Payment;
import com.hms.service.BillingService;
import com.hms.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillingMenu {
    private final Scanner scanner;
    private final BillingService billingService;

    public BillingMenu(Scanner scanner) {
        this.scanner = scanner;
        this.billingService = new BillingService();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      💰 BILLING MANAGEMENT          ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. View All Bills");
            System.out.println("  2. View Pending Bills");
            System.out.println("  3. View Bill Details");
            System.out.println("  4. View Patient Bills");
            System.out.println("  5. Generate Manual Bill");
            System.out.println("  6. Process Payment");
            System.out.println("  7. View Payment History");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);
            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: viewAll(); break;
                    case 2: viewPending(); break;
                    case 3: viewDetails(); break;
                    case 4: viewPatientBills(); break;
                    case 5: generateBill(); break;
                    case 6: processPayment(); break;
                    case 7: viewPayments(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void viewAll() throws HospitalException {
        printBillTable(billingService.getAllBills());
    }

    private void viewPending() throws HospitalException {
        List<Bill> bills = billingService.getPendingBills();
        if (bills.isEmpty()) System.out.println(ConsoleColors.success("  All bills paid!"));
        else printBillTable(bills);
    }

    private void viewDetails() throws HospitalException {
        System.out.print("  Enter Bill ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        Bill b = billingService.getBillById(id);
        double outstanding = billingService.getOutstandingBalance(id);
        System.out.println(ConsoleColors.heading("\n  ═══ BILL DETAILS ═══"));
        TablePrinter.printDetail("Bill ID", String.valueOf(b.getBillId()));
        TablePrinter.printDetail("Patient", b.getPatientName());
        TablePrinter.printDetail("Date", DateFormatter.formatForDisplay(b.getBillDate()));
        TablePrinter.printDetail("Consultation Fee", String.format("PKR %.2f", b.getConsultationFee()));
        TablePrinter.printDetail("Bed Charges", String.format("PKR %.2f", b.getBedCharges()));
        TablePrinter.printDetail("Medicine Charges", String.format("PKR %.2f", b.getMedicineCharges()));
        TablePrinter.printDetail("Lab Charges", String.format("PKR %.2f", b.getLabCharges()));
        TablePrinter.printDetail("Other Charges", String.format("PKR %.2f", b.getOtherCharges()));
        TablePrinter.printDetail("Discount", String.format("PKR %.2f", b.getDiscount()));
        TablePrinter.printDetail("TOTAL", String.format("PKR %.2f", b.getTotalAmount()));
        TablePrinter.printDetail("OUTSTANDING", String.format("PKR %.2f", outstanding));
        TablePrinter.printDetail("Status", b.getStatus().name());
    }

    private void viewPatientBills() throws HospitalException {
        System.out.print("  Enter Patient ID: ");
        int pid = InputValidator.parseIntSafe(scanner.nextLine());
        if (pid <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        List<Bill> bills = billingService.getBillsByPatient(pid);
        if (bills.isEmpty()) System.out.println(ConsoleColors.warn("  No bills found."));
        else printBillTable(bills);
    }

    private void generateBill() throws HospitalException {
        System.out.print("  Patient ID: ");
        int pid = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Consultation Fee: "); double c = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Bed Charges: "); double bed = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Medicine Charges: "); double med = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Lab Charges: "); double lab = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Other Charges: "); double oth = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Discount: "); double disc = InputValidator.parseDoubleSafe(scanner.nextLine());
        if (pid <= 0) { System.out.println(ConsoleColors.error("  Invalid input!")); return; }
        Bill bill = new Bill();
        bill.setPatientId(pid);
        bill.setConsultationFee(Math.max(0,c)); bill.setBedCharges(Math.max(0,bed));
        bill.setMedicineCharges(Math.max(0,med)); bill.setLabCharges(Math.max(0,lab));
        bill.setOtherCharges(Math.max(0,oth)); bill.setDiscount(Math.max(0,disc));
        int id = billingService.generateBill(bill);
        System.out.println(ConsoleColors.success("  ✓ Bill generated! ID: " + id));
    }

    private void processPayment() throws HospitalException {
        System.out.print("  Bill ID: ");
        int billId = InputValidator.parseIntSafe(scanner.nextLine());
        if (billId <= 0) { System.out.println(ConsoleColors.error("  Invalid!")); return; }
        double out = billingService.getOutstandingBalance(billId);
        System.out.println("  Outstanding: PKR " + String.format("%.2f", out));
        if (out <= 0) { System.out.println(ConsoleColors.success("  Already paid!")); return; }
        System.out.print("  Amount: "); double amt = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Method (CASH/CARD/INSURANCE/ONLINE): ");
        String m = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Reference (optional): "); String ref = scanner.nextLine().trim();
        PaymentMethod method;
        try { method = PaymentMethod.valueOf(m); }
        catch (Exception e) { System.out.println(ConsoleColors.error("  Invalid method!")); return; }
        Payment p = new Payment(); p.setBillId(billId); p.setAmountPaid(amt);
        p.setPaymentMethod(method); p.setReferenceNo(ref.isEmpty() ? null : ref);
        int payId = billingService.processPayment(p);
        System.out.println(ConsoleColors.success("  ✓ Payment processed! ID: " + payId));
    }

    private void viewPayments() throws HospitalException {
        System.out.print("  Enter Bill ID: ");
        int billId = InputValidator.parseIntSafe(scanner.nextLine());
        if (billId <= 0) { System.out.println(ConsoleColors.error("  Invalid!")); return; }
        List<Payment> payments = billingService.getPaymentsForBill(billId);
        if (payments.isEmpty()) { System.out.println(ConsoleColors.warn("  No payments.")); return; }
        String[] h = {"Pay ID", "Amount", "Method", "Date", "Reference"};
        List<String[]> rows = new ArrayList<>();
        for (Payment p : payments) {
            rows.add(new String[]{String.valueOf(p.getPaymentId()),
                String.format("PKR %.2f", p.getAmountPaid()), p.getPaymentMethod().name(),
                p.getPaymentDate() != null ? p.getPaymentDate().toString() : "N/A",
                p.getReferenceNo() != null ? p.getReferenceNo() : "-"});
        }
        TablePrinter.printTable(h, rows);
    }

    private void printBillTable(List<Bill> bills) {
        String[] h = {"ID", "Patient", "Date", "Total (PKR)", "Status"};
        List<String[]> rows = new ArrayList<>();
        for (Bill b : bills) {
            rows.add(new String[]{String.valueOf(b.getBillId()),
                b.getPatientName() != null ? b.getPatientName() : "ID:" + b.getPatientId(),
                DateFormatter.formatForDisplay(b.getBillDate()),
                String.format("%.2f", b.getTotalAmount()), b.getStatus().name()});
        }
        TablePrinter.printTable(h, rows);
    }
}
