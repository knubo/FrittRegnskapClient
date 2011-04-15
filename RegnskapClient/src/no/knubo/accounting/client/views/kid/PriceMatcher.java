package no.knubo.accounting.client.views.kid;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONObject;

class PriceMatcher {

    String[] keys = { "course", "train", "youth" };

    private final JSONObject prices;

    PriceMatcher(JSONObject prices) {
        this.prices = prices;

    }

    String[] matchPrices(double amount, boolean skipYear, boolean skipMemberships) {

        if (!skipYear) {
            double amountLeft = amount - Util.getDouble(prices.get("year"));

            if (amountLeft == 0) {
                return new String[] { "year" };
            }

            if (!skipMemberships) {
                for (int i = 0; i < keys.length; i++) {
                    if (Util.getDouble(prices.get(keys[i])) == amountLeft) {
                        return new String[] { "year", keys[i] };
                    }
                }
            }

            amountLeft = amount - Util.getDouble(prices.get("yearyouth"));
            if (amountLeft == 0) {
                return new String[] { "yearyouth" };
            }

            if (!skipMemberships) {
                for (int i = 0; i < keys.length; i++) {
                    if (Util.getDouble(prices.get(keys[i])) == amountLeft) {
                        return new String[] { "yearyouth", keys[i] };
                    }
                }
            }
        }

        if (!skipMemberships) {
            for (int i = 0; i < keys.length; i++) {
                if (Util.getDouble(prices.get(keys[i])) == amount) {
                    return new String[] { keys[i] };
                }
            }
        }

        return new String[] {};
    }
}
