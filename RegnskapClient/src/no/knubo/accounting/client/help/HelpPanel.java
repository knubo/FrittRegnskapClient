package no.knubo.accounting.client.help;

import no.knubo.accounting.client.HelpTexts;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HelpPanel extends Composite implements EventPreview {

    DisclosurePanel disclosurePanel;
    private static HelpPanel me;

    private Frame mainFrame;
    private HTML contextHelp;
    private final I18NAccount messages;
    private final HelpTexts helpTexts;
    private String help;
    private String prevHelp = "";

    public void setCurrentWidget(Widget widget, int currentPage) {
        mainFrame.setUrl("help/" + messages.HELP_ROOT() + "/" + currentPage
                + "/index.html");
        resize(widget);
    }

    public void resize(Widget widget) {

        int width = widget.getOffsetWidth();
        int height = widget.getOffsetHeight();

        if ((800 - width) < 400) {
            mainFrame.setWidth("400px");
        } else {
            mainFrame.setWidth((800 - width) + "px");
        }

        if (height < 400) {
            mainFrame.setHeight("400px");
        } else {
            mainFrame.setHeight(height + "px");
        }
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

        VerticalPanel dp = new VerticalPanel();
        dp.setWidth("100%");
        dp.add(contextHelp);
        dp.add(mainFrame);
        addEventHandler();
        disclosurePanel.add(dp);
        initWidget(disclosurePanel);

        /* Every second poll and see if help should be updated. */
        new Timer() {
            public void run() {

                // if(!disclosurePanel.isOpen()) {
                // return;
                // }

                if (help != null && prevHelp != help) {
                    contextHelp.setHTML(help);
                    prevHelp = help;
                }
            }

        }.scheduleRepeating(1000);
    }

    /**
     * Each time this is called it makes sure that it is on top of the event
     * stack so that it receives events.
     */
    public void addEventHandler() {
        DOM.removeEventPreview(this);
        DOM.addEventPreview(this);
    }

    public boolean onEventPreview(final Event event) {
        // if(!disclosurePanel.isOpen()) {
        // return true;
        // }
        Element elem = DOM.eventGetTarget(event);
        String id = DOM.getElementProperty(elem, "id");

        if (id.equals("trustStatusView_newTrustButton")) {
            help = formatHelp(messages.trustStatusView_newTrustButton(),
                    helpTexts.trustStatusView_newTrustButton());
        } else if (id.equals("trustStatusView.cancelButton")) {
            help = formatHelp(messages.trustStatusView_cancelButton(),
                    helpTexts.trustStatusView_newTrustButton());
        } else if (id.equals("postnmb")) {
            help = formatHelp(messages.postnmb(), helpTexts.postnmb());
        } else if (id.equals("day")) {
            help = formatHelp(messages.day(), helpTexts.day());
        } else if (id.equals("day_single")) {
            help = formatHelp(messages.day_single(), helpTexts.day_single());
        } else if (id.equals("month")) {
            help = formatHelp(messages.month(), helpTexts.month());
        } else if (id.equals("year")) {
            help = formatHelp(messages.year(), helpTexts.year());
        } else if (id.equals("attachment")) {
            help = formatHelp(messages.attachment(), helpTexts.attachment());
        } else if (id.equals("description")) {
            help = formatHelp(messages.description(), helpTexts.description());
        } else if (id.equals("RegisterHappening_saveButton")) {
            help = formatHelp(messages.RegisterHappening_saveButton(),
                    helpTexts.RegisterHappening_saveButton());
        } else if (id.equals("register_count_post")) {
            help = formatHelp(messages.register_count_post(), helpTexts
                    .register_count_post());
        } else if (id.equals("amount")) {
            help = formatHelp(messages.amount(), helpTexts.amount());
        } else if (id.equals("money_type")) {
            help = formatHelp(messages.money_type(), helpTexts.money_type());
        } else if (id.equals("number1000")) {
            help = formatHelp("1000", helpTexts.number1000());
        } else if (id.equals("number500")) {
            help = formatHelp("500", helpTexts.number500());
        } else if (id.equals("number200")) {
            help = formatHelp("200", helpTexts.number200());
        } else if (id.equals("number100")) {
            help = formatHelp("100", helpTexts.number100());
        } else if (id.equals("number50")) {
            help = formatHelp("50", helpTexts.number50());
        } else if (id.equals("number20")) {
            help = formatHelp("20", helpTexts.number20());
        } else if (id.equals("number10")) {
            help = formatHelp("10", helpTexts.number10());
        } else if (id.equals("number5")) {
            help = formatHelp("5", helpTexts.number5());
        } else if (id.equals("number1")) {
            help = formatHelp("1", helpTexts.number1());
        } else if (id.equals("number0.5")) {
            help = formatHelp("0.5", helpTexts.number0_5());
        } else if (id.equals("search")) {
            help = formatHelp(messages.search(), helpTexts.search());
        } else if (id.equals("clear")) {
            help = formatHelp(messages.clear(), helpTexts.clear());
        } else if (id.equals("hidden_person")) {
            help = formatHelp(messages.hidden_person(), helpTexts
                    .hidden_person());
        }

        return true;
    }

    private String formatHelp(String header, String text) {
        return "<strong>" + header + "</strong><br>" + text;
    }

}
