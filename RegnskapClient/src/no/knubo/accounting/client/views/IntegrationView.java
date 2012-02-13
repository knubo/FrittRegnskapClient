package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

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
    private TextBoxWithErrorText email;
    private NamedButton updateButton;
    private static Elements elements;

    public static IntegrationView getInstance(I18NAccount messages, Constants constants, Elements elements) {
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
        table.setText(3, 0, elements.integration_email(), "desc");
        email = new TextBoxWithErrorText(elements.integration_email());
        table.setWidget(3, 1, email);

        HorizontalPanel hp = new HorizontalPanel();

        enableButton = new NamedButton("integration_enable", elements.integration_enable());
        hp.add(enableButton);
        enableButton.addClickHandler(this);
        enableButton.addStyleName("buttonrow");
        
        updateButton = new NamedButton("update", elements.update());
        updateButton.addClickHandler(this);
        updateButton.addStyleName("buttonrow");
        hp.add(updateButton);
        
        disableButton = new NamedButton("integration_disable", elements.integration_disable());
        hp.add(disableButton);
        disableButton.addStyleName("buttonrow");
        disableButton.addClickHandler(this);
        table.setWidget(4, 0, hp);
        table.setColSpanAndRowStyle(4, 0, 2, "desc");

        table.setText(5, 0, "", "desc");
        table.setColSpanAndRowStyle(5, 0, 2, "");
        
        initWidget(table);
    }

    public void init() {
        String action = "status";
        doAction(action);

    }

    private void doAction(final String action) {
        MasterValidator mv = new MasterValidator();
        mv.email(messages.invalid_email(), email);

        if (!mv.validateStatus()) {
            return;
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                parse(responseObj);
                
                if(action.equals("update")) {
                    table.setText(5, 0, messages.save_ok());
                } else {
                    table.setText(5, 0, "");
                }
            }
        };
        StringBuffer params = new StringBuffer();
        params.append("action=" + action);
        Util.addPostParam(params, "email", email.getText());

        AuthResponder.post(constants, messages, callback, params, "accounting/integration.php");
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == enableButton) {
            doAction("enable");
        }
        if (event.getSource() == disableButton) {
            doAction("disable");
        }
        if(event.getSource() == updateButton) {
            doAction("update");
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
        email.setText(Util.strSkipNull(object.get("email")));
    }
}
