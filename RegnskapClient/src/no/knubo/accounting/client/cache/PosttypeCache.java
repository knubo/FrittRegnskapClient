package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
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

            public void serverResponse(String responseText) {
                JSONValue jsonValue = JSONParser.parse(responseText);
                JSONArray array = jsonValue.isArray();

                typeGivesDescription = new HashMap();
                originalSort = new ArrayList();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    String key = Util.str(obj.get("PostType"));
                    typeGivesDescription.put(key, Util.str(obj
                            .get("Description")));

                    originalSort.add(key);
                }
            }

        };
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/posttypes.php?action=inuse");

        try {
            builder.sendRequest("", new AuthResponder(constants, messages, handlerTypes));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
        
        ServerResponse handlerMembershipPayment = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue value = JSONParser.parse(responseText);
                JSONArray array = value.isArray();

                memberPaymentPosts = new ArrayList();

                for (int i = 0; i < array.size(); i++) {
                    JSONValue elem = array.get(i);

                    memberPaymentPosts.add(Util.str(elem));
                }
            }

        };
        
        RequestBuilder builderMP = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "defaults/post_defaults.php?selection=membershippayment");

        try {
            builderMP.sendRequest("", new AuthResponder(constants, messages, handlerMembershipPayment));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    public String getDescription(String type) {
        return (String) typeGivesDescription.get(type);
    }

    public String getDescriptionWithType(String type) {
        return type + " - " + typeGivesDescription.get(type);
    }

    public void fillAllPosts(ListBox box) {
        box.insertItem("", 0);
        int pos = 1;
        for (Iterator i = originalSort.iterator(); i.hasNext();) {
            String k = (String) i.next();

            String desc = (String) typeGivesDescription.get(k);

            box.insertItem(desc, k, pos++);
        }
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
}
