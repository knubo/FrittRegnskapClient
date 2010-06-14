package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;

public class AdminSQLView extends Composite {

    private static AdminSQLView me;

    public static AdminSQLView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminSQLView(messages, constants, elements);
        }
        return me;
    }

    private I18NAccount messages;
    private Constants constants;

    public AdminSQLView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;

    }
    
}
