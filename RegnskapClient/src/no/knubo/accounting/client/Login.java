package no.knubo.accounting.client;

import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class Login implements EntryPoint, ClickHandler, ServerResponse {

    private Constants constants;

    private I18NAccount messages;

    private Elements elements;

    private Button loginButton;

    private Button forgottenButton;

    private Button emailButton;

    private Button hideButton;

    private DialogBox forgottenPasswordPopup;

    private TextBoxWithErrorText emailField;

    private Label label;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        messages = (I18NAccount) GWT.create(I18NAccount.class);
        constants = (Constants) GWT.create(Constants.class);
        elements = (Elements) GWT.create(Elements.class);

        HorizontalPanel hp = new HorizontalPanel();

        loginButton = new Button(elements.login());
        loginButton.addClickHandler(this);
        loginButton.addStyleName("buttonlogin");

        forgottenButton = new Button(elements.forgotten_password());
        forgottenButton.addStyleName("buttonLogin");
        forgottenButton.addClickHandler(this);
        forgottenButton.setWidth("100%");

        hp.add(loginButton);
        hp.add(forgottenButton);

        RootPanel.get("gwt-placement").add(hp);

        setText("logintitle", elements.login_title());

        setText("usertitle", elements.user());
        setText("passwordtitle", elements.password());

        addEnterGivesLogin();

        Timer timer = new Timer() {

            @Override
            public void run() {
                DOM.getElementById("username").focus();
            }
        };
        timer.schedule(500);

    }

    private void addEnterGivesLogin() {
        NativePreviewHandler handler = new NativePreviewHandler() {

            public void onPreviewNativeEvent(NativePreviewEvent event) {
                NativeEvent nativeEvent = event.getNativeEvent();

                if (nativeEvent.getKeyCode() == KeyCodes.KEY_ENTER) {
                    event.cancel();
                    doLogin();
                }
            }
        };
        Event.addNativePreviewHandler(handler);
    }

    private void setText(String id, String text) {
        Element elem = DOM.getElementById(id);
        elem.setInnerHTML(text);
    }

    public void onClick(ClickEvent event) {
        if (loginButton == event.getSource()) {
            doLogin();
        } else if (forgottenButton == event.getSource()) {
            showForgottenPassword();
        } else if(emailButton == event.getSource()) {
            sendEmail();
        } else if(hideButton == event.getSource()) {
            forgottenPasswordPopup.hide();
        }
    }

    private void sendEmail() {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), emailField);
        mv.email(messages.invalid_email(), emailField);
        
        if(!mv.validateStatus()) {
            return;
        }

        ServerResponse callback = new ServerResponse() {
            
            public void serverResponse(JSONValue responseObj) {
                JSONObject obj = responseObj.isObject();
                
                String status = Util.str(obj.get("status"));
                
                if("1".equals(status)) {
                    label.setText(messages.email_forgotten_sent());
                } else {
                    label.setText(messages.email_forgotten_error());                    
                }
            }
        };
        StringBuffer params = new StringBuffer();
        params.append("action=forgotten");
        Util.addPostParam(params, "email", emailField.getText());
        AuthResponder.post(constants, messages, callback, params , "../../RegnskapServer/services/forgotten.php");

        
        
    }

    private void showForgottenPassword() {
        forgottenPasswordPopup = new DialogBox();

        AccountTable table = new AccountTable("edittable");
        table.setHTML(0, 0, "<h2>" + elements.forgotten_password() + "</h2>");
        table.setText(1, 0, messages.forgottenPasswordIntro());

        emailField = new TextBoxWithErrorText("email");
        emailField.getTextBox().setWidth("30em");
        table.setWidget(2, 0, emailField);
        emailButton = new Button(elements.mail_send());
        emailButton.addClickHandler(this);
        emailButton.addStyleName("buttonlogin");
        hideButton = new Button(elements.close());
        hideButton.addClickHandler(this);

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(emailButton);
        hp.add(hideButton);

        label = new Label();
        table.setWidget(3, 0, label);
        table.setWidget(4, 0, hp);

        forgottenPasswordPopup.add(table);

        forgottenPasswordPopup.setAutoHideEnabled(false);
        forgottenPasswordPopup.center();
    }

    private void doLogin() {
        String user = getInput("username").getValue();
        String password = getInput("password").getValue();

        if (user.length() == 0 || password.length() == 0) {
            return;
        }

        AuthResponder.get(constants, messages, this, "../../RegnskapServer/services/authenticate.php?user=" + user
                + "&password=" + password);
    }

    public void serverResponse(JSONValue resonseObj) {
        JSONObject isObject = resonseObj.isObject();

        JSONValue error = isObject.get("error");

        if (error != null) {
            JSONString string = error.isString();
            setText("info", string.stringValue());
        } else {
            InputElement input = getInput("submitit");
            input.click();
        }
    }

    private InputElement getInput(String id) {
        Element elem = DOM.getElementById(id);
        InputElement input = InputElement.as(elem);

        if (input == null) {
            Window.alert("Fant ikke :" + id);
        }
        return input;
    }

}
