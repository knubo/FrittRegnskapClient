package no.knubo.accounting.client.newinvoice;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.RegnskapLocalStorage;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceSummaryPage extends WizardPage<InvoiceContext> implements ClickHandler {

    public static final PageID PAGEID = new PageID();
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;
    private FlowPanel vp;
    private NamedButton okButton;

    public RegisterInvoiceSummaryPage(Elements elements, I18NAccount messages, Constants constants) {
        this.elements = elements;
        this.messages = messages;
        this.constants = constants;

        vp = new FlowPanel();

        Label label = new Label(messages.invoice_created_message());
        label.addStyleName("wizardpad");
        vp.add(label);
        
        okButton = new NamedButton("menuitem_invoice_new", elements.menuitem_invoice_new());
        okButton.addStyleName("buttonrow");
        okButton.addClickHandler(this);
        vp.add(okButton);
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

    @Override
    public String getTitle() {
        return elements.invoice_create_complete();
    }

    @Override
    public void beforeShow() {
        createInvoices();
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, false);
    }

    private void createInvoices() {

        String invoices = RegnskapLocalStorage.getInvoices();
        StringBuffer receivers = RegnskapLocalStorage.getInvoiceRecivers();

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                RegnskapLocalStorage.removeInvoicesData();
            }
        };
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=create_invoices");

        Util.addPostParam(parameters, "invoices", invoices);
        Util.addPostParam(parameters, "receivers", receivers.toString());
        Util.addPostParam(parameters, "invoice_type", String.valueOf(RegnskapLocalStorage.getInvoiceTemplate()));
        
        AuthResponder.post(constants, messages, callback, parameters, "accounting/invoice_ops.php");
    }

    @Override
    public Widget asWidget() {
        return vp;
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == okButton) {
            getWizard().showFirstPage();
        }
    }

}
