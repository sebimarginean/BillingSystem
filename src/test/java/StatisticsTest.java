import models.*;
import org.junit.Before;
import java.util.ArrayList;
import java.util.List;


public class StatisticsTest {
    private Statistics statistics;
    private List<Client> clients;
    private List<Invoice> invoices;
    private List<Payment> payments;

    @Before
    public void setUp() {
        clients = new ArrayList<>();
        invoices = new ArrayList<>();
        payments = new ArrayList<>();

        clients.add(new Client("Client1", 1));
        clients.add(new Client("Client2", 2));

        invoices.add(new Invoice(1, 1, new java.util.Date()));
        invoices.add(new Invoice(2, 2, new java.util.Date()));

        payments.add(new Payment(1, new java.util.Date(), 100.0f, 1));
        payments.add(new Payment(2, new java.util.Date(), 200.0f, 2));

        statistics = new Statistics(clients, invoices, payments);
    }

}
