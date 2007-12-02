package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

public class TrustActionCache {

    private static TrustActionCache instance;

    private Constants constants;
    private List allTrustActions;
    private Map trustActionsPerId;
    private JSONArray typeArray;
    private Map fondGivesName;

    private final I18NAccount messages;

    TrustActionCache(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        flush(null);
    }

    public void flush(final CacheCallback flushcallback) {
        allTrustActions = new ArrayList();
        trustActionsPerId = new HashMap();
        fondGivesName = new HashMap();

        ServerResponse resphandler = new ServerResponse() {

            public void serverResponse(JSONValue jsonValue) {
                JSONObject mainObject = jsonValue.isObject();
                JSONArray actionArray = mainObject.get("actions").isArray();

                for (int i = 0; i < actionArray.size(); i++) {
                    JSONObject obj = actionArray.get(i).isObject();

                    allTrustActions.add(obj);
                    trustActionsPerId.put(Util.str(obj.get("id")), obj);
                }

                typeArray = mainObject.get("types").isArray();

                for (int i = 0; i < typeArray.size(); i++) {
                    JSONObject obj = typeArray.get(i).isObject();
                    fondGivesName.put(Util.str(obj.get("fond")), Util.str(obj.get("description")));
                }
                if (flushcallback != null) {
                    flushcallback.flushCompleted();
                }
            }
        };

        AuthResponder.get(constants, messages, resphandler, "registers/trustaction.php?action=all");
    }

    public static TrustActionCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new TrustActionCache(constants, messages);
        }
        return instance;
    }

    public void fillTrustList(ListBox trustListBox) {
        trustListBox.addItem("", "");
        for (Iterator i = fondGivesName.entrySet().iterator(); i.hasNext();) {
            Entry entry = (Entry) i.next();

            trustListBox.addItem(entry.getValue().toString(), entry.getKey().toString());
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
                actionsBox.addItem(Util.str(obj.get("description")), Util.str(obj.get("id")));
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

    public boolean addsAccountLineUponSave(String selected) {
        JSONObject actionObj = (JSONObject) trustActionsPerId.get(selected);

        return !Util.isNull(actionObj.get("debetpost"))
                || !Util.isNull(actionObj.get("creditpost"));
    }

    public List getAll() {
        return allTrustActions;
    }

    public JSONArray getAllTrust() {
        return typeArray;
    }

    public JSONObject getTrustAction(String id) {
        return (JSONObject) trustActionsPerId.get(id);
    }

    public String trustGivesDesc(String trust) {
        return (String) fondGivesName.get(trust);
    }

}
