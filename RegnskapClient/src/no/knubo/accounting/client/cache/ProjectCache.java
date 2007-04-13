package no.knubo.accounting.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.ListBox;

public class ProjectCache implements ResponseTextHandler {

	private static ProjectCache instance;

	public static ProjectCache getInstance(Constants constants) {
		if (instance == null) {
			instance = new ProjectCache(constants);
		}
		return instance;
	}


	private HashMap projectGivesDesc;
	private ArrayList originalSort;

	private ProjectCache(Constants constants) {
		if (!HTTPRequest.asyncGet(constants.baseurl()
				+ "registers/projects.php", this)) {
		}
	}

	public void onCompletion(String responseText) {
		JSONValue jsonValue = JSONParser.parse(responseText);
		JSONArray array = jsonValue.isArray();

		projectGivesDesc = new HashMap();
		originalSort = new ArrayList();
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.get(i).isObject();

			String key = Util.str(obj.get("project"));
			projectGivesDesc.put(key, Util
					.str(obj.get("description")));
			originalSort.add(key);
		}
	}


	/**
	 * Fills the box with projects, adds blank as first choice.
	 * @param box
	 */
	public void fill(ListBox box) {
		box.insertItem("", 0);
		int pos = 1;
		for (Iterator i = originalSort.iterator(); i.hasNext();) {
			String k = (String) i.next();
			
			String desc = (String) projectGivesDesc.get(k);
			
			box.insertItem(desc, k, pos++);
		}
	}

	public String getName(String id) {
		return (String) projectGivesDesc.get(id);
	}
}
