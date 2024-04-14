package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * Clasa Payment reprezintă o plată în cadrul sistemului de facturare, conținând ID-ul plății,
 * data plății, suma plătită și ID-ul facturii asociate.
 */
public class Payment {
    private int paymentId;
    private Date paymentDate;
    private float paidAmount;
    private int invoiceId;
    /**
     * Constructor pentru crearea unei plăți cu ID-ul plății, data plății, suma plătită și ID-ul facturii specificate.
     */
    public Payment(int paymentId, Date paymentDate, float paidAmount, int invoiceId) {
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.paidAmount = paidAmount;
        this.invoiceId = invoiceId;
    }

    /**
     * Returnează ID-ul plății.
     *
     * @return ID-ul plății.
     */
    public int getPaymentId() {
        return paymentId;
    }
    /**
     * Returnează data plății.
     *
     * @return Data plății.
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * Returnează suma plătită.
     *
     * @return Suma plătită.
     */
    public float getPaidAmount() {
        return paidAmount;
    }


    /**
     * Returnează ID-ul facturii asociate plății.
     *
     * @return ID-ul facturii asociate plății.
     */
    public int getInvoiceId() {
        return invoiceId;
    }

    /**
     * Salvează informațiile despre plată într-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date în care să fie salvate informațiile despre plată.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void saveToDatabase(Connection conn) throws SQLException {
        String sql = "INSERT INTO payments (payment_id, payment_date, paid_amount, invoice_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.getPaymentId());
            pstmt.setDate(2, new java.sql.Date(this.getPaymentDate().getTime()));
            pstmt.setDouble(3, this.getPaidAmount());
            pstmt.setInt(4, this.getInvoiceId());
            pstmt.executeUpdate();
        }
    }
    /**
     * Șterge informațiile despre plată dintr-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date din care să fie șterse informațiile despre plată.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void deleteFromDatabase(Connection conn) throws SQLException {
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.getPaymentId());
            pstmt.executeUpdate();
        }
    }

}