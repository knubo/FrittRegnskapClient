package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

public class TrustActionCache {

    private static TrustActionCache instance;

    private Constants constants;

    private List allTrustActions;

    private Map trustActionsPerId;

    private Map fondGivesName;

    TrustActionCache(Constants constants) {
        this.constants = constants;
        flush();
    }

    public void flush() {
        allTrustActions = new ArrayList();
        trustActionsPerId = new HashMap();
        fondGivesName = new HashMap();

        ResponseTextHandler resphandler = new ResponseTextHandler() {
            public void onCompletion(String responseText) {
                JSONValue jsonValue = JSONParser.parse(responseText);
                JSONObject mainObject = jsonValue.isObject();
                JSONArray actionArray = mainObject.get("actions").isArray();

                for (int i = 0; i < actionArray.size(); i++) {
                    JSONObject obj = actionArray.get(i).isObject();

                    allTrustActions.add(obj);
                    trustActionsPerId.put(Util.str(obj.get("id")), obj);
                }

                JSONArray typeArray = mainObject.get("types").isArray();

                for (int i = 0; i < typeArray.size(); i++) {
                    JSONObject obj = typeArray.get(i).isObject();
                    fondGivesName.put(Util.str(obj.get("fond")), Util.str(obj
                            .get("description")));
                }

            }
        };
        if (!HTTPRequest.asyncGet(constants.baseurl()
                + "registers/trustaction.php?action=all", resphandler)) {
            // TODO report errors
        }
    }

    public static TrustActionCache getInstance(Constants constants) {
        if (instance == null) {
            instance = new TrustActionCache(constants);
        }
        return instance;
    }

    public void fillTrustList(ListBox trustListBox) {
        trustListBox.addItem("", "");
        for (Iterator i = fondGivesName.entrySet().iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();

            trustListBox.addItem(entry.getValue().toString(), entry.getKey()
                    .toString());
        }
    }

    public void fillActionList(ListBox actionsBox, String selectedFond) {
        actionsBox.clear();

        if (selectedFond.length() == 0) {
            return;
        }

        actionsBox.addItem("", "");

        for (Iterator i = allTrustActions.iterator(); i.hasNext();) {
            JSONObject obj = (JSONObject) i.next();
            
            if (Util.str(obj.get("fond")).equals(selectedFond)) {
                actionsBox.addItem(Util.str(obj.get("description")), Util
                        .str(obj.get("id")));
            }
        }
    }

    public void fillDefaultDesc(TextBoxWithErrorText descBox, String selected) {
        JSONObject actionObj = (JSONObject) trustActionsPerId.get(selected);

        if (actionObj == null) {
            Window.alert("Failed to find action obj " + selected);
            return;
        }

        
        descBox.setText(Util.str(actionObj.get("defaultdesc")));
    }
}
