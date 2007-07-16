package no.knubo.accounting.client.views;

import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;

public class AboutView extends Composite {

    private static AboutView instance;
    private static I18NAccount messages;

    public static AboutView getInstance(I18NAccount messages) {
        AboutView.messages = messages;
        if (instance == null) {
            instance = new AboutView();
        }
        return instance;
    }

    private AboutView() {
        Frame frame = new Frame("about.html");
        frame.setSize("800", "600");
        initWidget(frame);
        Window.setTitle(messages.menuitem_about());
    }
}
