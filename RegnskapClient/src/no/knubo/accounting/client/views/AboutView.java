package no.knubo.accounting.client.views;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;

public class AboutView extends Composite {

    private static AboutView instance;

    public static AboutView getInstance() {
        if (instance == null) {
            instance = new AboutView();
        }
        return instance;
    }

    private AboutView() {
        Frame frame = new Frame("about.html");
        frame.setSize("800", "600");
        initWidget(frame);
    }
}
