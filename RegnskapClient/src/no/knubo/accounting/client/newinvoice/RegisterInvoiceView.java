package no.knubo.accounting.client.newinvoice;

import net.binarymuse.gwt.client.ui.wizard.Wizard;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.view.HasWizardButtonMethods;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceView extends Composite implements SubmitCompleteHandler, ClickHandler {

    private static RegisterInvoiceView instance;
    private Hidden hiddenAction;
    private final Elements elements;
    private VerticalPanel panel;
    private Hidden hiddenExclude;
    private FormPanel form;
    private final I18NAccount messages;
    private Wizard<InvoiceContext> wizard;
    private final Constants constants;
    private final ViewCallback callback;

    public static RegisterInvoiceView getInstance(Constants constants, I18NAccount messages, Elements elements,
            ViewCallback callback) {
        if (instance == null) {
            instance = new RegisterInvoiceView(messages, constants, elements, callback);
        }
        return instance;
    }

    public RegisterInvoiceView(final I18NAccount messages, Constants constants, final Elements elements,
            ViewCallback callback) {

        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        this.callback = callback;
        form = new FormPanel();
        form.setAction(constants.baseurl() + "accounting/invoice_wizard.php");

        // Because we're going to add a FileUpload widget, we'll need to set the
        // form to use the POST method, and multi-part MIME encoding.
        // form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.addSubmitCompleteHandler(this);

        panel = new VerticalPanel();
        form.setWidget(panel);

        panel.add(createWizard());

        initWidget(form);
    }

    private Widget createWizard() {
        final RegisterInvoiceStartPage startPage = new RegisterInvoiceStartPage(elements, messages);
        final RegisterInvoiceChooseReceivers chooseReceivers = new RegisterInvoiceChooseReceivers(elements, messages,
                constants);
        final RegisterInvoiceSummaryPage summaryPage = new RegisterInvoiceSummaryPage(elements, messages, constants);
        
        
        wizard = new Wizard<InvoiceContext>(elements.menuitem_invoice_new(), new InvoiceContext(form, hiddenAction,
                hiddenExclude));

        HasWizardButtonMethods cancelButton = wizard.getButton(ButtonType.BUTTON_CANCEL);
        cancelButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                wizard.showPage(chooseReceivers.getPageID(), false, false, true);
            }
        });

        HasWizardButtonMethods finishButton = wizard.getButton(ButtonType.BUTTON_FINISH);

        finishButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                wizard.showPage(summaryPage.getPageID(), false, false, true);
            }
        });

        wizard.addPage(startPage);
        wizard.addPage(new RegisterInvoiceChooseInvoiceTypePage(elements, messages, constants, callback));
        wizard.addPage(chooseReceivers);
        wizard.addPage(new RegisterInvoiceConfirmPage(elements, messages));
        wizard.addPage(summaryPage);

        wizard.setSize("1024px", "768px");

        wizard.getButton(ButtonType.BUTTON_FINISH).addClickHandler(this);

        // previewPage.addEventListeners();

        return wizard;
    }

    @Override
    public void onSubmitComplete(SubmitCompleteEvent event) {
        // if (hiddenAction.getValue().equals("findfields")) {
        // chooseFieldsAndDataPage.setHTMLInDataTable(event.getResults());
        //
        // } else if(hiddenAction.getValue().equals("preview")) {
        // previewPage.setPreviewHTML(event.getResults());
        // } else if(hiddenAction.getValue().equals("insert")) {
        // resultPage.setResultHTML(event.getResults());
        // }
    }

    @Override
    public void onClick(ClickEvent event) {
        hiddenAction.setValue("insert");
        form.submit();

        wizard.showNextPage();
    }
}
