package no.knubo.accounting.client.views;

import com.google.gwt.json.client.JSONObject;

public interface PersonPickCallback {

    void pickPerson(String id, JSONObject personObj);

}
