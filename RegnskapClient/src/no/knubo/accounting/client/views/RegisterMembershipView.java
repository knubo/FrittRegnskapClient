package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class RegisterMembershipView extends Composite implements ClickListener {

    private static RegisterMembershipView me;

    private final I18NAccount messages;

    private final Constants constants;

    private RegisterMembershipView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;
    }

    public static RegisterMembershipView show(I18NAccount messages,
            Constants constants, ViewCallback caller) {
        if (me == null) {
            me = new RegisterMembershipView(messages, constants);
        }
        return me;
    }

    public void onClick(Widget sender) {

    }

}
