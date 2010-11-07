package no.knubo.accounting.client.views.portal;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class PortalSettings extends Composite implements ClickHandler {

    private static PortalSettings me;
    private AccountTable table;
    private NamedButton requestPortal;
    private NamedButton portalActivate;
    private NamedButton portalDeactivate;

    public PortalSettings(Constants constants, I18NAccount messages, Elements elements) {
        table = new AccountTable("edittable");

        table.setHeader(0, 0, elements.portal());
        table.setHeader(1, 0, elements.status());
        table.setHeader(2, 0, elements.portal_title());
        table.setHeader(3, 0, elements.portal_title());

        HorizontalPanel buttonPanel = new HorizontalPanel();
        
        requestPortal = new NamedButton("request_portal", elements.portal_action_request());
        requestPortal.addClickHandler(this);
        buttonPanel.add(requestPortal);
        
        
        portalActivate = new NamedButton("portal_activate", elements.portal_action_activate());
        portalActivate.addClickHandler(this);
        buttonPanel.add(portalActivate);
        
        portalDeactivate = new NamedButton("portal_deactivate", elements.portal_action_decativate());
        portalDeactivate.addClickHandler(this);
        buttonPanel.add(portalDeactivate);
        
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
        
    }
    
    public void onClick(ClickEvent event) {
        
    }
}
