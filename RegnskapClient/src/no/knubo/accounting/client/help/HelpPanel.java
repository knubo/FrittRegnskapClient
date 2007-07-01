package no.knubo.accounting.client.help;

import no.knubo.accounting.client.HelpTexts;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class HelpPanel extends Composite implements EventPreview {

    DisclosurePanel disclosurePanel;
    private static HelpPanel me;

    private Frame mainFrame;
    private HTML contextHelp;
    private final I18NAccount messages;
    private final HelpTexts helpTexts;

    public void setCurrentWidget(Widget widget, int currentPage) {
        mainFrame.setUrl("help/" + messages.HELP_ROOT() + "/" + currentPage
                + "/index.html");
        mainFrame.setWidth("100%"); 
        mainFrame.setHeight(widget.getOffsetHeight()+"px");
    }

    public static HelpPanel getInstance(I18NAccount messages,
            HelpTexts helpTexts) {
        if (me == null) {
            me = new HelpPanel(messages, helpTexts);
        }

        return me;
    }

    private HelpPanel(I18NAccount messages, HelpTexts helpTexts) {
        this.messages = messages;
        this.helpTexts = helpTexts;
        disclosurePanel = new DisclosurePanel();
        disclosurePanel.setHeader(new Label(messages.help()));
        disclosurePanel.setWidth("100%");
        disclosurePanel.setHeight("100%");
        mainFrame = new Frame();
        contextHelp = new HTML();
        contextHelp.setStyleName("contexthelp");
        
        HorizontalPanel dp = new HorizontalPanel();
        dp.setWidth("100%");
        dp.add(mainFrame);
        dp.add(contextHelp);
        DOM.addEventPreview(this);
        disclosurePanel.add(dp);
        initWidget(disclosurePanel);
    }

    public boolean onEventPreview(Event event) {

        Element elem = DOM.eventGetTarget(event);

        String id = DOM.getElementProperty(elem, "id");

        String help = "Context help";
        if(id.equals("postnmb")) {
            help = "<strong>"+messages.postnmb()+"</strong><br>"+helpTexts.postnmb();
        }

        contextHelp.setHTML(help);            
        return true;
    }
}
