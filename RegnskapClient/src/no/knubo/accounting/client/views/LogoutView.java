package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;

public class LogoutView extends Composite implements ClickHandler, ServerResponse {

    private static LogoutView instance;
    private static I18NAccount messages;
    private static Constants constants;
    private static Elements elements;

    public static LogoutView getInstance(Constants constants, I18NAccount messages,
            Elements elements) {
        LogoutView.messages = messages;
        LogoutView.constants = constants;
        LogoutView.elements = elements;
        if (instance == null) {
            instance = new LogoutView();
        }
        return instance;
    }

    private LogoutView() {
        DockPanel dp = new DockPanel();

        NamedButton logoutButton = new NamedButton("logout", elements.logout());
        logoutButton.addClickHandler(this);
        dp.add(logoutButton, DockPanel.NORTH);

        initWidget(dp);
    }

    public void onClick(ClickEvent event) {
        StringBuffer sb = new StringBuffer();
        sb.append("action=logout");
        AuthResponder.post(constants, messages, this, sb, "authenticate.php");
    }

    public void serverResponse(JSONValue val) {
        JSONObject obj = val.isObject();

        if ("1".equals(Util.str(obj.get("result")))) {
            Util.forward(constants.loginURL());
        }
    }
}
