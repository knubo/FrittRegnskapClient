package no.knubo.accounting.client.views;

import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CountCache;
import no.knubo.accounting.client.cache.HappeningCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.FocusCallback;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ErrorLabelWidget;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.validation.Validateable;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RegisterHappeningView extends Composite implements ClickListener, ChangeListener,
        FocusCallback {

    private static RegisterHappeningView me;

    private static ViewCallback caller;

    public static RegisterHappeningView show(I18NAccount messages, Constants constants,
            ViewCallback caller, Elements elements) {
        RegisterHappeningView.caller = caller;
        if (me == null) {
            me = new RegisterHappeningView(messages, constants, elements);
        }
        return me;
    }

    private final I18NAccount messages;

    private final Constants constants;

    TextBoxWithErrorText dayBox;

    TextBoxWithErrorText attachmentBox;

    TextBoxWithErrorText postNmbBox;

    HTML dateHeader;

    RegisterStandards registerStandards;

    TextBoxWithErrorText descriptionBox;

    ListBoxWithErrorText postListBox;

    TextBoxWithErrorText amountBox;

    IdHolder<String, TextBox> widgetGivesValue;

    HappeningCache happeningCache;

    protected RegisterHappeningView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;

        registerStandards = new RegisterStandards(constants, messages, elements);

        widgetGivesValue = new IdHolder<String, TextBox>();

        VerticalPanel vp = new VerticalPanel();

        dateHeader = registerStandards.getDateHeader();
        dateHeader.addClickListener(this);
        vp.add(dateHeader);

        FlexTable table = new FlexTable();
        table.setStyleName("edittable");
        vp.add(table);

        postNmbBox = registerStandards.getPostNmbBox();
        table.setWidget(0, 1, postNmbBox);
        table.setHTML(0, 0, elements.postnmb());
        Util.setCellId(table, 0, 0, "postnmb");

        dayBox = registerStandards.createDayBox();
        table.setWidget(1, 1, dayBox);
        table.setHTML(1, 0, elements.day());
        Util.setCellId(table, 1, 0, "day");

        attachmentBox = registerStandards.getAttachmentBox();
        table.setWidget(2, 1, attachmentBox);
        table.setHTML(2, 0, elements.attachment());
        Util.setCellId(table, 2, 0, "attachment");

        postListBox = new ListBoxWithErrorText("register_count_post");
        postListBox.getListbox().setMultipleSelect(false);
        postListBox.getListbox().setVisibleItemCount(1);
        postListBox.getListbox().addChangeListener(this);
        table.setWidget(3, 1, postListBox);
        table.setHTML(3, 0, elements.register_count_post());
        Util.setCellId(table, 3, 0, "post");

        descriptionBox = registerStandards.createDescriptionBox();
        table.setWidget(4, 1, descriptionBox);
        table.setHTML(4, 0, elements.description());
        Util.setCellId(table, 4, 0, "description");

        table.setHTML(5, 0, elements.amount());
        amountBox = registerStandards.createAmountBox();
        table.setWidget(5, 1, amountBox);
        Util.setCellId(table, 5, 0, "amount");

        table.setHTML(6, 0, elements.money_type());
        List<String> counts = CountCache.getInstance(constants, messages).getCounts();
        Util.setCellId(table, 6, 0, "money_type");

        int row = 7;
        for (String count : counts) {
            TextBoxWithErrorText numberBox = new TextBoxWithErrorText("number" + count);
            numberBox.setVisibleLength(10);
            table.setHTML(row, 0, count);
            table.setWidget(row, 1, numberBox);
            widgetGivesValue.add(count, numberBox.getTextBox());
            numberBox.addFocusListener(this);
            row++;
        }

        Button saveButton = new NamedButton("RegisterHappening_saveButton", elements
                .RegisterHappening_saveButton());
        saveButton.addClickListener(this);
        table.setWidget(row, 0, saveButton);

        initWidget(vp);
    }

    public void init() {

        postNmbBox.setText("");
        dayBox.setText("");
        attachmentBox.setText("");
        dateHeader.setHTML("...");
        descriptionBox.setText("");
        registerStandards.fetchInitalData(true);

        postListBox.getListbox().clear();
        happeningCache = HappeningCache.getInstance(constants, messages);
        happeningCache.fill(postListBox.getListbox());
    }

    public void onClick(Widget sender) {
        if (!validateSave()) {
            return;
        }
        doSave();
    }

    void doSave() {
        CountCache countCache = CountCache.getInstance(constants, messages);

        StringBuffer sb = new StringBuffer();
        sb.append("action=save");

        Util.addPostParam(sb, "day", dayBox.getText());
        Util.addPostParam(sb, "desc", descriptionBox.getText());
        Util.addPostParam(sb, "attachment", attachmentBox.getText());
        Util.addPostParam(sb, "postnmb", postNmbBox.getText());
        final String money = Util.fixMoney(amountBox.getText());
        Util.addPostParam(sb, "amount", money);
        Util.addPostParam(sb, "post", postListBox.getText());

        for (TextBox textBox : widgetGivesValue.getWidgets()) {

            String value = textBox.getText();
            if (value.length() == 0) {
                continue;
            }

            String id = widgetGivesValue.findId(textBox);
            String key = countCache.getFieldForCount(id);

            Util.addPostParam(sb, key, value);
        }

        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                handleSaveResponse(value);
            }

        };

        AuthResponder.post(constants, messages, callback, sb, "accounting/addhappening.php");

    }

    void handleSaveResponse(JSONValue value) {
        JSONObject object = value.isObject();

        if (object == null) {
            Window.alert(messages.failedConnect());
            return;
        }

        String lineid = Util.str(object.get("id"));
        resetFields();
        caller.openDetails(lineid);
    }

    private void resetFields() {
        dayBox.setText("");
        postListBox.setSelectedIndex(0);
        amountBox.setText("");
        List<TextBox> amountBoxes = widgetGivesValue.getWidgets();

        for (TextBox one : amountBoxes) {

            one.setText("");
        }
    }

    private boolean validateSave() {
        MasterValidator mv = new MasterValidator();

        mv.mandatory(messages.required_field(), new Widget[] { amountBox, descriptionBox,
                postListBox, attachmentBox, dayBox, postNmbBox });

        mv.day(messages.illegal_day(), registerStandards.getCurrentYear(), registerStandards
                .getCurrentMonth(), new Widget[] { dayBox });

        mv.money(messages.field_money(), new Widget[] { amountBox });

        mv.range(messages.field_to_low_zero(), new Integer(1), null, new Widget[] { attachmentBox,
                postNmbBox });

        mv.range(messages.field_positive(), new Integer(0), null, widgetGivesValue.getWidgets());

        return mv.validateStatus();
    }

    public void onChange(Widget sender) {
        if (sender == postListBox.getListbox()) {
            String id = postListBox.getText();

            descriptionBox.setText(happeningCache.getLineDescription(id));
        }
    }

    public void onFocus(Validateable me) {
        /* Not used */
    }

    public void onLostFocus(ErrorLabelWidget me) {
        /* Re-calculate sums */

        double sum = 0;

        for (TextBox widget : widgetGivesValue.getWidgets()) {
            String value = widgetGivesValue.findId(widget);

            if (widget.getText().length() > 0) {
                sum += Double.parseDouble(value) * Integer.parseInt(widget.getText());

            }
        }
        amountBox.getTextBox().setEnabled(sum == 0);
        amountBox.setText(String.valueOf(sum));
    }
}
