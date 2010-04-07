package no.knubo.accounting.client;

import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Login implements EntryPoint, ClickHandler, ServerResponse {

    private PasswordTextBox passBox;

    private Constants constants;

    private HTML infoLabel;

    private I18NAccount messages;

    private TextBox userBox;

    private Elements elements;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        messages = (I18NAccount) GWT.create(I18NAccount.class);
        constants = (Constants) GWT.create(Constants.class);
        elements = (Elements) GWT.create(Elements.class);

        DockPanel dp = new DockPanel();
        dp.setStyleName("middle");
        FlexTable table = new FlexTable();
        table.setStyleName("edittable");

        dp.add(table, DockPanel.CENTER);

        Button loginButton = new Button(elements.login());
        loginButton.addClickHandler(this);

        userBox = new TextBox();
        userBox.setWidth("12em");
        userBox.setMaxLength(12);
        passBox = new PasswordTextBox();
        passBox.setWidth("12em");
        passBox.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER) {
                    doLogin();
                }
            }

        });
        infoLabel = new HTML();

        table.setText(0, 0, elements.login());
        table.getFlexCellFormatter().setColSpan(0, 0, 2);
        table.setText(1, 0, elements.user());
        table.setWidget(1, 1, userBox);
        table.setText(2, 0, elements.password());
        table.setWidget(2, 1, passBox);
        table.setWidget(4, 1, loginButton);
        table.setWidget(5, 1, infoLabel);
        table.getFlexCellFormatter().setColSpan(4, 1, 2);
        RootPanel.get().add(dp);
        Window.setTitle(elements.login());
        userBox.setFocus(true);
        
    }

    public void onClick(ClickEvent event) {
        doLogin();
    }

    private void doLogin() {
        String user = this.userBox.getText();
        String password = this.passBox.getText();
        
        AuthResponder.get(constants, messages, this, "../../RegnskapServer/services/authenticate.php?user=" + user
                + "&password=" + password);
    }

    public void serverResponse(JSONValue resonseObj) {
        JSONObject isObject = resonseObj.isObject();

        JSONValue error = isObject.get("error");

        if (error != null) {
            JSONString string = error.isString();
            infoLabel.setText(string.stringValue());
        } else {
            Util.forward(constants.appURL());
        }        
    }

}
