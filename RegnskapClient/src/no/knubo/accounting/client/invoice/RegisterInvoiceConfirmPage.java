package no.knubo.accounting.client.invoice;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.RegnskapLocalStorage;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceConfirmPage extends WizardPage<InvoiceContext> {
    public static final PageID PAGEID = new PageID();
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;
    private VerticalPanel vp;
    private Label label;

    public RegisterInvoiceConfirmPage(Elements elements, I18NAccount messages, Constants constants) {
        this.elements = elements;
        this.messages = messages;
        this.constants = constants;

        vp = new VerticalPanel();

        label = new Label();
        label.addStyleName("wizardpad");
        vp.add(label);
    }

    @Override
    public void beforeShow() {
        int invoiceCount = RegnskapLocalStorage.getInvoiceCount();
        int receiversCount = RegnskapLocalStorage.getInvoiceReciversAsJSONArray().size();
        label.setText(messages.invoice_confirm_message(String.valueOf(invoiceCount), String.valueOf(receiversCount)));

        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, false);
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

    @Override
    public String getTitle() {
        return elements.invoice_confirm_create();
    }

    @Override
    public Widget asWidget() {
        return vp;
    }

}
