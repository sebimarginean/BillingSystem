package models;

import models.Invoice;
import models.Payment;

import java.util.List;

/**
 * Clasa Statistics conține liste de clienți, facturi și plăți,
 * utilizate pentru generarea statisticilor în cadrul sistemului de facturare.
 */
public class Statistics {
    private List<Client> clients;
    private List<Invoice> invoices;
    private List<Payment> payments;

    /**
     * Constructor pentru crearea unui obiect Statistics cu liste specificate de clienți, facturi și plăți.
     *
     * @param clients  Lista de clienți.
     * @param invoices Lista de facturi.
     * @param payments Lista de plăți.
     */
    public Statistics(List<Client> clients, List<Invoice> invoices, List<Payment> payments) {
        this.clients = clients;
        this.invoices = invoices;
        this.payments = payments;
    }
}