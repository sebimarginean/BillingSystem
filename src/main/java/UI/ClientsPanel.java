package UI;

import models.Client;
import models.DatabaseConnection;
import models.Invoice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa ClientsPanel reprezintă un panou în interfața grafică a sistemului de facturare pentru gestionarea clienților.
 */

public class ClientsPanel extends JPanel {
    private List<Client> clients;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, idField;
    private JButton addButton, deleteButton, searchButton;
    /**
     * Constructor pentru crearea unui panou ClientsPanel cu o listă de clienți partajată.
     *
     * @param sharedClients Lista de clienți partajată cu alte componente ale aplicației.
     */
    public ClientsPanel(List<Client> sharedClients) {
        this.clients = sharedClients;
        setLayout(new BorderLayout());
        initializeUI();
        loadClientsFromDatabase();
    }
    /**
     * Inițializează interfața utilizator pentru gestionarea clienților.
     */

    private void initializeUI() {
        String[] columnNames = {"CUI Client", "Nume Client", "Total Datorii"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel managePanel = new JPanel(new GridLayout(0, 2));
        managePanel.add(new JLabel("ID Client:"));
        idField = new JTextField(20);
        managePanel.add(idField);
        managePanel.add(new JLabel("Nume Client:"));
        nameField = new JTextField(20);
        managePanel.add(nameField);

        addButton = new JButton("Adaugă Client");
        addButton.addActionListener(e -> addClient());
        managePanel.add(addButton);

        deleteButton = new JButton("Șterge Client");
        deleteButton.addActionListener(e -> deleteClient());
        managePanel.add(deleteButton);


        searchButton = new JButton("Caută Client");
        searchButton.addActionListener(e -> searchClient());
        managePanel.add(searchButton);


        add(managePanel, BorderLayout.PAGE_END);
    }
    /**
     * Încarcă clienții din baza de date și îi afișează în tabel.
     */
    private void loadClientsFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT client_id, client_name, total_debt FROM clients";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("client_id");
                    String name = rs.getString("client_name");
                    double debt = rs.getDouble("total_debt");
                    Client client = new Client(name, id);
                    client.setTotalDebt(debt); // Asigurați-vă că aveți un setter pentru totalDebt în clasa Client
                    clients.add(client);
                    tableModel.addRow(new Object[]{id, name, debt});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea datelor din baza de date: " + e.getMessage());
        }
    }
    /**
     * Adaugă un client nou în listă și în baza de date.
     */
    private void addClient() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            if (!name.isEmpty() && !isDuplicate(id) && !isNameDuplicate(name)) {
                Client newClient = new Client(name, id);
                clients.add(newClient);
                tableModel.addRow(new Object[]{id, name, 0.0});

                try {
                    newClient.saveToDatabase(DatabaseConnection.getConnection());
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Eroare la salvarea în baza de date: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "ID-ul și numele clientului nu pot fi goale sau duplicate.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID-ul trebuie să fie un număr valid.");
        }
    }
    /**
     * Șterge un client selectat din listă și din baza de date.
     */
    private void deleteClient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int clientId = (Integer) tableModel.getValueAt(selectedRow, 0);

            try {
                Client clientToDelete = clients.stream()
                        .filter(client -> client.getClientId() == clientId)
                        .findFirst()
                        .orElse(null);

                if (clientToDelete != null) {
                    clientToDelete.deleteFromDatabase(DatabaseConnection.getConnection());

                    clients.remove(clientToDelete);
                    tableModel.removeRow(selectedRow);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Eroare la ștergerea din baza de date: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selectați un client pentru a șterge.");
        }
    }

    /**
     * Caută un client în tabel în funcție de ID sau nume.
     */
    private void searchClient() {
        String idText = idField.getText().trim();
        String nameText = nameField.getText().trim();
        boolean found = false;

        try {
            Integer searchId = idText.isEmpty() ? null : Integer.parseInt(idText);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Integer rowId = (Integer) tableModel.getValueAt(i, 0);
                String rowName = (String) tableModel.getValueAt(i, 1);

                boolean idMatch = (searchId != null && rowId.equals(searchId));
                boolean nameMatch = (!nameText.isEmpty() && rowName.equalsIgnoreCase(nameText));

                if ((searchId != null && nameText.isEmpty() && idMatch) ||
                        (searchId == null && !nameText.isEmpty() && nameMatch) ||
                        (searchId != null && !nameText.isEmpty() && idMatch && nameMatch)) {
                    table.setRowSelectionInterval(i, i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "Clientul specificat nu a fost găsit.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID-ul trebuie să fie un număr valid.");
        }
    }
    /**
     * Actualizează datoria unui client și actualizează rândul corespunzător din tabel.
     *
     * @param clientId ID-ul clientului pentru care se actualizează datoria.
     * @param amount   Suma de datorie de adăugat sau de scăzut.
     */
    public void updateClientDebt(int clientId, double amount) {
        for (Client client : clients) {
            if (client.getClientId() == clientId) {
                client.addDebt(amount);
                try {
                    client.updateDebtInDatabase(DatabaseConnection.getConnection());
                    updateClientRowInTable(client);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Eroare la actualizarea datoriei în baza de date: " + e.getMessage());
                }
                break;
            }
        }
    }
    /**
     * Actualizează rândul tabelului cu datoria actualizată a unui client.
     *
     * @param client Clientul pentru care se actualizează rândul în tabel.
     */
    private void updateClientRowInTable(Client client) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int tableClientId = (Integer) tableModel.getValueAt(i, 0);
            if (tableClientId == client.getClientId()) {
                tableModel.setValueAt(client.getTotalDebt(), i, 2);
                break;
            }
        }
    }


    private boolean isDuplicate(int id) {
        return clients.stream().anyMatch(client -> client.getClientId() == id);
    }
    private boolean isNameDuplicate(String name) {
        return clients.stream().anyMatch(client -> client.getClientName().equalsIgnoreCase(name));
    }
    public boolean doesClientExist(int clientId) {
        return clients.stream().anyMatch(client -> client.getClientId() == clientId);
    }

}
