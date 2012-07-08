package no.knubo.accounting.client.invoice;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.event.NavigationEvent;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceStartPage extends WizardPage<InvoiceContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    private final Elements elements;

    private boolean storageSupported;

    private final I18NAccount messages;

    private RadioButton radioYes;

    private RadioButton radioNo;

    private VerticalPanel invoiceContinueButtons;

    public RegisterInvoiceStartPage(Elements elements, I18NAccount messages) {
        this.elements = elements;
        this.messages = messages;
        panel = new FlowPanel();
        panel.add(new HTML(elements.wizard_invoice_intro()));

        invoiceContinueButtons = new VerticalPanel();
        invoiceContinueButtons.add(new Label(messages.invoice_continue()));
        radioYes = new RadioButton("invoice", elements.yes());
        invoiceContinueButtons.add(radioYes);
        radioNo = new RadioButton("invoice", elements.no());
        invoiceContinueButtons.add(radioNo);

        radioYes.setValue(true);

        panel.add(invoiceContinueButtons);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public String getTitle() {
        return elements.wizard_invoice_intro_title();
    }

    @Override
    public void beforeShow() {

        storageSupported = Storage.isLocalStorageSupported();

        if (!storageSupported) {
            panel.add(new Label(messages.browser_must_support_local_storage()));
        } else {
            checkIfInvoiceIsInProgress();
        }

        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, storageSupported);
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, false);
    }

    private void checkIfInvoiceIsInProgress() {
        Storage storage = Storage.getLocalStorageIfSupported();

        String invoices = storage.getItem(RegisterInvoiceChooseInvoiceTypePage.INVOICES_KEY);

        invoiceContinueButtons.setVisible(invoices != null);
    }

    @Override
    public void beforeNext(NavigationEvent event) {
        if (radioNo.getValue()) {
            Storage storage = Storage.getLocalStorageIfSupported();

            storage.removeItem(RegisterInvoiceChooseInvoiceTypePage.INVOICES_KEY);
        }
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

}