package no.knubo.accounting.client.cache;

import java.util.HashMap;
import java.util.Map;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;

/**
 * This class does lazy loading of the various post types that the system has.
 * 
 * @author knuterikborgen
 * 
 */
public class PosttypeCache implements ResponseTextHandler {

	private static PosttypeCache instance;

	Map typeGivesDescription;

	public static PosttypeCache getInstance(Constants constants) {
		if (instance == null) {
			instance = new PosttypeCache(constants);
		}
		return instance;
	}

	private PosttypeCache(Constants constants) {
		if (!HTTPRequest.asyncGet(constants.baseurl()
				+ "registers/posttypes.php", this)) {
		}
	}

	public void onCompletion(String responseText) {
		JSONValue jsonValue = JSONParser.parse(responseText);
		JSONArray array = jsonValue.isArray();

		typeGivesDescription = new HashMap();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.get(i).isObject();

			typeGivesDescription.put(Util.str(obj.get("PostType")), Util
					.str(obj.get("Description")));
		}
	}
	
	public String getDescription(String type) {
		return (String) typeGivesDescription.get(type);
	}
}
