package UI;

import models.Client;
import models.Invoice;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Clasa BillingSystemGUI reprezintă interfața grafică a sistemului de facturare.
 */
public class BillingSystemGUI {

    private JFrame frame;
    private JTabbedPane tabbedPane;
    private ClientsPanel clientsPanel;
    private InvoicesPanel invoicesPanel;

    public BillingSystemGUI() {
        initialize();
    }
    /**
     * Inițializează componentele interfeței grafice.
     */
    private void initialize() {
        frame = new JFrame("Sistem de Facturare");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tabbedPane = new JTabbedPane();

        List<Client> sharedClients = new ArrayList<>();
        List<Invoice> sharedInvoices = new ArrayList<>();

        clientsPanel = new ClientsPanel(sharedClients);
        invoicesPanel = new InvoicesPanel(sharedInvoices, clientsPanel);


        tabbedPane.addTab("Clienți", clientsPanel);
        tabbedPane.addTab("Facturi", invoicesPanel);
        tabbedPane.addTab("Plăți", new PaymentsPanel(sharedInvoices, clientsPanel, invoicesPanel));
        StatisticsPanel statisticsPanel = new StatisticsPanel(sharedClients, sharedInvoices);
        tabbedPane.addTab("Statistici", statisticsPanel);
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() instanceof StatisticsPanel) {
                statisticsPanel.reloadStatistics();
            }
        });


        frame.add(tabbedPane, BorderLayout.CENTER);
    }
    /**
     * Afișează fereastra principală a aplicației.
     */
    public void show() {
        frame.setVisible(true);
    }
    /**
     * Metoda principală pentru pornirea aplicației.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BillingSystemGUI gui = new BillingSystemGUI();
            gui.show();
        });
    }
}
