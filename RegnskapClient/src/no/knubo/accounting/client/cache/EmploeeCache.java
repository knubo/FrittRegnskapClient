package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.ListBox;

public class EmploeeCache implements ServerResponse {

    private HashMap<String, String> personById;

    private ArrayList<String> keys;

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

    public void serverResponse(JSONValue jsonValue) {
        JSONArray array = jsonValue.isArray();
        personById = new HashMap<String, String>();
        keys = new ArrayList<String>();

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
        
        for(String k: keys) {
            String desc = personById.get(k);

            box.insertItem(desc, k, pos++);
        }
    }

    public String getName(String id) {
        return personById.get(id);
    }

    public void flush() {
        AuthResponder.get(constants, messages, this, "registers/persons.php?action=all&onlyemp=1");
    }
}
