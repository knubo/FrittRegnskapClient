package no.knubo.accounting.client.invoice;

import java.util.Date;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

public class InvoiceSearchView extends Composite implements ClickHandler {

    private static InvoiceSearchView me;

    private I18NAccount messages;
    private Constants constants;
    private Elements elements;
    private AccountTable searchTable;
    private TextBoxWithErrorText fromDateBox;
    private TextBoxWithErrorText invoiceBox;
    private NamedButton searchButton;
    private AccountTable invoiceTable;

    private TextBoxWithErrorText toDateBox;
    private ListBoxWithErrorText statusComboBox;

    private NamedButton clearButton;

    private TextBoxWithErrorText firstnameBox;

    private TextBoxWithErrorText lastnameBox;

    public static InvoiceSearchView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new InvoiceSearchView(messages, constants, elements);
        }
        return me;
    }

    public InvoiceSearchView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        FlowPanel fp = new FlowPanel();

        searchTable = new AccountTable("edittable");
        fp.add(searchTable);

        fromDateBox = new TextBoxWithErrorText("from_date");
        toDateBox = new TextBoxWithErrorText("from_date");

        final DatePickerButton fromPicker = new DatePickerButton(new Date()) {

            @Override
            public void onChange(Calendar sender, Date oldValue) {
                super.onChange(sender, oldValue);
                fromDateBox.setText(Util.formatDate(getDate()));
            }
        };
        final DatePickerButton toPicker = new DatePickerButton(new Date()) {

            @Override
            public void onChange(Calendar sender, Date oldValue) {
                super.onChange(sender, oldValue);
                toDateBox.setText(Util.formatDate(getDate()));
            }
        };

        searchTable.setHeadingWithColspan(0, 4, elements.menuitem_invoice_search());
        searchTable.getRowFormatter().addStyleName(0, "large");

        searchTable.setText(1, 0, elements.from_date());
        searchTable.setText(1, 2, elements.to_date());

        HorizontalPanel fromHP = new HorizontalPanel();
        fromHP.add(fromDateBox);
        fromHP.add(fromPicker);

        HorizontalPanel toHP = new HorizontalPanel();
        toHP.add(toDateBox);
        toHP.add(toPicker);

        searchTable.setWidget(1, 1, fromHP);
        searchTable.setWidget(1, 3, toHP);

        searchTable.setText(2, 0, elements.invoice());
        invoiceBox = new TextBoxWithErrorText("invoice");
        searchTable.setWidget(2, 1, invoiceBox);

        searchTable.setText(2, 2, elements.status());
        statusComboBox = new ListBoxWithErrorText("status");
        statusComboBox.addItem("", "");
        InvoiceStatus.fill(statusComboBox);

        searchTable.setWidget(2, 3, statusComboBox);

        firstnameBox = new TextBoxWithErrorText("firstname");
        searchTable.setText(3, 0, elements.firstname());
        searchTable.setWidget(3, 1, firstnameBox);

        lastnameBox = new TextBoxWithErrorText("lastname");
        searchTable.setText(3, 2, elements.lastname());
        searchTable.setWidget(3, 3, lastnameBox);

        searchButton = new NamedButton("search", elements.search());
        searchButton.addClickHandler(this);

        clearButton = new NamedButton("clear", elements.clear());
        clearButton.addClickHandler(this);

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(searchButton);
        buttons.add(clearButton);

        searchTable.setWidget(5, 0, buttons);

        invoiceTable = new AccountTable("tableborder");
        invoiceTable.addStyleName("nobreaktable");
        invoiceTable.setHeaders(0, elements.invoice_template(), elements.invoice_due_date(), elements.amount(), elements.name(),
                elements.email(), elements.status(), "");

        fp.add(invoiceTable);

        initWidget(fp);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == clearButton) {
            clear();
        } else if (event.getSource() == searchButton) {
            search();
        } else if (event.getSource() instanceof Image) {
            Image im = (Image) event.getSource();
            editInvoice(im.getElement().getId());
        }
    }

    private void editInvoice(String id) {
        final String receiverId = id.substring("receiver_".length());

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {

            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=invoice&receiver_id=" + receiverId);
    }

    private void search() {
        removeInvoiceResult();

        StringBuffer sb = new StringBuffer();
        sb.append("action=search");
        Util.addPostParam(sb, "from_date", fromDateBox.getText());
        Util.addPostParam(sb, "to_date", toDateBox.getText());
        Util.addPostParam(sb, "invoice", invoiceBox.getText());
        Util.addPostParam(sb, "status", Util.getSelected(statusComboBox));
        Util.addPostParam(sb, "firstname", firstnameBox.getText());
        Util.addPostParam(sb, "lastname", lastnameBox.getText());

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray arr = responseObj.isArray();

                for (int i = 0; i < arr.size(); i++) {
                    JSONObject invoice = arr.get(i).isObject();
                    invoiceTable.setText(
                            i + 1, //
                            Util.str(invoice.get("description")), //
                            Util.formatDate(invoice.get("due_date")), //
                            Util.money(invoice.get("amount")), //
                            Util.str(invoice.get("firstname")) + " " + Util.str(invoice.get("lastname")), Util.str(invoice.get("email")),
                            InvoiceStatus.invoiceStatus(Util.getInt(invoice.get("invoice_status"))));

                    Image editImage = ImageFactory.editImage("receiver_" + Util.str(invoice.get("id")));
                    editImage.addClickHandler(me);
                    invoiceTable.setWidget(i + 1, 6, editImage);
                }
            }
        };
        AuthResponder.post(constants, messages, callback, sb, "accounting/invoice_ops.php");
    }

    private void removeInvoiceResult() {
        while (invoiceTable.getRowCount() > 1) {
            invoiceTable.removeRow(1);
        }
    }

    private void clear() {
        fromDateBox.setText("");
        toDateBox.setText("");
        statusComboBox.setSelectedIndex(0);
        invoiceBox.setText("");
        firstnameBox.setText("");
        lastnameBox.setText("");

        removeInvoiceResult();

    }
}
