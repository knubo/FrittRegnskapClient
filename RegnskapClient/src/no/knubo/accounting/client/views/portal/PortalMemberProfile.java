package no.knubo.accounting.client.views.portal;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

public class PortalMemberProfile extends Composite implements ClickHandler {

    private static PortalMemberProfile me;
    private AccountTable table;

    public PortalMemberProfile(Constants constants, I18NAccount messages, Elements elements) {
        table = new AccountTable("edittable");

        table.setHeader(0, 0, "Change with Firstname + Lastname");
        table.setHeader(1, 0, elements.last_login());
        table.setHeader(1, 1, elements.lastname());
        
        initWidget(table);
    }

    public static PortalMemberProfile getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (me == null) {
            me = new PortalMemberProfile(constants, messages, elements);
        }
        return me;
    }

    public void init() {
        
    }
    
    public void onClick(ClickEvent event) {
        
    }
}
