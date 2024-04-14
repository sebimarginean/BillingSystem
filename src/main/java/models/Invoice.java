package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Clasa Invoice reprezintă o factură în sistemul de facturare, conținând ID-ul facturii,
 * ID-ul clientului, data emiterii și totalul de plată.
 */
public class Invoice {

    private int invoiceId;
    private int clientId;
    private Date issueDate;
    private float totalToPay;

    public Invoice(int invoiceId, int clientId, Date issueDate) {
        this.invoiceId = invoiceId;
        this.clientId = clientId;
        this.issueDate = issueDate;
        this.totalToPay = 0;
    }


    /**
     * Reduce suma totală de plată a facturii.
     *
     * @param amount Suma de bani care se scade din totalul de plată.
     */
    public void reduceTotalToPay(float amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Suma nu poate fi negativă.");
        }
        this.totalToPay = Math.max(0, this.totalToPay - amount);
    }

    /**
     * Mărește suma totală de plată a facturii.
     *
     * @param amount Suma de bani care se adaugă la totalul de plată.
     */
    public void increaseTotalToPay(float amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Suma nu poate fi negativă.");
        }
        this.totalToPay += amount;
    }
    /**
     * Returnează ID-ul facturii.
     *
     * @return ID-ul facturii.
     */
    public int getInvoiceId() {
        return invoiceId;
    }

    /**
     * Returnează ID-ul clientului asociat facturii.
     *
     * @return ID-ul clientului asociat facturii.
     */
    public int getClientId() {
        return clientId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public float getTotalToPay() {
        return totalToPay;
    }

    /**
     * Setează totalul de plată pentru factură la o valoare specifică.
     *
     * @param totalToPay Noua valoare a totalului de plată pentru factură.
     */
    public void setTotalToPay(float totalToPay) {
        this.totalToPay = totalToPay;
    }

    /**
     * Salvează informațiile despre factură într-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date în care să fie salvate informațiile despre factură.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void saveToDatabase(Connection conn) throws SQLException {
        String sql = "INSERT INTO invoices (invoice_id, client_id, issue_date, total_to_pay) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.getInvoiceId());
            pstmt.setInt(2, this.getClientId());
            pstmt.setDate(3, new java.sql.Date(this.getIssueDate().getTime()));
            pstmt.setDouble(4, this.getTotalToPay());
            pstmt.executeUpdate();
        }
    }

    /**
     * Șterge informațiile despre factură dintr-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date din care să fie șterse informațiile despre factură.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void deleteFromDatabase(Connection conn) throws SQLException {
        String sql = "DELETE FROM invoices WHERE invoice_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.getInvoiceId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Actualizează totalul de plată pentru factură într-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date în care să fie actualizat totalul de plată pentru factură.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void updateTotalToPayInDatabase(Connection conn) throws SQLException {
        String sql = "UPDATE invoices SET total_to_pay = ? WHERE invoice_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, this.getTotalToPay());
            pstmt.setInt(2, this.getInvoiceId());
            pstmt.executeUpdate();
        }
    }
}