package no.knubo.accounting.client.help;

import java.util.MissingResourceException;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.HelpTexts;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.misc.WidgetIds;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HelpPanel extends Composite implements NativePreviewHandler, OpenHandler<DisclosurePanel> {

    DisclosurePanel disclosurePanel;
    private static HelpPanel me;

    private HTML helpHTML;
    private HTML contextHelp;
    private final I18NAccount messages;
    private final HelpTexts helpTexts;
    private String help;
    private String prevHelp = "";
    private HandlerRegistration nativePreviewHandler;
    private WidgetIds currentPage;
    private final Constants constants;
    private Elements elements;

    public void setCurrentWidget(Widget widget, WidgetIds currentPage) {
        this.currentPage = currentPage;
        if (disclosurePanel.isOpen()) {
            fetchHelp();
        }
        resize(widget);
    }

    private void fetchHelp() {
        helpHTML.setHTML("");
        ServerResponse callback = new ServerResponseString() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                /* Unused */
            }

            @Override
            public void serverResponse(String response) {
                helpHTML.setHTML(response);
            }
        };
        AuthResponder.getExternal(constants, messages, callback, "/wakka/wikka.php?wakka=" + idByWidget() + "/ajax");

    }

    private String idByWidget() {
        return currentPage.name().replace('_', 'x') + "xno";
    }

    public void resize(Widget widget) {

        int height = widget.getOffsetHeight();

        helpHTML.addStyleName("help");
        
        if (height < 500) {
            helpHTML.setHeight("500px");
        } else {
            helpHTML.setHeight(height + "px");
        }
    }

    public static HelpPanel getInstance(Constants constants, I18NAccount messages, Elements elements,
            HelpTexts helpTexts) {
        if (me == null) {
            me = new HelpPanel(messages, helpTexts, constants, elements);
        }

        return me;
    }

    private HelpPanel(I18NAccount messages, HelpTexts helpTexts, Constants constants, Elements elements) {
        this.messages = messages;
        this.helpTexts = helpTexts;
        this.constants = constants;
        this.elements = elements;

        disclosurePanel = new DisclosurePanel();
        disclosurePanel.setHeader(new Label(elements.help()));
        disclosurePanel.setWidth("100%");
        disclosurePanel.setHeight("100%");
        disclosurePanel.addOpenHandler(this);
        
        helpHTML = new HTML();
        contextHelp = new HTML();
        contextHelp.setStyleName("contexthelp");

        VerticalPanel dp = new VerticalPanel();
        dp.setWidth("100%");
        dp.add(contextHelp);

        ScrollPanel sp = new ScrollPanel();
        sp.add(helpHTML);
        
        dp.add(sp);
        addEventHandler();
        disclosurePanel.add(dp);
        initWidget(disclosurePanel);

        /* Every second poll and see if help should be updated. */
        new Timer() {
            @Override
            public void run() {

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
        if (nativePreviewHandler != null) {
            nativePreviewHandler.removeHandler();
        }
        nativePreviewHandler = Event.addNativePreviewHandler(this);
    }

    @Override
    public void onPreviewNativeEvent(NativePreviewEvent event) {
        EventTarget eventTarget = event.getNativeEvent().getEventTarget();

        if (eventTarget == null) {
            return;
        }

        if (!com.google.gwt.dom.client.Element.is(eventTarget)) {
            return;
        }

        Element elem = Element.as(eventTarget);

        String id = elem.getPropertyString("id");

        if (id == null) {
            return;
        }

        String helpText = null;
        try {
            helpText = helpTexts.getString(id);
        } catch (MissingResourceException e) {
            helpText = "";
        }

        String message = null;
        try {
            message = elements.getString(id);
        } catch (MissingResourceException e) {
            message = "";
        }

        help = formatHelp(message, helpText);

    }

    private String formatHelp(String header, String text) {
        return "<strong>" + header + "</strong><br>" + (text != null ? text : "---");
    }

    @Override
    public void onOpen(OpenEvent<DisclosurePanel> event) {
        fetchHelp();
        resize(this.getParent());
    }

}
