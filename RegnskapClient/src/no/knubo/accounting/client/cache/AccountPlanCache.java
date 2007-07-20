package no.knubo.accounting.client.cache;

import java.util.HashMap;

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

public class AccountPlanCache implements ServerResponse {

    private static AccountPlanCache instance;

    private final Constants constants;

    private final I18NAccount messages;

    private HashMap objectGivesId;
    
    public AccountPlanCache(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        objectGivesId = new HashMap();
        flush();
    }

    public static AccountPlanCache getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new AccountPlanCache(constants, messages);
        }
        return instance;
    }

    public void serverResponse(String responseText) {
        JSONValue value = JSONParser.parse(responseText);
        JSONArray array = value.isArray();
        
        for(int i = 0; i < array.size(); i++) {
            JSONValue arrVar = array.get(i);
            JSONObject object = arrVar.isObject();
            
            String id = Util.str(object.get("id"));
            
            objectGivesId.put(id, object);
        }
    }

    public void flush() {
        objectGivesId.clear();
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/accountplan.php");

        try {
            builder.sendRequest("", new AuthResponder(constants, messages, this));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }
    
    public String idGivesName(String id) {
        JSONObject obj = (JSONObject) objectGivesId.get(id);
        if(obj == null) {
            return "";
        }
        return Util.str(obj.get("name"));
    }

}
