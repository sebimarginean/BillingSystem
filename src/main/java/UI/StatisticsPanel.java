package UI;

import models.Client;
import models.Invoice;

import javax.swing.*;
import java.util.List;
/**
 * Clasa Statistics reprezintă un panou în interfața grafică a sistemului de facturare pentru gestionarea statisticilor.
 */
public class StatisticsPanel extends JPanel {
    private List<Client> clients;
    private List<Invoice> invoices;

    public StatisticsPanel(List<Client> clients, List<Invoice> invoices) {
        this.clients = clients;
        this.invoices = invoices;
        displayStatistics();
    }
    /**
     * Afișează statisticile pe panou,
     */
    private void displayStatistics() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("NUMĂRUL TOTAL DE CLIENȚI: " + getTotalNumberOfClients()));
        add(new JLabel("NUMĂRUL TOTAL DE FACTURI: " + getTotalNumberOfInvoices()));
        add(new JLabel("MEDIA PE FACTURĂ: " + getAverageInvoiceAmount()));
        add(new JLabel("SUMA TOTALĂ DE PE TOATE FACTURILE: " + getTotalAmountOnInvoices()));
        Client topClient = getTopClientByRevenue();
        if (topClient != null) {
            add(new JLabel("CLIENTUL CEL MAI DATOR: " + topClient.getClientName()));
        }
    }
    /**
     * Calculează numărul total de clienți din lista de clienți.
     *
     * @return Numărul total de clienți.
     */
    private int getTotalNumberOfClients() {
        return clients.size();
    }
    /**
     * Calculează numărul total de facturi din lista de facturi.
     *
     * @return Numărul total de facturi.
     */
    private int getTotalNumberOfInvoices() {
        return invoices.size();
    }
    /**
     * Calculează media sumelor de pe facturi.
     *
     * @return Media sumelor de pe facturi.
     */
    private double getAverageInvoiceAmount() {
        if (invoices.isEmpty()) {
            return 0;
        }
        double total = invoices.stream().mapToDouble(Invoice::getTotalToPay).sum();
        return total / invoices.size();
    }
    /**
     * Calculează suma totală a sumelor de pe toate facturile.
     *
     * @return Suma totală a sumelor de pe facturi.
     */
    private double getTotalAmountOnInvoices() {
        return invoices.stream().mapToDouble(Invoice::getTotalToPay).sum();
    }
    /**
     * Cea mai mare datorie
     *
     * @return Clientul cu cea mai mare datorie.
     */
    private Client getTopClientByRevenue() {
        Client topClient = null;
        double maxRevenue = 0;
        for (Client client : clients) {
            double clientRevenue = invoices.stream()
                    .filter(invoice -> invoice.getClientId() == client.getClientId())
                    .mapToDouble(Invoice::getTotalToPay)
                    .sum();
            if (clientRevenue > maxRevenue) {
                maxRevenue = clientRevenue;
                topClient = client;
            }
        }
        return topClient;
    }
    /**
     * Reîncarcă statisticile pe panou.
     */
    public void reloadStatistics() {
        removeAll();
        displayStatistics();
        revalidate();
        repaint();
    }
}
