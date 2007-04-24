package no.knubo.accounting.client;

import junit.framework.TestCase;

public class Utiltest extends TestCase {

	public void testMoney() {
		
		assertEquals(Util.money("100.00"), "100.00");
		assertEquals(Util.money("1.00"), "1.00");
		assertEquals(Util.money("1000.00"), "1,000.00");
		assertEquals(Util.money("1000000.00"), "1,000,000.00");
		assertEquals(Util.money("100000.00"), "100,000.00");
	}
}
