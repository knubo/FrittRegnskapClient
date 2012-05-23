package no.knubo.accounting.client.invoice;

import java.util.Date;

import net.binarymuse.gwt.client.ui.wizard.Wizard.ButtonType;
import net.binarymuse.gwt.client.ui.wizard.WizardPage;
import net.binarymuse.gwt.client.ui.wizard.event.NavigationEvent;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import org.gwt.advanced.client.datamodel.EditableGridDataModel;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.cell.DateCell;
import org.gwt.advanced.client.ui.widget.cell.TextBoxCell;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterInvoiceChooseInvoiceTypePage extends WizardPage<InvoiceContext> implements ClickHandler {

    public static final PageID PAGEID = new PageID();

    private final Elements elements;

    private AccountTable table;

    private ListBoxWithErrorText invoiceTemplates;

    private VerticalPanel vp;

    private final I18NAccount messages;

    private final Constants constants;

    private NamedButton useButton;

    private TextBoxWithErrorText dueDate;

    private TextBoxWithErrorText amount;

    private TextBoxWithErrorText reoccuranceInterval;

    public RegisterInvoiceChooseInvoiceTypePage(Elements elements, I18NAccount messages, Constants constants) {
        this.elements = elements;
        this.messages = messages;
        this.constants = constants;
        vp = new VerticalPanel();

        table = new AccountTable("");
        vp.add(table);

        int row = 0;
        table.setText(row, 0, elements.invoice_template());

        invoiceTemplates = new ListBoxWithErrorText("invoice_templates");
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(invoiceTemplates);

        useButton = new NamedButton("use", elements.use());
        useButton.addClickHandler(this);
        hp.add(useButton);

        table.setWidget(row++, 1, hp);
        table.setHeight("auto");

        /* Inn med alle felter i invoice template ? */
        /* Forslag til antall faktura som skal inn */
        /* Legg til og fjern faktura */
        /* Styre bel¿p */

        dueDate = new TextBoxWithErrorText("due_date");
        table.setText(row, 0, elements.due_date());
        table.setWidget(row++, 1, dueDate);

        amount = new TextBoxWithErrorText("amount");
        table.setText(row, 0, elements.amount());
        table.setWidget(row++, 1, amount);

        reoccuranceInterval = new TextBoxWithErrorText("invoice_reoccurance_interval");
        table.setText(row, 0, elements.invoice_reoccurance_interval());
        table.setWidget(row++, 1, reoccuranceInterval);

        table.setText(row, 0, elements.invoices());
        row++;

        EditableGridDataModel model = new EditableGridDataModel(new Object[][] {}) {
            @Override
            public void addRow(int beforeRow, Object[] row) throws IllegalArgumentException {
                super.addRow(beforeRow, new Object[] {new Date(), "0.00"});
            }
        };
        model.setPageSize(10);

        gridPanel = new GridPanel();
        EditableGrid edibleGrid = gridPanel.createEditableGrid(new String[] { elements.due_date(), elements.amount() },
                new Class[] { DateCell.class, TextBoxCell.class }, model);

        edibleGrid.getElement().setId("invoiceGrid");
        
        gridPanel.setPageNumberBoxDisplayed(true);
        gridPanel.setTotalCountDisplayed(true);
        gridPanel.getTopToolbar().setSaveButtonVisible(false);
        gridPanel.display();
        gridPanel.getGrid().setMultiRowModeEnabled(true);

        HorizontalPanel invoiceButtons = new HorizontalPanel();
        vp.add(invoiceButtons);
        vp.add(gridPanel);

    }

    private GridPanel gridPanel;

    protected JSONArray invoices;

    @Override
    public Widget asWidget() {
        return vp;
    }

    @Override
    public String getTitle() {
        return "Fakturakj¿ring";
    }

    @Override
    public void afterShow() {
        super.afterShow();

        gridPanel.setWidth("100%");
        gridPanel.resize();

        // NativeEvent event = Document.get().createFocusEvent();
        // DomEvent.fireNativeEvent(event, foo);
    }

    @Override
    public void beforeShow() {

        getWizard().setButtonVisible(ButtonType.BUTTON_NEXT, true);
        getWizard().setButtonVisible(ButtonType.BUTTON_CANCEL, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_FINISH, false);
        getWizard().setButtonVisible(ButtonType.BUTTON_PREVIOUS, true);

        loadInvoices();
    }

    private void loadInvoices() {
        ServerResponse response = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                invoices = responseObj.isArray();
                fillInvoicesChoices();
            }
        };
        AuthResponder.get(constants, messages, response, "accounting/invoice_ops.php?action=all");
    }

    protected void fillInvoicesChoices() {
        invoiceTemplates.clear();

        for (int i = 0; i < invoices.size(); i++) {
            JSONObject invoice = invoices.get(i).isObject();

            invoiceTemplates.addItem(Util.str(invoice.get("description")), Util.str(invoice.get("id")));
        }
    }

    @Override
    public PageID getPageID() {
        return PAGEID;
    }

    @Override
    public void beforeNext(NavigationEvent event) {

        super.beforeNext(event);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == useButton) {
            selectInvoice();
        }
    }

    private void selectInvoice() {

    }

}