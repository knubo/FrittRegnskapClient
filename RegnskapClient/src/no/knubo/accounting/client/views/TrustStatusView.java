package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class TrustStatusView extends Composite {

    private static TrustStatusView instance;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    public static TrustStatusView getInstance(Constants constants,
            I18NAccount messages) {
        if (instance == null) {
            instance = new TrustStatusView(constants, messages);
        }
        return instance;
    }

    public TrustStatusView(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        DockPanel dp = new DockPanel();

        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

}
