import models.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

public class InvoiceTest {
    private Invoice invoice;
    private final int invoiceId = 1;
    private final int clientId = 10;
    private final Date issueDate = new Date();
    private final float amount = 100.0f;

    @Before
    public void setUp() {
        invoice = new Invoice(invoiceId, clientId, issueDate);
    }

    @Test
    public void testInvoiceCreation() {
        assertEquals(invoiceId, invoice.getInvoiceId());
        assertEquals(clientId, invoice.getClientId());
        assertEquals(issueDate, invoice.getIssueDate());
        assertEquals(0.0f, invoice.getTotalToPay(), 0.001);
    }

    @Test
    public void testIncreaseTotalToPay() {
        invoice.increaseTotalToPay(amount);
        assertEquals(amount, invoice.getTotalToPay(), 0.001);
    }

    @Test
    public void testReduceTotalToPay() {
        invoice.setTotalToPay(amount);
        invoice.reduceTotalToPay(amount / 2);
        assertEquals(amount / 2, invoice.getTotalToPay(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReduceTotalToPayWithInvalidAmount() {
        invoice.reduceTotalToPay(-10.0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncreaseTotalToPayWithInvalidAmount() {
        invoice.increaseTotalToPay(-10.0f);
    }
}
