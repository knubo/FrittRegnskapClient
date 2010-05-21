package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class WelcomePage extends WizardPage<ImportPersonContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    public WelcomePage() {
        panel = new FlowPanel();
        panel.add(new HTML("<h1>Welcome</h1>"));
        panel.add(new HTML("<p>This wizard will guide you through " +
            "the process of creating a new account.</p>"));
        panel.add(new HTML("<p>When you are ready to being, please " +
            "click \"Next.\"</p>"));
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public String getTitle() {
        return "Welcome";
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

}