package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;

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

    Map<String, String> typeGivesDescription;

    List<String> originalSort;

    List<String> earnings;
    List<String> costs;
    List<String> capital;
    List<String> ownings;
    List<String> deprecations;
    List<String> bank;

    List<String> memberPaymentPosts;

    public static PosttypeCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new PosttypeCache(constants, messages);
        }
        return instance;
    }

    private PosttypeCache(Constants constants, I18NAccount messages) {

        ServerResponse handlerTypes = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue jsonValue) {
                JSONArray array = jsonValue.isArray();

                typeGivesDescription = new HashMap<String, String>();
                originalSort = new ArrayList<String>();
                earnings = new ArrayList<String>();
                costs = new ArrayList<String>();
                capital = new ArrayList<String>();
                ownings = new ArrayList<String>();
                deprecations = new ArrayList<String>();
                bank = new ArrayList<String>();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    String key = Util.str(obj.get("PostType"));
                    typeGivesDescription.put(key, Util.str(obj.get("Description")));

                    if (Util.getBoolean(obj.get("Capital"))) {
                        capital.add(key);
                    }
                    if (Util.getBoolean(obj.get("Earning"))) {
                        earnings.add(key);
                    }
                    if (Util.getBoolean(obj.get("Cost"))) {
                        costs.add(key);
                    }
                    if (Util.getBoolean(obj.get("Owning"))) {
                        ownings.add(key);
                    }

                    if (Util.getBoolean(obj.get("Deprecation"))) {
                        deprecations.add(key);
                    }
                    
                    if(Util.getBoolean(obj.get("Bank"))) {
                        bank.add(key);
                    }

                    originalSort.add(key);
                }
            }

        };

        AuthResponder.get(constants, messages, handlerTypes, "registers/posttypes.php?action=all");

        ServerResponse handlerMembershipPayment = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue value) {
                JSONArray array = value.isArray();

                memberPaymentPosts = new ArrayList<String>();

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
        if(typeGivesDescription == null) {
            return "...laster";
        }
        return typeGivesDescription.get(type);
    }

    public String getDescriptionWithType(String type) {
        return type + " - " + typeGivesDescription.get(type);
    }

    private void simpleFill(ListBox box, List<String> list, boolean ...includeKeySkipBlank) {
        if (includeKeySkipBlank != null && includeKeySkipBlank.length > 1 && includeKeySkipBlank[1]) {
            /* Nothing */
        } else {
            box.insertItem("", 0);
        }

        int pos = 1;
        for (String k : list) {

            String desc = typeGivesDescription.get(k);

            if (includeKeySkipBlank != null && includeKeySkipBlank.length > 0 && includeKeySkipBlank[0]) {
                desc = k + " " + desc;
            }
            box.insertItem(desc, k, pos++);
        }
    }

    public void fillAllCost(ListBox boxError) {
        simpleFill(boxError, costs);
    }

    public void fillAllOwnings(ListBox box) {
        simpleFill(box, ownings);
    }

    public void fillBank(ListBox box) {
        simpleFill(box, bank, true, true);
    }
    
    public void fillAllDeprecations(ListBox box) {
        simpleFill(box, deprecations);
    }

    public void fillAllEarnings(ListBox boxError) {
        simpleFill(boxError, earnings);
    }

    public void fillAllCapital(ListBox boxError) {
        simpleFill(boxError, capital);
    }

    public void fillAllPosts(ListBox box) {
        fillAllPosts(box, null, true, false);
    }

    public void fillMembershipPayments(ListBox box) {
        int pos = 1;
        for (String k : memberPaymentPosts) {

            String desc = typeGivesDescription.get(k);

            box.insertItem(desc, k, pos++);
        }
    }

    @Override
    public boolean keyExists(String key) {
        return typeGivesDescription.containsKey(key);
    }

    public void fillAllPosts(ListBox box, ListBox excludeBox, boolean addBlanc, boolean includeKey) {

        HashSet<String> excludeKeys = setUpExcludeKeys(excludeBox);

        if (addBlanc) {
            box.insertItem("", 0);
        }
        int pos = 1;
        for (String k : originalSort) {
            if (excludeKeys.contains(k)) {
                continue;
            }

            String desc = typeGivesDescription.get(k);

            if (includeKey) {
                desc = k + " " + desc;
            }
            box.insertItem(desc, k, pos++);
        }

    }

    private HashSet<String> setUpExcludeKeys(ListBox excludeBox) {
        if (excludeBox == null) {
            return new HashSet<String>();
        }
        HashSet<String> hs = new HashSet<String>();

        for (int i = 0; i < excludeBox.getItemCount(); i++) {
            hs.add(excludeBox.getValue(i));
        }
        return hs;
    }

    public void fillAllPosts(ListBoxWithErrorText box) {
        fillAllPosts(box.getListbox());
    }

    public void addPost(ListBox box, String k, boolean includeKey) {
        String desc = typeGivesDescription.get(k);

        if (includeKey) {
            desc = k + " " + desc;
        }
        box.addItem(desc, k);
    }
}
