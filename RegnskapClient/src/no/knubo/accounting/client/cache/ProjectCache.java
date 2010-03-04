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
import com.google.gwt.user.client.ui.ListBox;

public class ProjectCache implements Registry {

    private static ProjectCache instance;

    public static ProjectCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new ProjectCache(constants, messages);
        }
        return instance;
    }

    private HashMap<String, String> projectGivesDesc;
    private HashMap<String, String> descGivesProject;

    private ArrayList<String> originalSort;

    private ArrayList<JSONObject> allObjects;

    private final Constants constants;

    private final I18NAccount messages;

    private ProjectCache(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        flush(null);
    }

    public void flush(final CacheCallback flushcallback) {
        ServerResponse resphandler = new ServerResponse() {
            public void serverResponse(JSONValue jsonValue) {
                JSONArray array = jsonValue.isArray();

                projectGivesDesc = new HashMap<String, String>();
                descGivesProject = new HashMap<String, String>();
                originalSort = new ArrayList<String>();
                allObjects = new ArrayList<JSONObject>();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    allObjects.add(obj);
                    String key = Util.str(obj.get("project"));
                    String description = Util.str(obj.get("description"));
                    
                    projectGivesDesc.put(key, description);
                    descGivesProject.put(description, key);
                    originalSort.add(key);
                }
                if (flushcallback != null) {
                    flushcallback.flushCompleted();
                }
            }

        };

        AuthResponder.get(constants, messages, resphandler, "registers/projects.php?action=all");
    }

    /**
     * Fills the box with projects, adds blank as first choice.
     * 
     * @param box
     */
    public void fill(ListBox box) {
        box.insertItem("", 0);
        int pos = 1;
        
        for (String k : originalSort) {
            String desc = projectGivesDesc.get(k);

            box.insertItem(desc, k, pos++);
        }
    }

    public String getName(String id) {
        return projectGivesDesc.get(id);
    }

    public String getId(String description) {
        return descGivesProject.get(description);
    }
    
    public boolean keyExists(String key) {
        return projectGivesDesc.containsKey(key);
    }

    public List<JSONObject> getAll() {
        return allObjects;
    }

}
