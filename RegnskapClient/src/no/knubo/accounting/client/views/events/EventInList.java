package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class EventInList {

    static int i = 42;
    protected JSONObject data;

    public EventInList() {
        data = new JSONObject();

        data.put("id", new JSONString(""));
        data.put("name", new JSONString(""));
        data.put("startDate", new JSONString(""));
        data.put("endDate", new JSONString(""));
        data.put("eventDate", new JSONString(""));
        data.put("eventEndDate", new JSONString(""));
        data.put("maxPeople", new JSONNumber(0));
        data.put("active", new JSONString("0"));
    }

    public EventInList(JSONObject data) {
        this.data = data;

    }

    public String getName() {
        return Util.str(data.get("name"));
    }

    public String getStartDate() {
        return Util.strSkipNull(data.get("startDate"));
    }

    public String getEndDate() {
        return Util.strSkipNull(data.get("endDate"));
    }

    public String getEventDate() {
        return Util.strSkipNull(data.get("eventDate"));
    }

    public String getEventEndDate() {
        return Util.strSkipNull(data.get("eventEndDate"));
    }

    public String getMaxPeople() {
        return Util.str(data.get("maxPeople"));
    }

    public String getId() {
        return String.valueOf((int) data.get("id").isNumber().doubleValue());
    }

    public void setId(String str) {
        data.put("id", new JSONNumber(Double.parseDouble(str)));
    }

    public void setName(String text) {
        data.put("name", new JSONString(text));
    }

    public void setStartDate(String date) {
        data.put("startDate", new JSONString(date));
    }

    public void setEndDate(String date) {
        data.put("endDate", new JSONString(date));
    }

    public void setEventDate(String date) {
        data.put("eventDate", new JSONString(date));
    }

    public void setEventEndDate(String text) {
        data.put("eventEndDate", new JSONString(text));
    }

    public void setMaxPeople(String number) {
        data.put("maxPeople", new JSONNumber(Double.parseDouble(number)));
    }

    public boolean isActive() {
        return Util.getBoolean(data.get("active"));
    }

    public void setActive(boolean active) {
        data.put("active", new JSONString(active ? "1" : "0"));
    }

    public JSONObject getAsJSON() {
        return data;
    }



}
