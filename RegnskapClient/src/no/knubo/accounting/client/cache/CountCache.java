package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class CountCache implements ServerResponse {

    private ArrayList counts;

    private HashMap countGivesColumn;

    private static CountCache instance;

    private final Constants constants;

    private final I18NAccount messages;

    public CountCache(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        flush();
    }

    public static CountCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new CountCache(constants, messages);
        }
        return instance;
    }

    public void serverResponse(JSONValue value) {
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

        AuthResponder.get(constants, messages, this, "registers/count.php");
    }

    public List getCounts() {
        return counts;
    }

    public String getFieldForCount(String count) {
        return (String) countGivesColumn.get(count);
    }

}
