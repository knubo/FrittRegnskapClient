package no.knubo.accounting.client;

import junit.framework.TestCase;

public class Utiltest extends TestCase {

    public void testMoney() {

        assertEquals("1.00", Util.money("1"));
        assertEquals("100.00", Util.money("100.00"));
        assertEquals("1.00", Util.money("1.00"));
        assertEquals("1,000.00", Util.money("1000.00"));
        assertEquals("1,000,000.00", Util.money("1000000.00"));
        assertEquals("100,000.00", Util.money("100000.00"));
        assertEquals("9,551.50", Util.money("9551.5"));
    }
}
