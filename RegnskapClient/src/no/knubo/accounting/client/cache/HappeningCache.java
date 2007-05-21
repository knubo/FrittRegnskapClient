package no.knubo.accounting.client.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

public class HappeningCache {
    private static HappeningCache instance;

    private final Constants constants;

    protected HashMap dataPerId;

    public static HappeningCache getInstance(Constants constants) {
        if (instance == null) {
            instance = new HappeningCache(constants);
        }
        return instance;
    }

    private HappeningCache(Constants constants) {
        this.constants = constants;
        flush(null);
    }

    public void flush(final CacheCallback flushcallback) {

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "registers/happening.php");

        RequestCallback callback = new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                Window.alert(exception.getMessage());
            }

            public void onResponseReceived(Request request, Response response) {
                JSONValue value = JSONParser.parse(response.getText());
                JSONArray array = value.isArray();

                dataPerId = new HashMap();

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

        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest("action=all", callback);
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    public JSONObject getHappening(String id) {
        return (JSONObject) dataPerId.get(id);
    }

    public Collection getAll() {
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
        for (Iterator i = dataPerId.values().iterator(); i.hasNext();) {
            JSONObject obj = (JSONObject) i.next();

            box.insertItem(Util.str(obj.get("description")), Util.str(obj
                    .get("id")), pos++);
        }
    }

    public String getLineDescription(String id) {
        JSONObject object = (JSONObject) dataPerId.get(id);
        
        return Util.str(object.get("linedesc"));
    }
}
