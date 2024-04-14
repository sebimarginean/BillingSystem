package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clasa DatabaseConnection furnizează o conexiune la baza de date MySQL pentru sistemul de facturare.
 * Această clasă utilizează JDBC pentru a stabili și returna conexiunea la baza de date specificată.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sistem_facturare";
    private static final String USER = "root";
    private static final String PASSWORD = "YOUR_PASSWORD";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Nu s-a putut conecta la baza de date", e);
        }
    }
}
