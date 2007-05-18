package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class RegisterCountView extends Composite implements ClickListener {

    private static RegisterCountView me;

    public static RegisterCountView show(I18NAccount messages,
            Constants constants, ViewCallback caller) {
        if (me == null) {
            me = new RegisterCountView(messages, constants);
        }
        return me;
    }

    private final I18NAccount messages;

    private final Constants constants;

    protected String currentYear;

    protected String currentMonth;

    private FlexTable mainTable;

    private RegisterCountView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();
        mainTable = new FlexTable();
        dp.add(mainTable, DockPanel.NORTH);
        
        mainTable.setStyleName("edittable");

        mainTable.setText(0, 0, messages.postnmb());
        
        initWidget(dp);
    }

    public void onClick(Widget sender) {

    }
}
