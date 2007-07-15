package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

public class CountCache implements ServerResponse {

    private ArrayList counts;

    private HashMap countGivesColumn;

    private static CountCache instance;

    private final Constants constants;

    public CountCache(Constants constants) {
        this.constants = constants;
        flush();
    }

    public static CountCache getInstance(Constants constants) {
        if (instance == null) {
            instance = new CountCache(constants);
        }
        return instance;
    }

    public void serverResponse(String responseText) {
        JSONValue value = JSONParser.parse(responseText);

        JSONObject object = value.isObject();

        JSONValue values = object.get("values");
        JSONValue columnValue = object.get("columns");
        JSONArray arrayValues = values.isArray();
        JSONArray arrayColumns = columnValue.isArray();

        for (int i = 0; i < arrayValues.size(); i++) {
            JSONValue itvalue = arrayValues.get(i);
            JSONValue itcolumn = arrayColumns.get(i);

            String countValue = Util.str(itvalue);
            counts.add(countValue);
            countGivesColumn.put(countValue, Util.str(itcolumn));
        }
    }

    public void flush() {
        counts = new ArrayList();
        countGivesColumn = new HashMap();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/count.php");

        try {
            builder.sendRequest("", new AuthResponder(constants, this));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    public List getCounts() {
        return counts;
    }

    public String getFieldForCount(String count) {
        return (String) countGivesColumn.get(count);
    }

}
