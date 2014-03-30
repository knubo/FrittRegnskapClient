package no.knubo.accounting.client.invoice;

import java.util.Date;
import java.util.HashSet;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.DatePickerButton;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import org.gwt.advanced.client.ui.widget.Calendar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class InvoiceCreatePaper extends Composite implements ClickHandler {

    private static final int STATUS_COLUMN = 5;
    private static final int CHECK_COLUMN = STATUS_COLUMN + 1;
    private static InvoiceCreatePaper me;
    private final I18NAccount messages;
    private final Constants constants;
    private final Elements elements;
    private FlowPanel fp;
    private TextBoxWithErrorText dueDateBox;
    private AccountTable filterTable;
    private ListBoxWithErrorText invoiceBox;
    private NamedButton filterButton;
    private AccountTable invoiceTable;
    private NamedButton createInvoiceButton;
    private DialogBox statusBox;
    private Button closePopupButton;
    private Label invoiceError;

    public static InvoiceCreatePaper getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new InvoiceCreatePaper(messages, constants, elements);
        }
        return me;
    }

    public InvoiceCreatePaper(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        fp = new FlowPanel();

        filterTable = new AccountTable("edittable");
        fp.add(filterTable);

        dueDateBox = new TextBoxWithErrorText("invoice_due_date_before");

        final DatePickerButton picker = new DatePickerButton(new Date()) {

            @Override
            public void onChange(Calendar sender, Date oldValue) {
                super.onChange(sender, oldValue);
                dueDateBox.setText(Util.formatDate(getDate()));
            }
        };

        filterTable.setHeaders(0, elements.invoice_filter_create_paper());
        filterTable.getRowFormatter().addStyleName(0, "large");

        filterTable.setText(1, 0, elements.invoice_due_date_before());

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(dueDateBox);
        hp.add(picker);

        filterTable.setWidget(2, 0, hp);
        filterTable.setText(3, 0, elements.invoice());

        invoiceBox = new ListBoxWithErrorText("invoice");

        filterTable.setWidget(4, 0, invoiceBox);

        filterButton = new NamedButton("filter", elements.filter());
        filterButton.addClickHandler(this);

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(filterButton);

        
        filterTable.setWidget(5, 0, buttons);

        createInvoiceButton = new NamedButton("invoice_create_paper", elements.invoice_create_paper());
        createInvoiceButton.addClickHandler(this);
        buttons.add(createInvoiceButton);

        invoiceError = new Label();
        invoiceError.addStyleName("error");
        
        buttons.add(invoiceError);
        
        invoiceTable = new AccountTable("tableborder");
        invoiceTable.addStyleName("nobreaktable");
        invoiceTable.setHeaders(0, elements.invoice_template(), elements.invoice_due_date(), elements.amount(),
                elements.name(), elements.invoice_odt_template(), elements.status(), elements.selected());

        fp.add(invoiceTable);

        initWidget(fp);
    }

    public void init() {
        invoiceBox.clear();

        loadInvoices();
        filterInvoices();
    }

    private void loadInvoices() {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray invoices = responseObj.isArray();

                fillInvoices(invoices);
            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=invoices_not_sent");
    }

    protected void fillInvoices(JSONArray invoices) {
        invoiceBox.addItem("", "");

        for (int i = 0; i < invoices.size(); i++) {
            JSONObject invoice = invoices.get(i).isObject();
            invoiceBox.addItem(descriptionOfInvoice(invoice), Util.str(invoice.get("id")));
        }
    }

    private String descriptionOfInvoice(JSONObject invoice) {
        return elements.due_date() + ":" + Util.formatDate(invoice.get("due_date")) + ", " + elements.amount() + ":"
                + Util.money(invoice.get("amount"));
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == filterButton) {
            filterInvoices();
        } else if (event.getSource() == createInvoiceButton) {
            createInvoicePaper();
        } else if (event.getSource() == closePopupButton) {
            closePopupButton.setEnabled(false);
            statusBox.hide();
        }
    }

    private void createInvoicePaper() {

        JSONArray chosen = new JSONArray();

        HashSet<String> templates = new HashSet<String>();
        
        int pos = 0;
        for (int i = 1; i < invoiceTable.getRowCount(); i++) {
            CheckBox box = (CheckBox) invoiceTable.getWidget(i, CHECK_COLUMN);

            if (box.getValue()) {
                chosen.set(pos++, new JSONNumber(Integer.parseInt(box.getElement().getId().substring(2))));
            }
            
            templates.add(invoiceTable.getText(i, 4));
        }

        if(templates.size() > 1) {
            Util.timedMessage(invoiceError, messages.invoice_different_template(), 10);
            return;
        }
        
        StringBuffer params = new StringBuffer();
        params.append("action=paper_invoice");
        Util.addPostParam(params, "invoices", chosen.toString());
        Util.addPostParam(params, "invoice_template", templates.iterator().next());
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                /* Response is */
            }
        };
        AuthResponder.post(constants, messages, callback, params, "accounting/invoice_ops.php");

    }

    private void filterInvoices() {

        if (!validate()) {
            return;
        }

        while (invoiceTable.getRowCount() > 1) {
            invoiceTable.removeRow(1);
        }

        StringBuilder url = buildInvoiceURL();

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray arr = responseObj.isArray();

                for (int i = 0; i < arr.size(); i++) {
                    JSONObject invoice = arr.get(i).isObject();
                    int invoiceStatus = Util.getInt(invoice.get("invoice_status"));
                    String invoiceTemplate = Util.strSkipNull(invoice.get("invoice_template"));
                    invoiceTable.setText(
                            i + 1, //
                            Util.str(invoice.get("description")), //
                            Util.formatDate(invoice.get("due_date")), //
                            Util.money(invoice.get("amount")), //
                            Util.str(invoice.get("firstname")) + " " + Util.str(invoice.get("lastname")),
                            invoiceTemplate, InvoiceStatus.invoiceStatus(invoiceStatus));
                    CheckBox box = new CheckBox();
                    box.getElement().setId("i_" + Util.str(invoice.get("id")));

                    if (invoiceTemplate.length() == 0) {
                        box.setEnabled(false);
                    } else if (InvoiceStatus.invoiceNotSent(invoiceStatus)) {
                        box.setValue(true);
                    } else if (!InvoiceStatus.invoiceSent(invoiceStatus)) {
                        box.setEnabled(false);
                    }

                    invoiceTable.setWidget(i + 1, CHECK_COLUMN, box);
                }
            }
        };

        AuthResponder.get(constants, messages, callback, url.toString());
    }

    private boolean validate() {
        MasterValidator mv = new MasterValidator();
        mv.date(messages.date_format(), dueDateBox);

        if (dueDateBox.getText().length() > 0 && invoiceBox.getSelectedIndex() > 0) {
            mv.fail(dueDateBox, true, messages.invoice_select_due_date_or_invoice());
        }

        return mv.validateStatus();
    }

    private StringBuilder buildInvoiceURL() {
        StringBuilder query = new StringBuilder();

        query.append("accounting/invoice_ops.php?action=invoices");

        if (dueDateBox.getText().length() > 0) {
            query.append("&due_date=");
            query.append(dueDateBox.getText());
        }

        String invoice = Util.getSelected(invoiceBox);

        if (invoice.length() > 0) {
            query.append("&invoice=");
            query.append(invoice);
        }

        return query;
    }

}
