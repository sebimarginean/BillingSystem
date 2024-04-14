package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Clasa Client reprezintă un client cu nume, identificator unic și datorie totală.
 */
public class Client {
    private String clientName;

    private int clientId;

    private double totalDebt;
    /**
     * Constructor pentru crearea unui client cu nume și identificator unic specificat.
     */
    public Client(String clientName, int clientId) {
        this.clientName = clientName;
        this.clientId = clientId;
        this.totalDebt = 0.0;
    }
    /**
     * Adaugă datoria la totalul curent al clientului.
     *
     * @param amount Suma de adăugat la datoria totală.
     */
    public void addDebt(double amount) {
        this.totalDebt += amount;
    }
    /**
     * Returnează datoria totală a clientului.
     *
     * @return Datoria totală.
     */
    public double getTotalDebt() {
        return totalDebt;
    }

    /**
     * Returnează numele clientului.
     *
     * @return Numele clientului.
     */
    public String getClientName() {
        return clientName;
    }
    /**
     * Returnează identificatorul unic al clientului.
     *
     * @return Identificatorul unic al clientului.
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Setează datoria totală a clientului la o valoare specificată.
     *
     * @param totalDebt Valoarea la care să fie setată datoria totală.
     */
    public void setTotalDebt(double totalDebt) {
        this.totalDebt = totalDebt;
    }
    /**
     * Salvează informațiile despre client într-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date în care să fie salvate informațiile despre client.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void saveToDatabase(Connection conn) throws SQLException {
        String sql = "INSERT INTO clients (client_id, client_name, total_debt) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.clientId);
            pstmt.setString(2, this.clientName);
            pstmt.setDouble(3, this.totalDebt);
            pstmt.executeUpdate();
        }
    }
    /**
     * Șterge informațiile despre client dintr-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date din care să fie șterse informațiile despre client.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void deleteFromDatabase(Connection conn) throws SQLException {
        String sql = "DELETE FROM clients WHERE client_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.clientId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Actualizează datoria totală a clientului într-o bază de date specificată prin conexiunea dată.
     *
     * @param conn Conexiunea la baza de date în care să fie actualizată datoria totală a clientului.
     * @throws SQLException Dacă apare o eroare la interacțiunea cu baza de date.
     */
    public void updateDebtInDatabase(Connection conn) throws SQLException {
        String sql = "UPDATE clients SET total_debt = ? WHERE client_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, this.totalDebt);
            pstmt.setInt(2, this.clientId);
            pstmt.executeUpdate();
        }
    }
}
