package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class IntegrationView extends Composite implements ClickHandler {
    private static IntegrationView me;
    private AccountTable table;
    private NamedButton enableButton;
    private NamedButton disableButton;
    private final I18NAccount messages;
    private final Constants constants;
    private static Elements elements;

    public static IntegrationView show(I18NAccount messages, Constants constants, Elements elements) {
        IntegrationView.elements = elements;
        if (me == null) {
            me = new IntegrationView(messages, constants, elements);
        }
        return me;
    }

    public IntegrationView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        table = new AccountTable("tableborder");

        table.setText(0, 0, elements.menuitem_integration());
        table.setColSpanAndRowStyle(0, 0, 2, "header");

        table.setText(1, 0, elements.status(), "desc");
        table.setText(2, 0, elements.integration_secret(), "desc");

        HorizontalPanel hp = new HorizontalPanel();

        enableButton = new NamedButton("integration_enable", elements.integration_enable());
        hp.add(enableButton);
        enableButton.addClickHandler(this);
        disableButton = new NamedButton("integration_disable", elements.integration_disable());
        hp.add(disableButton);
        disableButton.addClickHandler(this);
        table.setWidget(3, 0, hp);
        table.setColSpanAndRowStyle(3, 0, 2, "desc");

        initWidget(table);
    }

    public void init() {
        String action = "status";
        doAction(action);

    }

    private void doAction(String action) {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                parse(responseObj);

            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/integration.php?action=" + action);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == enableButton) {
            doAction("enable");
        }
        if (event.getSource() == disableButton) {
            doAction("disable");
        }
    }

    private void parse(JSONValue responseObj) {
        JSONObject object = responseObj.isObject();
        String secret = Util.strSkipNull(object.get("secret"));

        boolean disabled = secret.equals("");
        enableButton.setEnabled(disabled);
        disableButton.setEnabled(!disabled);

        table.setText(2, 1, secret);
        if (disabled) {
            table.setText(1, 1, elements.integration_off(), "desc");
        } else {
            table.setText(1, 1, elements.integration_on(), "desc");
        }
    }
}
