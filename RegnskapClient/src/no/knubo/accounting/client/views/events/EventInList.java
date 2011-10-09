package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class EventInList {

    static int i = 42;
    private JSONObject data;

    public EventInList() {
        data = new JSONObject();

        data.put("id", new JSONNumber(42));
        data.put("name", new JSONString("Winter Jump"));
        data.put("startDate", new JSONString("20.09.2011"));
        data.put("endDate", new JSONString("30.09.2011"));
        data.put("eventDate", new JSONString("01.10.2011"));
        data.put("maxPeople", new JSONNumber(42));
    }

    public EventInList(JSONObject data) {
        this.data = data;

    }

    public String getName() {
        return Util.str(data.get("name"));
    }

    public String getStartDate() {
        return Util.str(data.get("startDate"));
    }

    public String getEndDate() {
        return Util.str(data.get("endDate"));
    }

    public String getEventDate() {
        return Util.str(data.get("eventDate"));
    }

    public String getMaxPeople() {
        return Util.str(data.get("maxPeople"));
    }

    public String getId() {
        return String.valueOf((int) data.get("id").isNumber().doubleValue());
    }

    public void setName(String text) {
        data.put("name", new JSONString(text));
    }

    public void setStartDate(String date) {
        data.put("startDate", new JSONString(date));
    }

    public void setStopDate(String date) {
        data.put("stopDate", new JSONString(date));
    }

    public void setEventDate(String date) {
        data.put("eventDate", new JSONString(date));
    }

    public void setMaxPeople(String number) {
        data.put("maxPeople", new JSONNumber(Double.parseDouble(number)));
    }

    public JSONObject getAsJSON() {
        return data;
    }

}
