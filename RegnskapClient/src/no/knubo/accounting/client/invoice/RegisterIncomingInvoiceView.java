package no.knubo.accounting.client.invoice;

import java.util.Date;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.Luhn;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.DatePickerButton;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import org.gwt.advanced.client.ui.widget.Calendar;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegisterIncomingInvoiceView extends Composite implements ClickHandler {

    private static final int CHECK_COLUMN = 6;

    private static RegisterIncomingInvoiceView me;
    private final I18NAccount messages;
    private final Constants constants;
    private TextBoxWithErrorText invoiceBox;
    private TextBoxWithErrorText dueDateBox;
    private TextBoxWithErrorText amountBox;
    private TextBoxWithErrorText firstNameBox;
    private TextBoxWithErrorText lastNameBox;
    private NamedButton searchButton;
    private AccountTable invoiceTable;

    public static RegisterIncomingInvoiceView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new RegisterIncomingInvoiceView(messages, constants, elements);
        }
        return me;
    }

    public RegisterIncomingInvoiceView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        VerticalPanel vp = new VerticalPanel();

        AccountTable queryTable = new AccountTable("edittable");

        queryTable.setHeadingWithColspan(0, 4, elements.menuitem_invoice_register());
        queryTable.getRowFormatter().addStyleName(0, "large");

        queryTable.setText(1, 0, elements.invoice());
        invoiceBox = new TextBoxWithErrorText("invoice");
        queryTable.setWidget(1, 1, invoiceBox);

        dueDateBox = new TextBoxWithErrorText("invoice_due_date_before");
        dueDateBox.setMaxLength(10);
        queryTable.setText(2, 2, elements.due_date());

        amountBox = new TextBoxWithErrorText("amount");
        queryTable.setText(2, 0, elements.amount());
        queryTable.setWidget(2, 1, amountBox);

        final DatePickerButton picker = new DatePickerButton(new Date()) {

            @Override
            public void onChange(Calendar sender, Date oldValue) {
                super.onChange(sender, oldValue);
                dueDateBox.setText(Util.formatDate(getDate()));
            }
        };
        HorizontalPanel duePanel = new HorizontalPanel();
        duePanel.add(dueDateBox);
        duePanel.add(picker);
        queryTable.setWidget(2, 3, duePanel);

        firstNameBox = new TextBoxWithErrorText("firstname");
        queryTable.setText(3, 0, elements.firstname());
        queryTable.setWidget(3, 1, firstNameBox);

        lastNameBox = new TextBoxWithErrorText("lastname");
        queryTable.setText(3, 2, elements.lastname());
        queryTable.setWidget(3, 3, lastNameBox);

        HorizontalPanel buttonPanel = new HorizontalPanel();

        searchButton = new NamedButton("search", elements.search());
        searchButton.addClickHandler(this);
        buttonPanel.add(searchButton);

        queryTable.setWidget(4, 0, buttonPanel);
        queryTable.getFlexCellFormatter().setColSpan(4, 0, 4);

        vp.add(queryTable);

        invoiceTable = new AccountTable("tableborder");
        invoiceTable.setHeaders(0, elements.invoice(), elements.due_date(), elements.amount(), elements.name(),
                elements.email(), elements.invoice(), elements.process());

        vp.add(invoiceTable);

        initWidget(vp);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == searchButton) {
            search();
        }
    }

    private boolean validate() {
        MasterValidator mv = new MasterValidator();

        mv.date(messages.date_format(), dueDateBox);

        if(amountBox.getText().length() > 0) {
            mv.money(messages.field_money(), amountBox);
        }

        if (!mv.validateStatus()) {
            return false;
        }

        String invoice = invoiceBox.getText();
        if (invoice.length() == 0) {
            return true;
        }

        String[] parts = invoice.split("-");

        if (parts.length != 2) {
            mv.fail(invoiceBox, true, messages.invoice_format());

            return mv.validateStatus();
        }

        String digit = Luhn.generateDigit(parts[0]);

        mv.fail(invoiceBox, !digit.equals(parts[1]), messages.invoice_luhn_bad());

        return mv.validateStatus();
    }

    private void search() {
        if (!validate()) {
            return;
        }

        while (invoiceTable.getRowCount() > 1) {
            invoiceTable.removeRow(1);
        }

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray arr = responseObj.isArray();

                for (int i = 0; i < arr.size(); i++) {
                    JSONObject invoice = arr.get(i).isObject();

                    String id = Util.str(invoice.get("id"));

                    invoiceTable.setText(
                            i + 1, //
                            Util.str(invoice.get("description")), //
                            Util.formatDate(invoice.get("due_date")), //
                            Util.money(invoice.get("amount")), //
                            Util.str(invoice.get("firstname")) + " " + Util.str(invoice.get("lastname")),
                            Util.strSkipNull(invoice.get("email")), id + "-" + Luhn.generateDigit(id),
                            Util.str(invoice.get("email")));

                    invoiceTable.getRowFormatter().addStyleName(i + 1, "nobreaktable");

                    Image editImage = ImageFactory.chooseImage("receiver_" + Util.str(invoice.get("id")));
                    editImage.addClickHandler(me);
                    invoiceTable.setWidget(i + 1, CHECK_COLUMN, editImage);
                }

            }
        };

        StringBuffer sb = new StringBuffer();
        sb.append("action=search");

        Util.addPostParam(sb, "status", InvoiceStatus.INVOICE_SENT);
        Util.addPostParam(sb, "invoice", invoiceBox.getText());
        Util.addPostParam(sb, "amount", amountBox.getText());
        Util.addPostParam(sb, "due_date", dueDateBox.getText());
        Util.addPostParam(sb, "firstname", firstNameBox.getText());
        Util.addPostParam(sb, "lastname", lastNameBox.getText());

        AuthResponder.post(constants, messages, callback, sb, "accounting/invoice_ops.php");
    }

}
