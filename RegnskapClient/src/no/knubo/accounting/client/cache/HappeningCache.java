package no.knubo.accounting.client.cache;

import java.util.Collection;
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

public class HappeningCache implements ServerResponse {
    private static HappeningCache instance;

    private final Constants constants;

    protected HashMap<String, JSONObject> dataPerId;

    private CacheCallback flushcallback;

    private final I18NAccount messages;

    public static HappeningCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new HappeningCache(constants, messages);
        }
        return instance;
    }

    private HappeningCache(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        flush(null);
    }

    public void flush(final CacheCallback callback) {
        this.flushcallback = callback;

        AuthResponder.get(constants, messages, this, "registers/happening.php?action=all");
    }

    public JSONObject getHappening(String id) {
        return dataPerId.get(id);
    }

    public Collection<JSONObject> getAll() {
        return dataPerId.values();
    }

    /**
     * Fills the box, with a blank line at the top.
     * 
     * @param box
     */
    public void fill(ListBox box) {
        box.insertItem("", 0);
        int pos = 1;
        for(JSONObject obj : dataPerId.values()) {
            box.insertItem(Util.str(obj.get("description")), Util.str(obj.get("id")), pos++);
        }
    }

    public String getLineDescription(String id) {
        JSONObject object = dataPerId.get(id);

        return Util.str(object.get("linedesc"));
    }

    public void serverResponse(JSONValue value) {
        JSONArray array = value.isArray();

        dataPerId = new HashMap<String, JSONObject>();

        for (int i = 0; i < array.size(); i++) {
            JSONValue one = array.get(i);
            JSONObject object = one.isObject();

            String id = Util.str(object.get("id"));
            dataPerId.put(id, object);
        }
        if (flushcallback != null) {
            flushcallback.flushCompleted();
        }
    }
}
