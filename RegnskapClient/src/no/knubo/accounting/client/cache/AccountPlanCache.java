package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class AccountPlanCache implements ServerResponse {

    private static AccountPlanCache instance;

    private final Constants constants;

    private final I18NAccount messages;

    private HashMap objectGivesId;

    private List keys;

    public AccountPlanCache(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        objectGivesId = new HashMap();
        keys = new ArrayList();
        flush();
    }

    public static AccountPlanCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new AccountPlanCache(constants, messages);
        }
        return instance;
    }

    public void serverResponse(JSONValue value) {
        JSONArray array = value.isArray();

        for (int i = 0; i < array.size(); i++) {
            JSONValue arrVar = array.get(i);
            JSONObject object = arrVar.isObject();

            String id = Util.str(object.get("id"));

            keys.add(id);
            objectGivesId.put(id, object);
        }
    }

    public void flush() {
        objectGivesId.clear();
        keys.clear();

        AuthResponder.get(constants, messages, this, "registers/accountplan.php");
    }

    public String idGivesName(String id) {
        JSONObject obj = (JSONObject) objectGivesId.get(id);
        if (obj == null) {
            return "";
        }
        return Util.str(obj.get("name"));
    }

    public void fill(ListBox box) {
        box.addItem("");
        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = (String) i.next();
            String name = idGivesName(key);

            box.addItem(name, key);
        }
    }

}
