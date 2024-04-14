import models.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BillingSystemTest {
    private BillingSystem billingSystem;
    private Client client1, client2;
    private Invoice invoice1, invoice2;

    @Before
    public void setUp() {
        billingSystem = new BillingSystem();
        client1 = new Client("Client1", 1);
        client2 = new Client("Client2", 2);
        invoice1 = new Invoice(1, 1, new java.util.Date());
        invoice2 = new Invoice(2, 2, new java.util.Date());
    }

    @Test
    public void testAddClient() {
        billingSystem.addClient(client1);
        assertEquals(1, billingSystem.getClientList().size());
        assertEquals(client1, billingSystem.getClientList().get(0));

        billingSystem.addClient(client2);
        assertEquals(2, billingSystem.getClientList().size());
        assertEquals(client2, billingSystem.getClientList().get(1));
    }

    @Test
    public void testAddInvoice() {
        billingSystem.addInvoice(invoice1);
        assertEquals(1, billingSystem.getInvoiceList().size());
        assertEquals(invoice1, billingSystem.getInvoiceList().get(0));

        billingSystem.addInvoice(invoice2);
        assertEquals(2, billingSystem.getInvoiceList().size());
        assertEquals(invoice2, billingSystem.getInvoiceList().get(1));
    }

    @Test
    public void testAddNullClient() {
        billingSystem.addClient(null);
        assertTrue(billingSystem.getClientList().isEmpty());
    }

    @Test
    public void testAddNullInvoice() {
        billingSystem.addInvoice(null);
        assertTrue(billingSystem.getInvoiceList().isEmpty());
    }
}
