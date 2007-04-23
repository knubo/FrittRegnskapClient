package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * This class does lazy loading of the various post types that the system has.
 * 
 * @author knuterikborgen
 * 
 */
public class PosttypeCache implements ResponseTextHandler, Registry {

	private static PosttypeCache instance;

	Map typeGivesDescription;
	List originalSort;

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
		originalSort = new ArrayList();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.get(i).isObject();

			String key = Util.str(obj.get("PostType"));
			typeGivesDescription.put(key, Util
					.str(obj.get("Description")));
			originalSort.add(key);
		}
	}
	
	public String getDescription(String type) {
		return (String) typeGivesDescription.get(type);
	}
	
	public void fill(ListBox box) {
		box.insertItem("", 0);
		int pos = 1;
		for (Iterator i = originalSort.iterator(); i.hasNext();) {
			String k = (String) i.next();
			
			String desc = (String) typeGivesDescription.get(k);
			
			box.insertItem(desc, k, pos++);
		}
	}

	public boolean keyExists(String key) {
		return typeGivesDescription.containsKey(key);
	}
}
