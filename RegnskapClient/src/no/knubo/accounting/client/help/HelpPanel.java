package no.knubo.accounting.client.help;

import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class HelpPanel extends Composite implements EventPreview {

    DisclosurePanel disclosurePanel;
    private HTML html;

    public HelpPanel(I18NAccount messages) {
        disclosurePanel = new DisclosurePanel();
        disclosurePanel.setHeader(new Label(messages.help()));

        html = new HTML();
        disclosurePanel.add(html);
        DOM.addEventPreview(this);
        initWidget(disclosurePanel);
    }

    public boolean onEventPreview(Event event) {

        Element elem = DOM.eventGetTarget(event);

        String name = DOM.getElementProperty(elem, "name");
        html.setText("Elem:" + name);

        return true;
    }
}
