package no.knubo.accounting.client;

import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

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
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Login implements EntryPoint, ClickHandler, ServerResponse {

    private Constants constants;

    private I18NAccount messages;

    private Elements elements;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        messages = (I18NAccount) GWT.create(I18NAccount.class);
        constants = (Constants) GWT.create(Constants.class);
        elements = (Elements) GWT.create(Elements.class);

        Button loginButton = new Button(elements.login());
        loginButton.addClickHandler(this);

        RootPanel.get("gwt-placement").add(loginButton);

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
                
                if(nativeEvent.getKeyCode() == KeyCodes.KEY_ENTER) {
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
        doLogin();
    }

    private void doLogin() {
        String user = getInput("username").getValue();
        String password = getInput("password").getValue();

        if(user.length() == 0 && password.length() == 0) {
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
        
        if(input == null) {
            Window.alert("Fant ikke :"+id);
        }
        return input;
    }

}
