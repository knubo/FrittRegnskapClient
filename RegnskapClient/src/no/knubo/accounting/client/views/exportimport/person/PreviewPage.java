package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import no.knubo.accounting.client.Elements;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class PreviewPage extends WizardPage<ImportPersonContext> implements ClickHandler {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    private final Elements elements;

    private HTML preview;

    public PreviewPage(Elements elements) {
        this.elements = elements;
        panel = new FlowPanel();
        preview = new HTML();
        panel.add(preview);

    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public String getTitle() {
        return elements.wizard_import_person_preview();
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

    @Override
    public void beforeShow() {
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, false);
        
        
        getContext().hiddenAction.setValue("preview");
        getContext().submit();

    }

    public void setPreviewHTML(String results) {
        preview.setHTML(results);
    }

    public void onClick(ClickEvent event) {
        getWizard().showPreviousPage();
    }

    public void addEventListeners() {
        getWizard().getButton(ButtonType.BUTTON_CANCEL).addClickHandler(this);

    }


}
