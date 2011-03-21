package no.knubo.accounting.client.views.ownings;

import com.google.gwt.json.client.JSONObject;

public interface OwningChange {

    void deleteExecuted(JSONObject data);

    void changeExecuted(JSONObject data);

}
