package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.List;

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
public class MonthHeaderCache implements ResponseTextHandler {

	private static MonthHeaderCache instance;

	List headersByName;

	public static MonthHeaderCache getInstance(Constants constants) {
		if (instance == null) {
			instance = new MonthHeaderCache(constants);
		}
		return instance;
	}

	private MonthHeaderCache(Constants constants) {
		if (!HTTPRequest.asyncGet(constants.baseurl()
				+ "registers/monthcolumns.php", this)) {
		}
	}

	public void onCompletion(String responseText) {
		JSONValue jsonValue = JSONParser.parse(responseText);


		JSONArray array = jsonValue.isArray();
		headersByName = new ArrayList();

		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.get(i).isObject();

			headersByName.add(Util.str(obj.get("Name")));
		}
	}
	
	public List headers() {
		return headersByName;
	}

}
