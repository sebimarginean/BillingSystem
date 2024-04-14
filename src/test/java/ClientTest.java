import models.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ClientTest {
    private Client client;
    private final int clientId = 1;
    private final String clientName = "Test Client";
    private final double debtAmount = 100.0;

    @Before
    public void setUp() {
        client = new Client(clientName, clientId);
    }

    @Test
    public void testClientCreation() {
        assertEquals(clientId, client.getClientId());
        assertEquals(clientName, client.getClientName());
        assertEquals(0.0, client.getTotalDebt(), 0.001);
    }

    @Test
    public void testAddDebt() {
        client.addDebt(debtAmount);
        assertEquals(debtAmount, client.getTotalDebt(), 0.001);
    }

    @Test
    public void testSetTotalDebt() {
        client.setTotalDebt(debtAmount);
        assertEquals(debtAmount, client.getTotalDebt(), 0.001);
    }

}

