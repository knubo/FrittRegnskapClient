package no.knubo.accounting.client.views.portal;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.user.client.ui.Composite;

public class PortalMemberlist extends Composite {

    private static PortalMemberlist me;
    private AccountTable table;

    public PortalMemberlist(Constants constants, I18NAccount messages, Elements elements) {
        table = new AccountTable("tableborder");

        table.setHeader(0, 0, elements.portal_members());
        table.getFlexCellFormatter().setColSpan(0, 0, 3);
        table.setHeader(1, 0, elements.firstname());
        table.setHeader(1, 1, elements.lastname());
        table.setHeader(1, 2, elements.last_login());
        
        initWidget(table);
    }

    public static PortalMemberlist getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (me == null) {
            me = new PortalMemberlist(constants, messages, elements);
        }
        return me;
    }

    public void init() {
        
    }
    
}
