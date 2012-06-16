package no.knubo.accounting.client.invoice;

import net.binarymuse.gwt.client.ui.wizard.Wizard;
import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.exportimport.person.ChooseFieldsAndDataPage;
import no.knubo.accounting.client.views.exportimport.person.PreviewPage;
import no.knubo.accounting.client.views.exportimport.person.ResultPage;
import no.knubo.accounting.client.views.exportimport.person.SelectFilePage;

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

        // hiddenAction = new Hidden();
        // hiddenAction.setName("action");
        // panel.add(hiddenAction);
        //
        // hiddenExclude = new Hidden();
        // hiddenExclude.setName("exclude");
        // panel.add(hiddenExclude);

        panel.add(createWizard());

        initWidget(form);
    }

    private Widget createWizard() {
        wizard = new Wizard<InvoiceContext>(elements.menuitem_import_person(), new InvoiceContext(form, hiddenAction,
                hiddenExclude));

        wizard.addPage(new RegisterInvoiceStartPage(elements, messages));
        wizard.addPage(new RegisterInvoiceChooseInvoiceTypePage(elements, messages, constants, callback));
        // wizard.addPage(new SelectFilePage(elements, messages));

        // chooseFieldsAndDataPage = new ChooseFieldsAndDataPage(elements,
        // messages);
        // wizard.addPage(chooseFieldsAndDataPage);

        // previewPage = new PreviewPage(elements);
        // wizard.addPage(previewPage);
        // resultPage = new ResultPage(elements);
        // wizard.addPage(resultPage);
        wizard.setSize("800px", "600px");

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
