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

public class SendInvoiceEmailView extends Composite implements ClickHandler {

    private static final int STATUS_COLUMN = 5;
    private static final int CHECK_COLUMN = STATUS_COLUMN + 1;
    private static SendInvoiceEmailView me;
    private final I18NAccount messages;
    private final Constants constants;
    private final Elements elements;
    private FlowPanel fp;
    private TextBoxWithErrorText dueDateBox;
    private AccountTable filterTable;
    private ListBoxWithErrorText invoiceBox;
    private NamedButton filterButton;
    private AccountTable invoiceTable;
    private NamedButton sendInvoiceButton;
    private HashMap<String, JSONObject> invoiceTypeCache = new HashMap<String, JSONObject>();
    private DialogBox statusBox;
    private AccountTable statusTable;
    private Button closePopupButton;
    private NamedButton previewButton;

    public static SendInvoiceEmailView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new SendInvoiceEmailView(messages, constants, elements);
        }
        return me;
    }

    public SendInvoiceEmailView(I18NAccount messages, Constants constants, Elements elements) {
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

        filterTable.setHeaders(0, elements.invoice_filter_send_email());
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

        sendInvoiceButton = new NamedButton("invoice_send_email", elements.invoice_send_email());
        sendInvoiceButton.addClickHandler(this);
        buttons.add(sendInvoiceButton);

        invoiceTable = new AccountTable("tableborder");
        invoiceTable.addStyleName("nobreaktable");
        invoiceTable.setHeaders(0, elements.invoice_template(), elements.invoice_due_date(), elements.amount(),
                elements.name(), elements.email(), elements.status(), elements.selected());

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
        } else if (event.getSource() == sendInvoiceButton) {
            boolean cont = Window.confirm(messages.invoice_confirm_send());
            if (cont) {
                sendInvoiceEmail();
            }
        } else if (event.getSource() == closePopupButton) {
            closePopupButton.setEnabled(false);
            statusBox.hide();
        } else if (event.getSource() == previewButton) {
            preview();
        }
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

                actualPreviewInvoice(invoiceTemplate, personId, invoiceId);

            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=emailtemplate&id="
                + invoiceTypeId);

    }

    void actualPreviewInvoice(JSONObject invoiceTemplate, String personId, String invoiceId) {
        StringBuffer sb = new StringBuffer();

        String email = invoiceTable.getText(1, 5);
        String amount = invoiceTable.getText(1, 2);
        String dueDate = invoiceTable.getText(1, 1);

        sb.append("action=preview");
        Util.addPostParam(sb, "personid", personId);
        Util.addPostParam(sb, "email", replaceParameters(email, amount, invoiceId, dueDate));
        Util.addPostParam(sb, "subject", URL.encode(Util.str(invoiceTemplate.get("email_subject"))));
        Util.addPostParam(sb, "body", URL.encode(replaceParameters(Util.str(invoiceTemplate.get("email_body")), amount,
                invoiceId, dueDate)));
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

    private void sendInvoiceEmail() {
        invoiceTypeCache.clear();

        setupStatusBox();

        int row = 1;
        sendForRow(row);
    }

    private void sendForRow(int row) {

        if (!closePopupButton.isEnabled()) {
            statusBox.hide();
            return;
        }

        if (invoiceTable.getRowCount() == row) {
            sendingComplete();
            return;
        }

        CheckBox box = (CheckBox) invoiceTable.getWidget(row, CHECK_COLUMN);

        if (box.getValue() && box.isEnabled()) {
            String id = box.getElement().getId();
            String[] parts = id.split("_");

            sendInvoice(row, box, parts[1], parts[2], parts[3]);
        } else {
            sendForRow(row + 1);
        }
    }

    private void sendingComplete() {
        closePopupButton.setText(elements.close());
    }

    private void setupStatusBox() {
        statusBox = new DialogBox();
        statusBox.setText(elements.invoice_sent_email_status());
        statusTable = new AccountTable("tableborder");

        statusTable.setHeaders(0, elements.name(), elements.email(), elements.status());

        FlowPanel fp = new FlowPanel();

        closePopupButton = new Button();
        closePopupButton.addClickHandler(this);
        closePopupButton.setText(elements.abort());

        fp.add(closePopupButton);
        fp.add(statusTable);

        statusBox.setWidget(fp);

        statusBox.center();
    }

    private void sendInvoice(final int row, final CheckBox box, String invoiceId, String invoiceTypeId, String personId) {
        JSONObject invoiceType = getInvoiceType(row, box, invoiceId, invoiceTypeId, personId);

        if (invoiceType == null) {
            /* Called after invoice is cached */
            return;
        }

        String email = invoiceTable.getText(row, 5);
        String amount = invoiceTable.getText(row, 2);
        String dueDate = invoiceTable.getText(row, 1);

        if (!Util.getBoolean(invoiceType.get("emailOK"))) {
            statusTable.insertRow(1);
            statusTable.setText(1, invoiceTable.getText(row, 3), invoiceTable.getText(row, 4),
                    elements.invoice_status_not_sent());

            invoiceTable.setText(row, STATUS_COLUMN, elements.invoice_template_not_ready());
            sendForRow(row + 1);
            return;
        }

        if (!closePopupButton.isEnabled()) {
            statusBox.hide();
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("action=email");
        Util.addPostParam(sb, "personid", personId);
        Util.addPostParam(sb, "email", email);
        Util.addPostParam(sb, "subject", URL.encode(Util.str(invoiceType.get("email_subject"))));
        Util.addPostParam(sb, "body", URL.encode(replaceParameters(Util.str(invoiceType.get("email_body")), amount,
                invoiceId, dueDate)));
        Util.addPostParam(sb, "format", Util.str(invoiceType.get("email_format")));
        Util.addPostParam(sb, "header", Util.strSkipNull(invoiceType.get("email_header")));
        Util.addPostParam(sb, "footer", Util.strSkipNull(invoiceType.get("email_footer")));

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                sendCompleted(row, box, Util.getBoolean(responseObj.isObject().get("status")));
            }
        };
        AuthResponder.post(constants, messages, callback, sb, "reports/email.php");

    }

    private String replaceParameters(String email, String amount, String invoiceId, String dueDate) {
        String result = email.replace("{amount}", amount);
        result = result.replace("{due_date}", dueDate);
        result = result.replace("{invoice}", invoiceId + "-" + Luhn.generateDigit(invoiceId));

        return result;
    }

    private void sendCompleted(final int row, CheckBox box, boolean success) {
        statusTable.insertRow(1);
        statusTable.setText(1, invoiceTable.getText(row, 3), invoiceTable.getText(row, 4), success ? elements.ok()
                : elements.failed());

        if (success) {
            invoiceTable.setText(row, STATUS_COLUMN, elements.invoice_status_sent());
            box.setEnabled(false);
        }
        Timer t = new Timer() {

            @Override
            public void run() {
                sendForRow(row + 1);
            }
        };
        t.schedule(1000);
    }

    private JSONObject getInvoiceType(int row, CheckBox box, String invoiceId, String invoiceTypeId, String personId) {
        JSONObject invoiceType = invoiceTypeCache.get(invoiceTypeId);

        if (invoiceType == null) {
            fetchInvoiceType(row, box, invoiceId, invoiceTypeId, personId);
            return null;
        }

        return invoiceType;
    }

    private void fetchInvoiceType(final int row, final CheckBox box, final String invoiceId,
            final String invoiceTypeId, final String personId) {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                if (responseObj.isObject() == null) {
                    Util.log("Got null object for " + invoiceTypeId);
                    return;
                }

                invoiceTypeCache.put(invoiceTypeId, responseObj.isObject());
                sendInvoice(row, box, invoiceId, invoiceTypeId, personId);
            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=emailtemplate&id="
                + invoiceTypeId);
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
                            Util.str(invoice.get("firstname")) + " " + Util.str(invoice.get("lastname")),
                            Util.str(invoice.get("email")), InvoiceStatus.invoiceStatus(invoiceStatus));
                    CheckBox box = new CheckBox();
                    box.getElement().setId(
                            "i_" + Util.str(invoice.get("id")) + "_" + Util.str(invoice.get("template_id")) + "_"
                                    + Util.str(invoice.get("person_id")));

                    if (InvoiceStatus.invoiceNotSent(invoiceStatus)) {
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
