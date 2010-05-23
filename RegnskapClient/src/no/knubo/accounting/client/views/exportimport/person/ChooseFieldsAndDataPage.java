package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ChooseFieldsAndDataPage extends WizardPage<ImportPersonContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    public ChooseFieldsAndDataPage() {
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
    
    @Override
    public void beforeShow() {
    	getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
    	getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
    	getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, true);
    }
    

}
