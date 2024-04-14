import models.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.*;

public class PaymentTest {
    private Payment payment;
    private final int paymentId = 1;
    private final Date paymentDate = new Date();
    private final float paidAmount = 150.0f;
    private final int invoiceId = 10;

    @Before
    public void setUp() {
        payment = new Payment(paymentId, paymentDate, paidAmount, invoiceId);
    }

    @Test
    public void testPaymentCreation() {
        assertEquals(paymentId, payment.getPaymentId());
        assertEquals(paymentDate, payment.getPaymentDate());
        assertEquals(paidAmount, payment.getPaidAmount(), 0.001);
        assertEquals(invoiceId, payment.getInvoiceId());
    }
}
