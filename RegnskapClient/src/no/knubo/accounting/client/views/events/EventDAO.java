package no.knubo.accounting.client.views.events;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class EventDAO {

    public static List<EventInList> getEvents() {
        return Arrays.asList(new EventInList(), new EventInList());
    }

    public static Event getEvent(String id) {

        String json = "{\"id\":"
                + id
                + ", \"name\":\"Winter Jump\", \"startDate\":\"20.09.2011\", \"endDate\":\"30.09.2011\", \"eventDate\":\"01.10.2011\", \"maxPeople\":4242, \"stopDate\":\"30.09.2011\", \"groups\":{\"bar\":{\"col\":2, \"row\":4}, \"bar2\":{\"col\":3, \"row\":6}}, \"choices\":[{\"name\":\"Foo\", \"group\":\"bar2\", \"fromDate\":\"20.09.2011\", \"toDate\":\"21.09.2011\", \"membershipRequired\":\"1\", \"price\":\"100\", \"priceMembers\":\"101\", \"priceLessons\":\"102\", \"priceTrain\":\"103\", \"priceYouth\":\"103\", \"max\":\"42\", \"maxDifferenceSex\":\"3\"},{\"name\":\"Foo\", \"group\":\"bar\", \"fromDate\":\"20.09.2011\", \"toDate\":\"21.09.2011\", \"membershipRequired\":\"1\", \"price\":\"100\", \"priceMembers\":\"101\", \"priceLessons\":\"102\", \"priceTrain\":\"103\", \"priceYouth\":\"103\", \"max\":\"42\", \"maxDifferenceSex\":\"3\"},{\"name\":\"Foo2\", \"group\":\"bar\", \"fromDate\":\"20.09.2011\", \"toDate\":\"21.09.2011\", \"membershipRequired\":\"1\", \"price\":\"100\", \"priceMembers\":\"101\", \"priceLessons\":\"102\", \"priceTrain\":\"103\", \"priceYouth\":\"103\", \"max\":\"42\", \"maxDifferenceSex\":\"3\"}], \"html\":{\"5:2\":\"31\", \"1:0\":\"12\"}}";

        JSONObject obj = (JSONObject) JSONParser.parseStrict(json);
        return new Event(obj);
    }
}
