package no.knubo.accounting.client.help;

import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;

public class HelpPanel extends Composite {

    DisclosurePanel disclosurePanel;
    
    public HelpPanel(I18NAccount messages) {
        disclosurePanel = new DisclosurePanel();
        disclosurePanel.setHeader(new Label(messages.help()));
        initWidget(disclosurePanel);
    }
}
