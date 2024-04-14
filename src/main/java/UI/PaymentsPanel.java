package UI;

import models.DatabaseConnection;
import models.Invoice;
import models.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clasa PaymentPanel reprezintă un panou în interfața grafică a sistemului de facturare pentru gestionarea plaților.
 */
public class PaymentsPanel extends JPanel {
    private ClientsPanel clientsPanel;
    private List<Invoice> invoices;

    private List<Payment> payments;
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    private JTextField paymentIdField, paidAmountField, invoiceIdField;
    private JButton addButton, deleteButton;
    private JSpinner paymentDateSpinner;
    private InvoicesPanel invoicesPanel;


    public PaymentsPanel(List<Invoice> sharedInvoices, ClientsPanel clientsPanel, InvoicesPanel invoicesPanel) {
        this.invoices = sharedInvoices;
        this.clientsPanel = clientsPanel;
        this.payments = new ArrayList<>();
        this.invoicesPanel = invoicesPanel;

        setLayout(new BorderLayout());
        initializeUI();
        loadPaymentsFromDatabase();
    }
    /**
     * Inițializează interfața utilizator.
     */
    private void initializeUI() {
        String[] columnNames = {"ID Plată", "Data Plății", "Suma Plătită", "ID Factură"};
        tableModel = new DefaultTableModel(columnNames, 0);
        paymentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2));

        detailsPanel.add(new JLabel("ID Plată:"));
        paymentIdField = new JTextField(20);
        detailsPanel.add(paymentIdField);

        detailsPanel.add(new JLabel("Data Plății:"));
        paymentDateSpinner = new JSpinner(new SpinnerDateModel());
        detailsPanel.add(paymentDateSpinner);

        detailsPanel.add(new JLabel("Suma Plătită:"));
        paidAmountField = new JTextField(20);
        detailsPanel.add(paidAmountField);

        detailsPanel.add(new JLabel("ID Factură:"));
        invoiceIdField = new JTextField(20);
        detailsPanel.add(invoiceIdField);

        addButton = new JButton("Adaugă Plată");
        addButton.addActionListener(e -> addPayment());
        detailsPanel.add(addButton);

        deleteButton = new JButton("Șterge Plată");
        deleteButton.addActionListener(e -> deletePayment());
        detailsPanel.add(deleteButton);

        add(detailsPanel, BorderLayout.PAGE_END);
    }
    /**
     * Găsește o factură în lista de facturi după ID-ul acesteia.
     *
     * @param invoiceId ID-ul facturii.
     * @return Factura găsită sau `null` dacă nu a fost găsită nicio factură cu ID-ul respectiv.
     */
    private Invoice findInvoiceById(int invoiceId) {
        for (Invoice invoice : invoices) {
            if (invoice.getInvoiceId() == invoiceId) {
                return invoice;
            }
        }
        return null;
    }
    /**
     * Adaugă o nouă plată în lista de plăți și în baza de date.
     */
    private void addPayment() {
        try {
            int paymentId = Integer.parseInt(paymentIdField.getText().trim());
            Date paymentDate = (Date) paymentDateSpinner.getValue();
            float paidAmount = Float.parseFloat(paidAmountField.getText().trim());
            int invoiceId = Integer.parseInt(invoiceIdField.getText().trim());

            Invoice invoice = findInvoiceById(invoiceId);
            if (invoice == null) {
                JOptionPane.showMessageDialog(this, "Factura cu ID-ul " + invoiceId + " nu există.");
                return;
            }
            if (invoice.getTotalToPay() < paidAmount) {
                JOptionPane.showMessageDialog(this, "Suma plătită este mai mare decât totalul de plată al facturii.");
                return;
            }

            invoice.reduceTotalToPay(paidAmount);
            invoice.updateTotalToPayInDatabase(DatabaseConnection.getConnection()); // Actualizează totalul de plată în baza de date

            Payment newPayment = new Payment(paymentId, paymentDate, paidAmount, invoiceId);
            newPayment.saveToDatabase(DatabaseConnection.getConnection());

            payments.add(newPayment);
            tableModel.addRow(new Object[]{paymentId, paymentDate, paidAmount, invoiceId});
            invoicesPanel.updateInvoiceTable(invoiceId, invoice.getTotalToPay());

            clientsPanel.updateClientDebt(invoice.getClientId(), -paidAmount);

        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare: " + e.getMessage());
        }
    }


    /**
     * Șterge plata selectată din listă și din baza de date.
     */
    private void deletePayment() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Payment paymentToDelete = payments.get(selectedRow);
            Invoice invoice = findInvoiceById(paymentToDelete.getInvoiceId());

            if (invoice != null) {
                try {
                    paymentToDelete.deleteFromDatabase(DatabaseConnection.getConnection()); // Ștergerea plății din baza de date

                    invoice.increaseTotalToPay(paymentToDelete.getPaidAmount()); // Adăugarea sumei plătite înapoi la totalul facturii
                    clientsPanel.updateClientDebt(invoice.getClientId(), paymentToDelete.getPaidAmount()); // Actualizarea datoriei clientului

                    payments.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                    invoicesPanel.updateInvoiceTable(paymentToDelete.getInvoiceId(), invoice.getTotalToPay()); // Actualizarea tabelului de facturi

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Eroare la ștergerea din baza de date: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nu s-a găsit factura corespunzătoare plății selectate.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selectați o plată pentru a șterge.");
        }
    }

    private boolean doesPaymentExist(int paymentId) {
        return payments.stream().anyMatch(payment -> payment.getPaymentId() == paymentId);
    }


    private void reloadTableData() {
        tableModel.setRowCount(0);
        for (Payment payment : payments) {
            tableModel.addRow(new Object[]{payment.getPaymentId(), payment.getPaymentDate(), payment.getPaidAmount(), payment.getInvoiceId()});
        }
    }
    /**
     * Încarcă datele plăților din baza de date și le afișează în tabel.
     */
    private void loadPaymentsFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT payment_id, payment_date, paid_amount, invoice_id FROM payments";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int paymentId = rs.getInt("payment_id");
                    Date paymentDate = rs.getDate("payment_date");
                    float paidAmount = rs.getFloat("paid_amount");
                    int invoiceId = rs.getInt("invoice_id");

                    Payment payment = new Payment(paymentId, paymentDate, paidAmount, invoiceId);
                    payments.add(payment);
                    tableModel.addRow(new Object[]{paymentId, paymentDate, paidAmount, invoiceId});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea plăților din baza de date: " + e.getMessage());
        }
    }

}
