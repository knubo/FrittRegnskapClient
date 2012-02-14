package no.knubo.accounting.client.views.admin;

import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseWithValidation;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewInstallPopup extends DialogBox implements ClickHandler {

    private static final int FIELD_SUPERUSER = 0;
    private static final int FIELD_PASSWORD = 1;
    private static final int FIELD_DOMAIN = 2;
    private static final int FIELD_CONTACT = 3;
    private static final int FIELD_CLUB = 4;
    private static final int FIELD_EMAIL = 5;
    private static final int FIELD_ADDRESS = 6;
    private static final int FIELD_POSTNMB = 7;
    private static final int FIELD_CITY = 8;
    private static final int FIELD_PHONE = 9;
    private static final int FIELD_WIKILOGIN = 10;
    private final AdminInstallsView adminInstallsView;
    private AccountTable table;
    private final I18NAccount messages;
    private final Constants constants;
    private NamedButton installButton;
    private NamedButton closeButton;
    private Label infoLabel;

    public NewInstallPopup(AdminInstallsView adminInstallsView, I18NAccount messages, Elements elements,
            Constants constants) {
        this.adminInstallsView = adminInstallsView;
        this.messages = messages;
        this.constants = constants;

        table = new AccountTable("edittable");

        add(FIELD_SUPERUSER, elements.admin_superuser());
        add(FIELD_PASSWORD, elements.password());
        add(FIELD_DOMAIN, elements.admin_domain());
        add(FIELD_CONTACT, elements.admin_contact());
        add(FIELD_CLUB, elements.admin_clubname());
        add(FIELD_EMAIL, elements.email());
        add(FIELD_ADDRESS, elements.address());
        add(FIELD_POSTNMB, elements.postnmb());
        add(FIELD_CITY, elements.city());
        add(FIELD_PHONE, elements.phone());
        add(FIELD_WIKILOGIN, "wikilogin");

        FlowPanel buttonPanel = new FlowPanel();
        installButton = new NamedButton("install_create", elements.create());
        installButton.addClickHandler(this);
        buttonPanel.add(installButton);
        closeButton = new NamedButton("install_close", elements.close());
        closeButton.addClickHandler(this);
        buttonPanel.add(closeButton);

        VerticalPanel vp = new VerticalPanel();
        vp.add(table);
        infoLabel = new Label();
        infoLabel.addStyleName("error");
        vp.add(infoLabel);
        vp.add(buttonPanel);

        add(vp);
        center();
    }

    private void add(int row, String text) {
        table.setText(row, 0, text);
        table.setWidget(row, 1, new TextBoxWithErrorText("installs_" + text));
    }

    protected void callInstall(String secret) {
        StringBuffer sb = new StringBuffer();
        sb.append("superuser=");
        sb.append(getText(FIELD_SUPERUSER));
        Util.addPostParam(sb, "secret", secret);
        Util.addPostParam(sb, "password", getText(FIELD_PASSWORD));
        Util.addPostParam(sb, "contact", getText(FIELD_CONTACT));
        Util.addPostParam(sb, "phone", getText(FIELD_PHONE));
        Util.addPostParam(sb, "zipcode", getText(FIELD_POSTNMB));
        Util.addPostParam(sb, "city", getText(FIELD_CITY));
        Util.addPostParam(sb, "address", getText(FIELD_ADDRESS));
        Util.addPostParam(sb, "email", getText(FIELD_EMAIL));
        Util.addPostParam(sb, "clubname", getText(FIELD_CLUB));
        Util.addPostParam(sb, "domainname", getText(FIELD_DOMAIN));
        Util.addPostParam(sb, "password", getText(FIELD_PASSWORD));
        Util.addPostParam(sb, "wikilogin", getText(FIELD_WIKILOGIN));

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                hide();
                adminInstallsView.init();
            }
        };
        AuthResponder.post(constants, messages, callback, sb, "admin/admin_install.php");

    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == closeButton) {
            hide();
            return;
        }

        if (event.getSource() == installButton) {
            startInstall();
        }
    }

    private void startInstall() {
        MasterValidator mv = new MasterValidator();

        for (int row = 0; row < table.getRowCount(); row++) {
            TextBoxWithErrorText t = getField(row);
            mv.mandatory(messages.required_field(), t);
        }
        
        mv.email(messages.invalid_email(), getField(FIELD_EMAIL));
        
        if (!mv.validateStatus()) {
            return;
        }

        checkAndCreateSecret();
    }

    private void checkAndCreateSecret() {
        StringBuffer sb = new StringBuffer();
        sb.append("action=installprep");
        Util.addPostParam(sb, "wikilogin", getText(FIELD_WIKILOGIN));
        Util.addPostParam(sb, "domain", getText(FIELD_DOMAIN));

        ServerResponse callback = new ServerResponseWithValidation() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();
                callInstall(Util.str(object.get("secret")));
            }

            @Override
            public void validationError(List<String> fields) {
                StringBuilder sb = new StringBuilder();
                for (String field : fields) {
                    if (field.equals("wikilogin")) {
                        sb.append(messages.admin_error_wikilogin());
                    }
                    if(field.equals("domain")) {
                        sb.append(messages.admin_error_domain());
                    }
                    sb.append(" ");
                }
                infoLabel.setText(sb.toString());
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "admin/installs.php");
    }

    private String getText(int row) {
        return getField(row).getText();
    }

    private TextBoxWithErrorText getField(int row) {
        return (TextBoxWithErrorText) table.getWidget(row, 1);
    }

}
