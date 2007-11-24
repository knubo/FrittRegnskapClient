package no.knubo.accounting.client.cache;

import java.util.ArrayList;
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

/**
 * Cache for MonthHeaders - this is the account collection posts for month view.
 */
public class MonthHeaderCache implements ServerResponse {

    private static MonthHeaderCache instance;

    List headersByName;
    List keys;

    public static MonthHeaderCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new MonthHeaderCache(constants, messages);
        }
        return instance;
    }

    private MonthHeaderCache(Constants constants, I18NAccount messages) {

        AuthResponder.get(constants, messages, instance, "registers/monthcolumns.php");
    }

    public void serverResponse(JSONValue jsonValue) {

        JSONArray array = jsonValue.isArray();
        headersByName = new ArrayList();
        keys = new ArrayList();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();

            keys.add(Util.str(obj.get("Id")));
            headersByName.add(Util.str(obj.get("Name")));
        }
    }

    public String getDescription(String id) {
        Iterator idIt = keys.iterator();
        Iterator descIt = headersByName.iterator();

        while (idIt.hasNext()) {
            String elem = (String) idIt.next();
            String desc = (String) descIt.next();

            if (elem.equals(id)) {
                return desc;
            }
        }

        return "";
    }

    /**
     * All headers in display order.
     * 
     * @return List of Strings.
     */
    public List headers() {
        return headersByName;
    }

    /**
     * Returns the keys of the headers.
     * 
     * @return The list of keys.
     */
    public List keys() {
        return keys;
    }

    public void fill(ListBox box) {
        box.addItem("");

        Iterator idIt = keys.iterator();
        Iterator descIt = headersByName.iterator();

        while (idIt.hasNext()) {
            String elem = (String) idIt.next();
            String desc = (String) descIt.next();

            box.addItem(desc, elem);
        }
    }

}
