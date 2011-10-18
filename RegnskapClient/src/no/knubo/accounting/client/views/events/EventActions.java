package no.knubo.accounting.client.views.events;

import java.util.LinkedList;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class EventActions {

    public static List<EventInList> getEvents() {

        LinkedList<EventInList> data = new LinkedList<EventInList>();
        for (int i = 42; i < 45; i++) {
            JSONObject one = new JSONObject();

            one.put("id", new JSONNumber(i));
            one.put("name", new JSONString("Winter Jump"));
            one.put("startDate", new JSONString("20.09.2011"));
            one.put("endDate", new JSONString("30.09.2011"));
            one.put("eventDate", new JSONString("01.10.2011"));
            one.put("maxPeople", new JSONNumber(42));

            data.add(new EventInList(one));
        }

        return data;
    }

    public static Event getEvent(String id) {

        String json = "{\"id\":"
                + id
                + ", \"active\":\"1\", \"name\":\"Winter Jump\", \"startDate\":\"20.09.2011\", \"endDate\":\"30.09.2011\", \"eventDate\":\"01.10.2011\", \"maxPeople\":4242, \"stopDate\":\"30.09.2011\", \"groups\":{\"bar\":{\"col\":2, \"row\":4}, \"bar2\":{\"col\":3, \"row\":6}}, \"choices\":[{\"name\":\"Foo\", \"group\":\"bar2\", \"fromDate\":\"20.09.2011\", \"toDate\":\"21.09.2011\", \"membershipRequired\":\"1\", \"price\":\"100\", \"priceMembers\":\"101\", \"priceLessons\":\"102\", \"priceTrain\":\"103\", \"priceYouth\":\"103\", \"max\":\"42\", \"maxDifferenceSex\":\"3\"},{\"name\":\"Foo\", \"group\":\"bar\", \"fromDate\":\"20.09.2011\", \"toDate\":\"21.09.2011\", \"membershipRequired\":\"1\", \"price\":\"100\", \"priceMembers\":\"101\", \"priceLessons\":\"102\", \"priceTrain\":\"103\", \"priceYouth\":\"103\", \"max\":\"42\", \"maxDifferenceSex\":\"3\"},{\"name\":\"Foo2\", \"group\":\"bar\", \"fromDate\":\"20.09.2011\", \"toDate\":\"21.09.2011\", \"membershipRequired\":\"1\", \"price\":\"100\", \"priceMembers\":\"101\", \"priceLessons\":\"102\", \"priceTrain\":\"103\", \"priceYouth\":\"103\", \"max\":\"42\", \"maxDifferenceSex\":\"3\"}], \"html\":{\"5:2\":\"31\", \"1:0\":\"12\"}}";

        JSONObject obj = (JSONObject) JSONParser.parseStrict(json);
        return new Event(obj);
    }

    public static void activate() {

    }

    public static void deactivate() {

    }
}
