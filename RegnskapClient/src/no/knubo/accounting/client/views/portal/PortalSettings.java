package no.knubo.accounting.client.views.portal;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class PortalSettings extends Composite implements ClickHandler {

    private static PortalSettings me;
    private AccountTable table;
    private NamedButton requestPortal;
    private NamedButton portalActivate;
    private NamedButton portalDeactivate;
    private final Constants constants;
    private final I18NAccount messages;
    private TextBoxWithErrorText portalTitle;
    private final Elements elements;
    private NamedButton updateButton;
    private Label infoLabel;

    public PortalSettings(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        table = new AccountTable("edittable");

        table.setHeader(0, 0, elements.portal());
        table.setHeader(1, 0, elements.status());
        table.setHeader(2, 0, elements.portal_title());

        portalTitle = new TextBoxWithErrorText("portal_title");
        portalTitle.setMaxLength(255);
        portalTitle.setVisibleLength(100);
        table.setWidget(2, 1, portalTitle);

        HorizontalPanel buttonPanel = new HorizontalPanel();

        requestPortal = new NamedButton("request_portal", elements.portal_action_request());
        requestPortal.addClickHandler(this);
        requestPortal.addStyleName("buttonrownarrow");
        buttonPanel.add(requestPortal);

        portalActivate = new NamedButton("portal_activate", elements.portal_action_activate());
        portalActivate.addClickHandler(this);
        portalActivate.addStyleName("buttonrownarrow");
        buttonPanel.add(portalActivate);

        portalDeactivate = new NamedButton("portal_deactivate", elements.portal_action_decativate());
        portalDeactivate.addClickHandler(this);
        portalDeactivate.addStyleName("buttonrownarrow");
        buttonPanel.add(portalDeactivate);

        updateButton = new NamedButton("update", elements.update());
        updateButton.addClickHandler(this);
        updateButton.addStyleName("buttonrow");
        table.setWidget(3,0, updateButton);
       
        infoLabel = new Label();
        table.setWidget(3,1,infoLabel);
        
        table.setWidgetFlex(4, 0, buttonPanel).setColSpan(4, 0, 2);

        initWidget(table);
    }

    public static PortalSettings getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (me == null) {
            me = new PortalSettings(constants, messages, elements);
        }
        return me;
    }

    public void init() {
        ServerResponse resp = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();

                portalTitle.setText(Util.str(object.get("portal_title")));

                int portalStatus = Util.getInt(object.get("portal_status"));

                updateStatusText(portalStatus);
           
            }
        };
        AuthResponder.get(constants, messages, resp, "portal/portal_admin.php?action=portalinfo");

    }

    public void onClick(ClickEvent event) {
        if(event.getSource() == updateButton) {
            savePortalTitle();
        }
        if(event.getSource() == portalDeactivate) {
            sendStatusChange(3);
        }
        if(event.getSource() == portalActivate) {
            sendStatusChange(1);
        }
        if(event.getSource() == requestPortal) {
            sendStatusChange(4);
        }
        
    }

    private void sendStatusChange(final int newStatus) {
        ServerResponse callback = new ServerResponse() {
            
            public void serverResponse(JSONValue responseObj) {
                infoLabel.setText(messages.save_ok());
                updateStatusText(newStatus);
            }
        };
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=saveinfo");
        Util.addPostParam(parameters, "portal_title", portalTitle.getText());
        
        AuthResponder.get(constants, messages, callback, "portal/portal_admin.php?action=change&status="+newStatus);
        
    }

    private void savePortalTitle() {
        ServerResponse callback = new ServerResponse() {
            
            public void serverResponse(JSONValue responseObj) {
                infoLabel.setText(messages.save_ok());
            }
        };
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=saveinfo");
        Util.addPostParam(parameters, "portal_title", portalTitle.getText());
        
        AuthResponder.post(constants, messages, callback, parameters, "portal/portal_admin.php");
    }

    private void updateStatusText(int portalStatus) {
        switch (portalStatus) {
        case 0:
            table.setText(1,1, elements.portal_status_inactive_0());
            break;
        case 1:
            table.setText(1,1, elements.portal_status_active_1());
            break;
        case 2:
            table.setText(1,1, elements.portal_status_blocked_2());
            break;
        case 3:
            table.setText(1,1, elements.portal_status_closed_3());
            break;
        case 4:
            table.setText(1,1, elements.portal_status_pending_4());
            break;
        }
        
        requestPortal.setEnabled(portalStatus == 0);
        portalActivate.setEnabled(portalStatus == 3);
        portalDeactivate.setEnabled(portalStatus == 1);
    }
}
