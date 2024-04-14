package UI;

import models.DatabaseConnection;
import models.Invoice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
/**
 * Clasa InvoicesPanel reprezintă un panou în interfața grafică a sistemului de facturare pentru gestionarea facturilor.
 */
public class InvoicesPanel extends JPanel {
    private List<Invoice> invoices;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;

    private JTextField invoiceIdField, clientIdField, totalField;
    private JTextField searchInvoiceField, filterClientField;
    private JButton searchInvoiceButton, filterClientButton, resetSearchAndFilterButton;

    private JButton addButton, deleteButton;
    private JSpinner issueDateSpinner;

    private ClientsPanel clientsPanel;

    public InvoicesPanel(List<Invoice> sharedInvoices, ClientsPanel clientsPanel) {
        this.invoices = sharedInvoices;
        this.clientsPanel = clientsPanel;
        setLayout(new BorderLayout());
        initializeUI();
        loadInvoicesFromDatabase();
    }
    /**
     * Inițializează interfața utilizator pentru gestionarea elementelor grafice ale acestei ferestre.
     */

    private void initializeUI() {
        String[] columnNames = {"ID Factura", "ID Client", "Emitere", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);
        invoiceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        add(scrollPane, BorderLayout.CENTER);

        detailsPanel = new JPanel(new GridLayout(0, 2));

        detailsPanel.add(new JLabel("ID Factură:"));
        invoiceIdField = new JTextField(20);
        detailsPanel.add(invoiceIdField);

        detailsPanel.add(new JLabel("ID Client:"));
        clientIdField = new JTextField(20);
        detailsPanel.add(clientIdField);

        detailsPanel.add(new JLabel("Emitere:"));
        issueDateSpinner = new JSpinner(new SpinnerDateModel());
        detailsPanel.add(issueDateSpinner);

        detailsPanel.add(new JLabel("Total:"));
        totalField = new JTextField(20);
        detailsPanel.add(totalField);

        addButton = new JButton("Adaugă Factura");
        addButton.addActionListener(e -> addInvoice());
        detailsPanel.add(addButton);


        deleteButton = new JButton("Anulează factura selectată");
        deleteButton.addActionListener(e -> deleteSelectedInvoice());
        detailsPanel.add(deleteButton);

        searchInvoiceField = new JTextField(20);
        searchInvoiceButton = new JButton("Caută Factură");
        searchInvoiceButton.addActionListener(e -> searchInvoice());

        filterClientField = new JTextField(20);
        filterClientButton = new JButton("Filtrează după Client");
        filterClientButton.addActionListener(e -> filterByClient());

        detailsPanel.add(searchInvoiceField);
        detailsPanel.add(searchInvoiceButton);

        detailsPanel.add(filterClientField);
        detailsPanel.add(filterClientButton);

        resetSearchAndFilterButton = new JButton("Resetează Căutare/Filtrare");
        resetSearchAndFilterButton.addActionListener(e -> resetSearchAndFilter());
        detailsPanel.add(resetSearchAndFilterButton);


        add(detailsPanel, BorderLayout.PAGE_END);
    }
    /**
     * Adaugă o factură nouă în lista de facturi și în baza de date.
     */
    private void addInvoice() {
        try {
            int invoiceId = Integer.parseInt(invoiceIdField.getText().trim());
            int clientId = Integer.parseInt(clientIdField.getText().trim());
            Date issueDate = (Date) issueDateSpinner.getValue();
            float totalToPay = Float.parseFloat(totalField.getText().trim());
            if (doesInvoiceExist(invoiceId)) {
                JOptionPane.showMessageDialog(this, "Numărul facturii " + invoiceId + " există deja.");
                return;
            }
            if (!clientsPanel.doesClientExist(clientId)) {
                JOptionPane.showMessageDialog(this, "Clientul cu CUI-ul " + clientId + " nu există.");
                return;
            }

            Invoice newInvoice = new Invoice(invoiceId, clientId, issueDate);
            newInvoice.setTotalToPay(totalToPay);
            invoices.add(newInvoice);

            try {
                newInvoice.saveToDatabase(DatabaseConnection.getConnection());
                tableModel.addRow(new Object[]{invoiceId, clientId, issueDate, totalToPay});
                clientsPanel.updateClientDebt(clientId, totalToPay);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Eroare la salvarea în baza de date: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Asigura-te ca ai introdus date valide");
        }
    }
    /**
     * Șterge factura selectată din listă și din baza de date.
     */
    private void deleteSelectedInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow >= 0) {
            Invoice invoice = invoices.get(selectedRow);
            int clientId = invoice.getClientId();
            double totalToPay = invoice.getTotalToPay();

            try {
                invoice.deleteFromDatabase(DatabaseConnection.getConnection());
                invoices.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                clientsPanel.updateClientDebt(clientId, -totalToPay);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Eroare la ștergerea din baza de date: " + e.getMessage());
            }

        } else {
            JOptionPane.showMessageDialog(this, "Nu ai selectat factura de sters");
        }
    }
    /**
     * Caută o factură după ID și actualizează tabelul cu rezultatele găsite.
     */
    private void searchInvoice() {
        String searchTerm = searchInvoiceField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                int invoiceId = Integer.parseInt(searchTerm);
                tableModel.setRowCount(0);
                for (Invoice invoice : invoices) {
                    if (invoice.getInvoiceId() == invoiceId) {
                        tableModel.addRow(new Object[]{invoice.getInvoiceId(), invoice.getClientId(), invoice.getIssueDate(), invoice.getTotalToPay()});
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Introduceți un număr valid pentru ID-ul facturii.");
            }
        }
    }
    /**
     * Filtrează facturile după ID-ul clientului.
     */
    private void filterByClient() {
        String clientIdTerm = filterClientField.getText().trim();
        if (!clientIdTerm.isEmpty()) {
            try {
                int clientId = Integer.parseInt(clientIdTerm);
                tableModel.setRowCount(0);
                for (Invoice invoice : invoices) {
                    if (invoice.getClientId() == clientId) {
                        tableModel.addRow(new Object[]{invoice.getInvoiceId(), invoice.getClientId(), invoice.getIssueDate(), invoice.getTotalToPay()});
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Introduceți un număr valid pentru ID-ul clientului.");
            }
        }
    }
    /**
     * Resetează căutarea și filtrarea, afișând toate facturile.
     */
    private void resetSearchAndFilter() {
        searchInvoiceField.setText("");
        filterClientField.setText("");
        reloadTableData();
    }
    /**
     * Reîncarcă datele în tabel, afișând toate facturile.
     */
    private void reloadTableData() {
        tableModel.setRowCount(0);
        for (Invoice invoice : invoices) {
            tableModel.addRow(new Object[]{invoice.getInvoiceId(), invoice.getClientId(), invoice.getIssueDate(), invoice.getTotalToPay()});
        }
    }
    /**
     * Verifică dacă o factură cu un anumit ID există deja în lista de facturi.
     *
     * @param invoiceId ID-ul facturii de verificat.
     * @return `true` dacă factura există, `false` altfel.
     */
    private boolean doesInvoiceExist(int invoiceId) {
        return invoices.stream().anyMatch(invoice -> invoice.getInvoiceId() == invoiceId);
    }
    /**
     * Actualizează valoarea totalului pentru o factură specifică în tabel.
     *
     * @param invoiceId ID-ul facturii de actualizat.
     * @param newTotal  Valoarea totalului actualizat.
     */
    public void updateInvoiceTable(int invoiceId, float newTotal) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((Integer)tableModel.getValueAt(i, 0) == invoiceId) {
                tableModel.setValueAt(newTotal, i, 3);
                break;
            }
        }
    }
    /**
     * Încarcă datele facturilor din baza de date și le afișează în tabel.
     */
    private void loadInvoicesFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT invoice_id, client_id, issue_date, total_to_pay FROM invoices";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int invoiceId = rs.getInt("invoice_id");
                    int clientId = rs.getInt("client_id");
                    Date issueDate = rs.getDate("issue_date");
                    double totalToPay = rs.getDouble("total_to_pay");
                    Invoice invoice = new Invoice(invoiceId, clientId, issueDate);
                    invoice.setTotalToPay((float) totalToPay);
                    invoices.add(invoice);
                    tableModel.addRow(new Object[]{invoiceId, clientId, issueDate, totalToPay});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea facturilor din baza de date: " + e.getMessage());
        }
    }
}
