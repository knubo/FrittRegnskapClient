package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.event.NavigationEvent;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.FileUploadWithErrorText;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SelectFilePage extends WizardPage<ImportPersonContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    private final Elements elements;

    private TextBoxWithErrorText delimiterBox;

    private final I18NAccount messages;

    private FileUploadWithErrorText upload;

    public SelectFilePage(Elements elements, I18NAccount messages) {
        this.elements = elements;
        this.messages = messages;
        panel = new FlowPanel();

        Label delimiter = new Label(elements.delimiter());
        panel.add(delimiter);

        delimiterBox = new TextBoxWithErrorText("delimiter");
        delimiterBox.setName("delimiter");
        delimiterBox.setMaxLength(1);
        panel.add(delimiterBox);

        Label tb = new Label(elements.file());
        panel.add(tb);
        upload = new FileUploadWithErrorText("file_upload");
        upload.setName("uploadFormElement");
        panel.add(upload);

    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public String getTitle() {
        return elements.wizard_import_person_identify_fields();
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

    @Override
    public void afterShow() {
        delimiterBox.setFocus(true);
    }

    @Override
    public void beforeShow() {
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
    }

    @Override
    public void beforeNext(NavigationEvent event) {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), delimiterBox, upload);
       
        if(!mv.validateStatus()) {
            event.cancel();
            return;
        }

        
    }
}
