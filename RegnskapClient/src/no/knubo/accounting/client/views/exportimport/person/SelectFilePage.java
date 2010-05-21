package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class SelectFilePage extends WizardPage<ImportPersonContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    public SelectFilePage() {
        panel = new FlowPanel();
        panel.add(new HTML("TODO"));
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public String getTitle() {
        return "Select fields and data";
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }
    
    

}
