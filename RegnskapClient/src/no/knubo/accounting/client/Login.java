package no.knubo.accounting.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Login implements EntryPoint, ClickListener, ResponseTextHandler {

	private PasswordTextBox passBox;

	private Constants constants;

	private HTML infoLabel;

	private I18NAccount messages;

	private TextBox userBox;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		messages = (I18NAccount) GWT.create(I18NAccount.class);
		constants = (Constants) GWT.create(Constants.class);

		Grid grid = new Grid(4, 2);

		Button loginButton = new Button(messages.login());
		loginButton.addClickListener(this);

		userBox = new TextBox();
		passBox = new PasswordTextBox();
		infoLabel = new HTML();

		grid.setText(0, 0, messages.user());
		grid.setWidget(0, 1, userBox);
		grid.setText(1, 0, messages.password());
		grid.setWidget(1, 1, passBox);
		grid.setWidget(2, 1, loginButton);
		grid.setWidget(3, 1, infoLabel);
		RootPanel.get().add(grid);
	}

	public void onClick(Widget sender) {
		String user = this.userBox.getText();
		String password = this.passBox.getText();
		if (!HTTPRequest.asyncGet(this.constants.baseurl()
				+ "authenticate.php?user=" + user + "&password=" + password,
				this)) {
			infoLabel.setText(messages.failedLogin());
		}
	}

	public void onCompletion(String responseText) {
		JSONValue jsonValue = JSONParser.parse(responseText);

		JSONObject isObject = jsonValue.isObject();

		JSONValue error = isObject.get("error");

		if(error != null) {
			JSONString string = error.isString();
			infoLabel.setText(string.stringValue());
		} else {
			JSONValue url = isObject.get("url");
			JSONString string = url.isString();

			Util.forward(string.stringValue());
		}
	}

}
