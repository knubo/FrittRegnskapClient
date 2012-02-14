package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResultPage extends WizardPage<ImportPersonContext> implements ClickHandler {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    private final Elements elements;

    private HTML resultHTML;

    public ResultPage(Elements elements) {
        this.elements = elements;
        panel = new FlowPanel();
        resultHTML = new HTML();
        panel.add(resultHTML);
        NamedButton okButton = new NamedButton("import_complete", elements.ok());
        okButton.addClickHandler(this);
        panel.add(okButton);
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

    public void setResultHTML(String results) {
        resultHTML.setHTML(results);
    }

    @Override
    public void onClick(ClickEvent event) {
        getWizard().showFirstPage();
    }
    

}
