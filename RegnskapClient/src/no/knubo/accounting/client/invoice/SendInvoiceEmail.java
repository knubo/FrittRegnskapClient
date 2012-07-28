package no.knubo.accounting.client.invoice;

import java.util.Date;

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

import org.gwt.advanced.client.ui.widget.Calendar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class SendInvoiceEmail extends Composite implements ClickHandler {

    private static SendInvoiceEmail me;
    private final I18NAccount messages;
    private final Constants constants;
    private final Elements elements;
    private FlowPanel fp;
    private TextBoxWithErrorText dueDateBox;
    private AccountTable filterTable;
    private ListBoxWithErrorText invoiceBox;
    private NamedButton filterButton;
    private AccountTable invoices;

    public static SendInvoiceEmail getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new SendInvoiceEmail(messages, constants, elements);
        }
        return me;
    }

    public SendInvoiceEmail(I18NAccount messages, Constants constants, Elements elements) {
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

        filterTable.setHeaders(0, elements.invoice_filter());
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

        filterTable.setWidget(5, 0, filterButton);

        invoices = new AccountTable("tableborder");
        invoices.addStyleName("nobreaktable");
        invoices.setHeaders(0, elements.invoice_template(), elements.invoice_due_date(), elements.amount(),
                elements.status(), elements.selected());

        fp.add(invoices);

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
        }
    }

    private void filterInvoices() {
        while (invoices.getRowCount() > 1) {
            invoices.removeRow(1);
        }

        StringBuilder url = buildInvoiceURL();

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray arr = responseObj.isArray();

                for (int i = 0; i < arr.size(); i++) {
                    JSONObject invoice = arr.get(i).isObject();
                    invoices.setText(i + 1, //
                            Util.str(invoice.get("description")), //
                            Util.formatDate(invoice.get("due_date")), //
                            Util.money(invoice.get("amount")), //
                            invoiceStatus(Util.getInt(invoice.get("invoice_status"))));
                    CheckBox box = new CheckBox();
                    box.getElement().setId("invoice_" + Util.str(invoice.get("id")));
                    box.setValue(true);
                    invoices.setWidget(i + 1, 4, box);
                }
            }
        };
        AuthResponder.get(constants, messages, callback, url.toString());
    }

    protected String invoiceStatus(int int1) {
        switch (int1) {
        case 1:
            return "Ikke sent";
        case 2:
            return "Sent";
        case 3:
            return "Slettet";
        }
        return "???" + int1;
    }

    private StringBuilder buildInvoiceURL() {
        StringBuilder query = new StringBuilder();
        query.append("accounting/invoice_ops.php?action=invoices");
        return query;
    }

}
