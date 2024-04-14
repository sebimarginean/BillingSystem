package models;

import java.util.List;
import java.util.ArrayList;
/**
 * Clasa BillingSystem reprezintă un sistem de facturare care permite adăugarea de clienți și facturi.
 */
public class BillingSystem {
    private List<Invoice> invoiceList;
    private List<Client> clientList;

    /**
     * Constructorul clasei BillingSystem inițializează listele de facturi și clienți.
     */
    public BillingSystem() {
        invoiceList = new ArrayList<>();
        clientList = new ArrayList<>();
    }

    /**
     * Adaugă un client în lista de clienți a sistemului.
     *
     * @param client Obiectul Client de adăugat în sistem.
     */
    public void addClient(Client client) {
        if (client != null) {
            clientList.add(client);
        }
    }

    /**
     * Adaugă o factură în lista de facturi a sistemului.
     *
     * @param invoice Obiectul Invoice de adăugat în sistem.
     */
    public void addInvoice(Invoice invoice) {
        if (invoice != null) {
            invoiceList.add(invoice);
        }
    }

    /**
     * Getter pentru lista de facturi.
     *
     * @return lista de facturi.
     */
    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    /**
     * Getter pentru lista de clienți.
     *
     * @return lista de clienți.
     */
    public List<Client> getClientList() {
        return clientList;
    }
}

