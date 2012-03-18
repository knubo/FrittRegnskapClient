package no.knubo.accounting.client;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class RegexpTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("15 mai 2009".matches("\\d\\d? \\w+ 20\\d\\d"));

        System.out.println("Bingo [[FrittRegnskapBeta Les mer]] Lotto".replaceAll("\\[\\[(\\w+)\\s(.+)\\]\\]",
                "<a href=\"/wakka/$1\">$2</a>"));
    }

    @Test
    public void testARegexp() {
        Pattern pattern = Pattern.compile("^(?!.*  )(?=.*\\p{Alpha})(?=.*\\p{Digit})[\\p{Graph} ]{6,}$", Pattern.CASE_INSENSITIVE);
        
        Assert.assertFalse(pattern.matcher("abcdef").matches());
        Assert.assertTrue(pattern.matcher("abcde1").matches());
        Assert.assertFalse(pattern.matcher("abcd1").matches());
        Assert.assertTrue(pattern.matcher("abcd1aa").matches());
        Assert.assertTrue(pattern.matcher("AAAA 1").matches());
        Assert.assertFalse(pattern.matcher("AAA  1").matches());

        Assert.assertTrue(pattern.matcher("AAA__1").matches());
        Assert.assertTrue(pattern.matcher("AAA??1").matches());
        Assert.assertTrue(pattern.matcher("AAA~~1").matches());
        Assert.assertFalse(pattern.matcher("AAA‘‘1").matches());
    }
}
