package no.knubo.accounting.client.views;

import java.util.Iterator;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.cache.CountCache;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterHappeningView extends Composite implements ClickListener {

    private static RegisterHappeningView me;

    public static RegisterHappeningView show(I18NAccount messages,
            Constants constants, ViewCallback caller) {
        if (me == null) {
            me = new RegisterHappeningView(messages, constants);
        }
        return me;
    }

    private final I18NAccount messages;

    private final Constants constants;

    protected String currentYear;

    protected String currentMonth;

    private FlexTable mainTable;

    private TextBoxWithErrorText dayBox;

    private TextBoxWithErrorText attachmentBox;

    private TextBoxWithErrorText postNmbBox;

    private HTML dateHeader;

    private RegisterStandards registerStandards;

    private TextBoxWithErrorText descriptionBox;

    private ListBox postListBox;

    private TextBoxWithErrorText amountBox;

    private RegisterHappeningView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;

        VerticalPanel vp = new VerticalPanel();

        dateHeader = new HTML();
        dateHeader.addClickListener(this);
        vp.add(dateHeader);

        FlexTable table = new FlexTable();
        table.setStyleName("edittable");
        vp.add(table);

        postNmbBox = new TextBoxWithErrorText();
        postNmbBox.setMaxLength(7);
        postNmbBox.setVisibleLength(5);
        table.setWidget(0, 1, postNmbBox);
        table.setHTML(0, 0, messages.postnmb());

        dayBox = new TextBoxWithErrorText();
        dayBox.setMaxLength(2);
        dayBox.setVisibleLength(2);
        table.setWidget(1, 1, dayBox);
        table.setHTML(1, 0, messages.day());

        attachmentBox = new TextBoxWithErrorText();
        attachmentBox.setMaxLength(7);
        attachmentBox.setVisibleLength(7);
        table.setWidget(2, 1, attachmentBox);
        table.setHTML(2, 0, messages.attachment());

        descriptionBox = new TextBoxWithErrorText();
        descriptionBox.setMaxLength(40);
        descriptionBox.setVisibleLength(40);
        table.setWidget(3, 1, descriptionBox);
        table.setHTML(3, 0, messages.description());

        postListBox = new ListBox();
        postListBox.setMultipleSelect(false);
        postListBox.setVisibleItemCount(1);
        table.setWidget(4, 1, postListBox);
        table.setHTML(4, 0, messages.register_count_post());

        table.setHTML(5, 0, messages.amount());
        amountBox = new TextBoxWithErrorText();
        amountBox.setVisibleLength(10);
        table.setWidget(5, 1, amountBox);


        table.setHTML(6, 0, messages.money_type());
        List counts = CountCache.getInstance(constants).getCounts();

        int row = 7;
        for (Iterator i = counts.iterator(); i.hasNext();) {
            String count = (String) i.next();
            TextBoxWithErrorText numberBox = new TextBoxWithErrorText();
            numberBox.setVisibleLength(10);
            table.setHTML(row, 0, count);
            table.setWidget(row, 1, numberBox);
            row++;
        }

        
        Button saveButton = new Button(messages.save());
        saveButton.addClickListener(this);
        table.setWidget(row, 0, saveButton);
        
        registerStandards = new RegisterStandards(constants, messages,
                dateHeader, attachmentBox, postNmbBox, dayBox, descriptionBox);

        initWidget(vp);
    }

    public void init() {
        postNmbBox.setText("");
        dayBox.setText("");
        attachmentBox.setText("");
        dateHeader.setHTML("...");
        registerStandards.fetchInitalData();
    }

    public void onClick(Widget sender) {

    }
}
