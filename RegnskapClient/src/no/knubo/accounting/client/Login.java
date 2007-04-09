package no.knubo.accounting.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Login implements EntryPoint, ClickListener, ResponseTextHandler {

	private TextBox userBox;

	private PasswordTextBox passBox;

	private Constants constants;

	private Label infoLabel;

	private I18NAccount msgs;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
	    msgs = (I18NAccount) GWT.create(I18NAccount.class);
		constants = (Constants) GWT.create(Constants.class);

		Grid grid = new Grid(4, 2);

		Button loginButton = new Button(msgs.login());
		loginButton.addClickListener(this);

		userBox = new TextBox();
		passBox = new PasswordTextBox();
		infoLabel = new Label();
		
		grid.setText(0, 0, msgs.user());
		grid.setWidget(0, 1, userBox);
		grid.setText(1, 0, msgs.password());
		grid.setWidget(1, 1, passBox);
		grid.setWidget(2, 1, loginButton);
		grid.setWidget(3, 1, infoLabel);
		RootPanel.get().add(grid);
	}

	public void onClick(Widget sender) {
		String user = this.userBox.getText();
		String password = this.passBox.getText();
		if(!HTTPRequest.asyncGet(this.constants.baseurl()
				+ "authenticate.php?user=" + user + "&password=" + password,
				this)) {
			infoLabel.setText(msgs.failedLogin());
		} else {
			infoLabel.setText("");
		}

	}

	public void onCompletion(String responseText) {
		infoLabel.setText(responseText);
	}
}
