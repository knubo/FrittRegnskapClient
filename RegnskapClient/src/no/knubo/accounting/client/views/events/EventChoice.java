package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class EventChoice {

    static final String MAX_DIFFERENCE_SEX = "maxDifferenceSex";
    static final String MAX = "max";
    static final String PRICE_YOUTH = "priceYouth";
    static final String PRICE_TRAIN = "priceTrain";
    static final String PRICE_LESSONS = "priceLessons";
    static final String PRICE_MEMBERS = "priceMembers";
    static final String MEMB_REQ = "membershipRequired";
    static final String PRICE = "price";
    static final String TO_DATE = "toDate";
    static final String FROM_DATE = "fromDate";
    static final String GROUP = "group";
    static final String NAME = "name";
    JSONObject obj;

    public EventChoice(JSONObject obj) {
        this.obj = obj;
    }

    public EventChoice() {
        obj = new JSONObject();

        put(NAME, "");
        put(GROUP, "");
        put(FROM_DATE, "");
        put(TO_DATE, "");
        put(PRICE, "");
        put(PRICE_MEMBERS, "");
        put(MEMB_REQ, "");
        put(PRICE_LESSONS, "");
        put(PRICE_TRAIN, "");
        put(PRICE_YOUTH, "");
        put(MAX, "");
        put(MAX_DIFFERENCE_SEX, "");
    }

    void put(String key, String value) {
        obj.put(key, new JSONString(value));
    }

    public String getName() {
        return Util.str(obj.get(NAME));
    }

    public String getGroup() {
        return Util.str(obj.get(GROUP));
    }

    public String getFromDate() {
        return Util.str(obj.get(FROM_DATE));
    }

    public String getToDate() {
        return Util.str(obj.get(TO_DATE));
    }

    public String getPrice() {
        return Util.str(obj.get(PRICE));
    }

    public String getPriceMembers() {
        return Util.str(obj.get(PRICE_MEMBERS));
    }

    public String getPriceLessons() {
        return Util.str(obj.get(PRICE_LESSONS));
    }

    public String getPriceTrain() {
        return Util.str(obj.get(PRICE_TRAIN));
    }

    public String getPriceYouth() {
        return Util.str(obj.get(PRICE_YOUTH));
    }

    public String getMaxNumber() {
        return Util.str(obj.get(MAX));
    }

    public String getMaxDifferenceSex() {
        return Util.str(obj.get(MAX_DIFFERENCE_SEX));
    }

    public Boolean getMembershipRequired() {
        return Util.str(obj.get(MEMB_REQ)).equals("1");
    }

    public JSONValue getAsJSON() {
        return obj;
    }
}
