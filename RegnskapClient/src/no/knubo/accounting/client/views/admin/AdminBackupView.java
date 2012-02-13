package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;

public class AdminBackupView extends Composite {


    private static AdminBackupView me;
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;

    public AdminBackupView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
    }

    public static AdminBackupView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminBackupView(messages, constants, elements);
        }
        return me;
    }
}
