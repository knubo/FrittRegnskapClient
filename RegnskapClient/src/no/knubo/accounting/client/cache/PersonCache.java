package no.knubo.accounting.client.cache;

import no.knubo.accounting.client.Constants;

import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;

public class PersonCache implements ResponseTextHandler {

	private static PersonCache instance;

	public static PersonCache getInstance(Constants constants) {
		if (instance == null) {
			instance = new PersonCache(constants);
		}
		return instance;
	}

	private PersonCache(Constants constants) {
		if (!HTTPRequest.asyncGet(constants.baseurl()
				+ "registers/posttypes.php", this)) {
		}
	}

	public void onCompletion(String responseText) {
		// TODO Auto-generated method stub

	}

}
