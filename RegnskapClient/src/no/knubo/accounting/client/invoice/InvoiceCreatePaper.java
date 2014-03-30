package no.knubo.accounting.client.invoice;

import java.util.Date;
import java.util.HashMap;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.Luhn;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.DatePickerButton;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import org.gwt.advanced.client.ui.widget.Calendar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
    private NamedButton previewButton;

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

        previewButton = new NamedButton("preview", elements.preview());
        previewButton.addClickHandler(this);
        buttons.add(previewButton);

        createInvoiceButton = new NamedButton("invoice_create_paper", elements.invoice_create_paper());
        createInvoiceButton.addClickHandler(this);
        buttons.add(createInvoiceButton);

        invoiceTable = new AccountTable("tableborder");
        invoiceTable.addStyleName("nobreaktable");
        invoiceTable.setHeaders(0, elements.invoice_template(), elements.invoice_due_date(), elements.amount(), elements.name(),
                elements.email(), elements.status(), elements.selected());

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
        } else if (event.getSource() == previewButton) {
            preview();
        }
    }

    private void createInvoicePaper() {
        
    }

    private void preview() {

        CheckBox box = (CheckBox) invoiceTable.getWidget(1, CHECK_COLUMN);
        String id = box.getElement().getId();
        String[] parts = id.split("_");

        final String invoiceId = parts[1];
        final String invoiceTypeId = parts[2];
        final String personId = parts[3];

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject invoiceTemplate = responseObj.isObject();

                actualPreviewInvoice(invoiceTemplate, personId, invoiceTypeId, invoiceId);

            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=emailtemplate&id=" + invoiceTypeId);

    }

    void actualPreviewInvoice(JSONObject invoiceTemplate, String personId, String invoiceTypeId, String invoiceId) {
        StringBuffer sb = new StringBuffer();

        String email = invoiceTable.getText(1, 5);
        String amount = invoiceTable.getText(1, 2);
        String dueDate = invoiceTable.getText(1, 1);

        sb.append("action=preview");
        Util.addPostParam(sb, "personid", personId);
        Util.addPostParam(sb, "email", replaceParameters(email, amount, invoiceId, invoiceTypeId, dueDate));
        Util.addPostParam(sb, "subject", URL.encode(Util.str(invoiceTemplate.get("email_subject"))));
        Util.addPostParam(sb, "body",
                URL.encode(replaceParameters(Util.str(invoiceTemplate.get("email_body")), amount, invoiceId, invoiceTypeId, dueDate)));
        Util.addPostParam(sb, "format", Util.str(invoiceTemplate.get("email_format")));
        Util.addPostParam(sb, "header", Util.strSkipNull(invoiceTemplate.get("email_header")));
        Util.addPostParam(sb, "footer", Util.strSkipNull(invoiceTemplate.get("email_footer")));

        ServerResponseString callback = new ServerResponseString() {

            @Override
            public void serverResponse(String response) {

                DialogBox popup = new DialogBox();
                popup.setText(elements.preview_actual());
                popup.setAutoHideEnabled(true);
                popup.setModal(true);

                VerticalPanel vp = new VerticalPanel();

                ScrollPanel sp = new ScrollPanel();
                vp.add(sp);

                sp.setWidth("800px");
                sp.setHeight("40em");
                sp.add(new HTML(response));

                popup.add(vp);
                popup.center();
            }

            @Override
            public void serverResponse(JSONValue responseObj) {
                /* unused */
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "reports/email.php");
    }

    private String replaceParameters(String email, String amount, String invoiceId, String invoiceTypeId, String dueDate) {
        String result = email.replace("{amount}", amount);
        result = result.replace("{due_date}", dueDate);
        result = result.replace("{invoice}",
                invoiceTypeId + Luhn.generateDigit(invoiceTypeId) + "-" + invoiceId + Luhn.generateDigit(invoiceId));

        return result;
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
                    invoiceTable.setText(
                            i + 1, //
                            Util.str(invoice.get("description")), //
                            Util.formatDate(invoice.get("due_date")), //
                            Util.money(invoice.get("amount")), //
                            Util.str(invoice.get("firstname")) + " " + Util.str(invoice.get("lastname")), Util.str(invoice.get("email")),
                            InvoiceStatus. invoiceStatus(invoiceStatus));
                    CheckBox box = new CheckBox();
                    box.getElement().setId(
                            "i_" + Util.str(invoice.get("id")) + "_" + Util.str(invoice.get("template_id")) + "_"
                                    + Util.str(invoice.get("person_id")));
                    
                    if (InvoiceStatus.invoiceNotSent(invoiceStatus)) {
                        box.setValue(true);
                    } else if(!InvoiceStatus.invoiceSent(invoiceStatus)) {
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
