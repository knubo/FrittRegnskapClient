package no.knubo.accounting.client.misc;

import org.junit.Assert;
import org.junit.Test;

public class LuhnTest {

    @Test
    public void check_luhn() {
        Assert.assertEquals("8", Luhn.generateDigit("15"));
    }

}
