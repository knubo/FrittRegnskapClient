package no.knubo.accounting.client.views.kid;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class PriceMatcherTest extends TestCase {

    private PriceMatcher priceMatcher;

    @Override
    protected void setUp() throws Exception {
        JavaScriptObject jsValue = null;
        JSONObject prices = new JSONObject(jsValue) {
            Map<String,String> data = new HashMap<String,String>();
            @Override
            public JSONValue put(String key, JSONValue jsonValue) {
                data.put(key, jsonValue.toString());
                return jsonValue;
            }
            
            @Override
            public JSONValue get(String key) {
                return new JSONStringTest(data.get(key));
            }
        };
        prices.put("course", new JSONStringTest("700.00"));
        prices.put("train", new JSONStringTest("400.00"));
        prices.put("year", new JSONStringTest("300.00"));
        prices.put("youth", new JSONStringTest("350.00"));
        prices.put("yearyouth", new JSONStringTest("150.00"));
        priceMatcher = new PriceMatcher(prices);

    }

    public void test_matches_one_price_year_first() {
        String[] prices = priceMatcher.matchPrices(300, false);

        Assert.assertEquals(1, prices.length);
        Assert.assertEquals("year", prices[0]);
    }

    public void test_matches_two_with_year_pri() {
        String[] prices = priceMatcher.matchPrices(700, false);

        Assert.assertEquals(2, prices.length);
        Assert.assertEquals("year", prices[0]);
        Assert.assertEquals("train", prices[1]);
    }

    public void test_if_skipYear_no_match_year() {
        String[] prices = priceMatcher.matchPrices(700, true);

        Assert.assertEquals(1, prices.length);
        Assert.assertEquals("course", prices[0]);
    }

    public void test_if_skipYear_no_match_year_exact_year_sum() {
        String[] prices = priceMatcher.matchPrices(300, true);
        
        Assert.assertEquals(0, prices.length);
    }

    public void test_cannot_match_more_than_2_if_so_match_none() {
        String[] prices = priceMatcher.matchPrices(1400, false);

        Assert.assertEquals(0, prices.length);
    }

    public void test_cannot_match_two_of_type_year() {
        String[] prices = priceMatcher.matchPrices(450, false);

        Assert.assertEquals(0, prices.length);

    }

    public void test_cannot_match_two_of_type_membership() {
        String[] prices = priceMatcher.matchPrices(1100, false);

        Assert.assertEquals(0, prices.length);

    }
    
    static class JSONStringTest extends JSONString {

        private final String value2;

        public JSONStringTest(String value) {
            super(value);
            value2 = value;
        }
        
        @Override
        public String toString() {
            return value2;
        }
        
    }
}
