package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;

public class CountCache implements ResponseTextHandler {

    private ArrayList counts;

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

    public void onCompletion(String responseText) {
        JSONValue value = JSONParser.parse(responseText);

        JSONArray array = value.isArray();

        for (int i = 0; i < array.size(); i++) {
            JSONValue itvalue = array.get(i);
            counts.add(Util.str(itvalue));
        }
    }

    public void flush() {
        counts = new ArrayList();
        if (!HTTPRequest.asyncGet(constants.baseurl() + "registers/count.php",
                this)) {
            // TODO report errors
        }
    }

    public List getCounts() {
        return counts;
    }

}
