package no.knubo.accounting.client.invoice;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceStartPage extends WizardPage<InvoiceContext> {

    public static final PageID PAGEID = new PageID();

    private FlowPanel panel;

    private final Elements elements;

    private boolean storageSupported;

    private final I18NAccount messages;

    public RegisterInvoiceStartPage(Elements elements, I18NAccount messages) {
        this.elements = elements;
        this.messages = messages;
        panel = new FlowPanel();
        panel.add(new HTML(elements.wizard_invoice_intro()));
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

        if(!storageSupported) {
            panel.add(new Label(messages.browser_must_support_local_storage()));
        }
        
        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, storageSupported);
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, false);
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

}