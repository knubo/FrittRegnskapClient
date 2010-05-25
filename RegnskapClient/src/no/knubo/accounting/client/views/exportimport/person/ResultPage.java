package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import no.knubo.accounting.client.Elements;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResultPage extends WizardPage<ImportPersonContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    private final Elements elements;

    public ResultPage(Elements elements) {
        this.elements = elements;
        panel = new FlowPanel();
        panel.add(new HTML("TODO"));
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public String getTitle() {
        return elements.wizard_import_person_result();
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }
    
    @Override
    public void beforeShow() {
    	getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
    	getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
    	getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, false);
    	getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, false);
    }
    

}
