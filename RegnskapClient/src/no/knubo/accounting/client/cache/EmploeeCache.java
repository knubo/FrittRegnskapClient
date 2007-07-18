package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
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
import com.google.gwt.user.client.ui.ListBox;

public class EmploeeCache implements ServerResponse {

    private HashMap personById;

    private ArrayList keys;

    private static EmploeeCache instance;

    private final Constants constants;

    private final I18NAccount messages;


    public static EmploeeCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new EmploeeCache(constants, messages);
        }
        return instance;
    }

    private EmploeeCache(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        flush();
    }

    public void serverResponse(String responseText) {
        JSONValue jsonValue = JSONParser.parse(responseText);
        JSONArray array = jsonValue.isArray();
        personById = new HashMap();
        keys = new ArrayList();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();

            String key = Util.str(obj.get("id"));
            keys.add(key);
            personById.put(key, Util.str(obj.get("lastname")) + ", "
                    + Util.str(obj.get("firstname")));
        }
    }

    /**
     * Fills the box, with a blank line at the top.
     * 
     * @param box
     */
    public void fill(ListBox box) {
        box.insertItem("", 0);
        int pos = 1;
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String k = (String) i.next();

            String desc = (String) personById.get(k);

            box.insertItem(desc, k, pos++);
        }
    }

    public String getName(String id) {
        return (String) personById.get(id);
    }

    public void flush() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/persons.php?action=all&onlyemp=1");

        try {
            builder.sendRequest("", new AuthResponder(constants, messages, this));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }
}
