package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.google.gwt.user.client.ui.ListBox;

public class ProjectCache implements Registry {

    private static ProjectCache instance;

    public static ProjectCache getInstance(Constants constants) {
        if (instance == null) {
            instance = new ProjectCache(constants);
        }
        return instance;
    }

    private HashMap projectGivesDesc;

    private ArrayList originalSort;

    private ArrayList allObjects;

    private final Constants constants;

    private ProjectCache(Constants constants) {
        this.constants = constants;
        flush(null);
    }

    public void flush(final CacheCallback flushcallback) {
        ServerResponse resphandler = new ServerResponse() {
            public void serverResponse(String responseText) {
                JSONValue jsonValue = JSONParser.parse(responseText);
                JSONArray array = jsonValue.isArray();

                projectGivesDesc = new HashMap();
                originalSort = new ArrayList();
                allObjects = new ArrayList();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    allObjects.add(obj);
                    String key = Util.str(obj.get("project"));
                    projectGivesDesc.put(key, Util.str(obj.get("description")));
                    originalSort.add(key);
                }
                if (flushcallback != null) {
                    flushcallback.flushCompleted();
                }
            }

        };

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/projects.php?action=all");

        try {
            builder.sendRequest("", new AuthResponder(constants, resphandler));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    /**
     * Fills the box with projects, adds blank as first choice.
     * 
     * @param box
     */
    public void fill(ListBox box) {
        box.insertItem("", 0);
        int pos = 1;
        for (Iterator i = originalSort.iterator(); i.hasNext();) {
            String k = (String) i.next();

            String desc = (String) projectGivesDesc.get(k);

            box.insertItem(desc, k, pos++);
        }
    }

    public String getName(String id) {
        return (String) projectGivesDesc.get(id);
    }

    public boolean keyExists(String key) {
        return projectGivesDesc.containsKey(key);
    }

    public List getAll() {
        return allObjects;
    }

}
