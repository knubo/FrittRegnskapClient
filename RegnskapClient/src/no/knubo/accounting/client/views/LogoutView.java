package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

public class LogoutView extends Composite implements ClickListener,
        ServerResponse {

    private static LogoutView instance;
    private static I18NAccount messages;
    private static Constants constants;

    public static LogoutView getInstance(Constants constants,
            I18NAccount messages) {
        LogoutView.messages = messages;
        LogoutView.constants = constants;
        if (instance == null) {
            instance = new LogoutView();
        }
        return instance;
    }

    private LogoutView() {
        DockPanel dp = new DockPanel();

        NamedButton logoutButton = new NamedButton("logout", messages.logout());
        logoutButton.addClickListener(this);
        dp.add(logoutButton, DockPanel.NORTH);

        initWidget(dp);
    }

    public void onClick(Widget sender) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "authenticate.php");

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");

            builder.sendRequest("action=logout", new AuthResponder(constants, messages,
                    this));
        } catch (RequestException e) {
            Window.alert("Error from server:" + e);
        }
    }

    public void serverResponse(String responseText) {
        if ("1".equals(responseText)) {
            Util.forward(constants.loginURL());
        } else {
            Window.alert("Error from server:" + responseText);
        }
    }
}
