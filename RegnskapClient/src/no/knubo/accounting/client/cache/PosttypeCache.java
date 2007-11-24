package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * This class does lazy loading of the various post types that the system has.
 * 
 * @author knuterikborgen
 * 
 */
public class PosttypeCache implements Registry {

    private static PosttypeCache instance;

    Map typeGivesDescription;

    List originalSort;

    List memberPaymentPosts;

    public static PosttypeCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new PosttypeCache(constants, messages);
        }
        return instance;
    }

    private PosttypeCache(Constants constants, I18NAccount messages) {

        ServerResponse handlerTypes = new ServerResponse() {

            public void serverResponse(JSONValue jsonValue) {
                JSONArray array = jsonValue.isArray();

                typeGivesDescription = new HashMap();
                originalSort = new ArrayList();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    String key = Util.str(obj.get("PostType"));
                    typeGivesDescription.put(key, Util.str(obj.get("Description")));

                    originalSort.add(key);
                }
            }

        };

        AuthResponder.get(constants, messages, handlerTypes, "registers/posttypes.php?action=all");

        ServerResponse handlerMembershipPayment = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONArray array = value.isArray();

                memberPaymentPosts = new ArrayList();

                for (int i = 0; i < array.size(); i++) {
                    JSONValue elem = array.get(i);

                    memberPaymentPosts.add(Util.str(elem));
                }
            }

        };

        AuthResponder.get(constants, messages, handlerMembershipPayment,
                "defaults/post_defaults.php?selection=membershippayment");

    }

    public String getDescription(String type) {
        return (String) typeGivesDescription.get(type);
    }

    public String getDescriptionWithType(String type) {
        return type + " - " + typeGivesDescription.get(type);
    }

    public void fillAllPosts(ListBox box) {
        fillAllPosts(box, null, true, false);
    }

    public void fillMembershipPayments(ListBox box) {
        int pos = 1;
        for (Iterator i = memberPaymentPosts.iterator(); i.hasNext();) {
            String k = (String) i.next();

            String desc = (String) typeGivesDescription.get(k);

            box.insertItem(desc, k, pos++);
        }
    }

    public boolean keyExists(String key) {
        return typeGivesDescription.containsKey(key);
    }

    public void fillAllPosts(ListBox box, ListBox excludeBox, boolean addBlanc, boolean includeKey) {

        HashSet excludeKeys = setUpExcludeKeys(excludeBox);

        if (addBlanc) {
            box.insertItem("", 0);
        }
        int pos = 1;
        for (Iterator i = originalSort.iterator(); i.hasNext();) {
            String k = (String) i.next();

            if (excludeKeys.contains(k)) {
                continue;
            }

            String desc = (String) typeGivesDescription.get(k);

            if (includeKey) {
                desc = k + " " + desc;
            }
            box.insertItem(desc, k, pos++);
        }

    }

    private HashSet setUpExcludeKeys(ListBox excludeBox) {
        if (excludeBox == null) {
            return new HashSet();
        }
        HashSet hs = new HashSet(excludeBox.getItemCount());

        for (int i = 0; i < excludeBox.getItemCount(); i++) {
            hs.add(excludeBox.getValue(i));
        }
        return hs;
    }
}
