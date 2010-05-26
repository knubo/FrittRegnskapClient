package no.knubo.accounting.client.views.exportimport.person;

import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.event.NavigationEvent;
import no.knubo.accounting.client.Elements;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SelectFilePage extends WizardPage<ImportPersonContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    private final Elements elements;

    private final Hidden hiddenAction;

    private final FormPanel form;

    public SelectFilePage(Elements elements, Hidden hiddenAction, FormPanel form) {
        this.elements = elements;
        this.hiddenAction = hiddenAction;
        this.form = form;
        panel = new FlowPanel();

        Label delimiter = new Label(elements.delimiter());
        panel.add(delimiter);

        TextBox delimiterBox = new TextBox();
        delimiterBox.setName("delimiter");
        delimiterBox.setMaxLength(1);
        panel.add(delimiterBox);

        Label tb = new Label(elements.file());
        panel.add(tb);
        FileUpload upload = new FileUpload();
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
    public void beforeShow() {
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
    }

    @Override
    public void beforeNext(NavigationEvent event) {
        hiddenAction.setValue("findfields");
        form.submit();
    }
}
