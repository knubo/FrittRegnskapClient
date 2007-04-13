package no.knubo.accounting.client.cache;

import no.knubo.accounting.client.Constants;

import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;

public class ProjectCache implements ResponseTextHandler {

	private static ProjectCache instance;

	public static ProjectCache getInstance(Constants constants) {
		if (instance == null) {
			instance = new ProjectCache(constants);
		}
		return instance;
	}

	private ProjectCache(Constants constants) {
		if (!HTTPRequest.asyncGet(constants.baseurl()
				+ "registers/posttypes.php", this)) {
		}
	}

	public void onCompletion(String responseText) {

	}

}
