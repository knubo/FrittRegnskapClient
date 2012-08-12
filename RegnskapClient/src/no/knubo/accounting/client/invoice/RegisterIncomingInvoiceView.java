package no.knubo.accounting.client.invoice;

import java.util.Date;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.DatePickerButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import org.gwt.advanced.client.ui.widget.Calendar;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegisterIncomingInvoiceView extends Composite {

    private static RegisterIncomingInvoiceView me;
    private final I18NAccount messages;
    private final Constants constants;
    private final Elements elements;
    private TextBoxWithErrorText invoiceInput;
    private TextBoxWithErrorText dueDateBox;
    private TextBoxWithErrorText amountBox;
    private TextBoxWithErrorText firstNameBox;
    private TextBoxWithErrorText lastNameBox;

    public static RegisterIncomingInvoiceView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new RegisterIncomingInvoiceView(messages, constants, elements);
        }
        return me;
    }

    public RegisterIncomingInvoiceView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        VerticalPanel vp = new VerticalPanel();

        AccountTable queryTable = new AccountTable("edittable");
        queryTable.setText(1, 0, elements.invoice());
        invoiceInput = new TextBoxWithErrorText("invoice");
        queryTable.setWidget(1, 1, invoiceInput);

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
        
        
        vp.add(queryTable);

        AccountTable resultTable = new AccountTable("tableborder");
        resultTable.setHeaders(0, elements.invoice(), elements.name(), elements.due_date(), elements.action());

        vp.add(resultTable);

        initWidget(vp);
    }

}
