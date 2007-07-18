package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.List;

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

/**
 * This class does lazy loading of the various post types that the system has.
 * 
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
	    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/monthcolumns.php");

        try {
            builder.sendRequest("", new AuthResponder(constants, messages, this));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
	}

	public void serverResponse(String responseText) {
		JSONValue jsonValue = JSONParser.parse(responseText);
		JSONArray array = jsonValue.isArray();
		headersByName = new ArrayList();
		keys = new ArrayList();

		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.get(i).isObject();

			keys.add(Util.str(obj.get("Id")));
			headersByName.add(Util.str(obj.get("Name")));
		}
	}
	
	/**
	 * All headers in display order.
	 * @return List of Strings.
	 */
	public List headers() {
		return headersByName;
	}
	
	/**
	 * Returns the keys of the headers.
	 * @return The list of keys.
	 */
	public List keys() {
		return keys;
	}
	

}
