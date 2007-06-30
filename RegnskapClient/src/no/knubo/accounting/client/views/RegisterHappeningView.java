package no.knubo.accounting.client.views;

import java.util.Iterator;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CountCache;
import no.knubo.accounting.client.cache.HappeningCache;
import no.knubo.accounting.client.misc.ErrorLabelWidget;
import no.knubo.accounting.client.misc.FocusCallback;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ListBoxWithErrorText;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.validation.Validateable;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
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

public class RegisterHappeningView extends Composite implements ClickListener,
        ChangeListener, FocusCallback {

    private static RegisterHappeningView me;

    private static ViewCallback caller;

    public static RegisterHappeningView show(I18NAccount messages,
            Constants constants, ViewCallback caller) {
        RegisterHappeningView.caller = caller;
        if (me == null) {
            me = new RegisterHappeningView(messages, constants);
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

    IdHolder widgetGivesValue;

    HappeningCache happeningCache;

    protected RegisterHappeningView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;

        registerStandards = new RegisterStandards(constants, messages);

        widgetGivesValue = new IdHolder();

        VerticalPanel vp = new VerticalPanel();

        dateHeader = registerStandards.getDateHeader();
        dateHeader.addClickListener(this);
        vp.add(dateHeader);

        FlexTable table = new FlexTable();
        table.setStyleName("edittable");
        vp.add(table);

        postNmbBox = registerStandards.getPostNmbBox();
        table.setWidget(0, 1, postNmbBox);
        table.setHTML(0, 0, messages.postnmb());
        Util.setCellId(table, 0, 0, "postnmb");

        dayBox = registerStandards.createDayBox();
        table.setWidget(1, 1, dayBox);
        table.setHTML(1, 0, messages.day());
        Util.setCellId(table, 1, 0, "day");

        attachmentBox = registerStandards.getAttachmentBox();
        table.setWidget(2, 1, attachmentBox);
        table.setHTML(2, 0, messages.attachment());
        Util.setCellId(table, 2, 0, "attachment");

        postListBox = new ListBoxWithErrorText("postnmb");
        postListBox.getListbox().setMultipleSelect(false);
        postListBox.getListbox().setVisibleItemCount(1);
        postListBox.getListbox().addChangeListener(this);
        table.setWidget(3, 1, postListBox);
        table.setHTML(3, 0, messages.register_count_post());
        Util.setCellId(table, 3, 0, "post");

        descriptionBox = registerStandards.createDescriptionBox();
        table.setWidget(4, 1, descriptionBox);
        table.setHTML(4, 0, messages.description());
        Util.setCellId(table, 4, 0, "description");

        table.setHTML(5, 0, messages.amount());
        amountBox = registerStandards.createAmountBox();
        table.setWidget(5, 1, amountBox);
        Util.setCellId(table, 5, 0, "amount");

        table.setHTML(6, 0, messages.money_type());
        List counts = CountCache.getInstance(constants).getCounts();
        Util.setCellId(table, 6, 0, "RegisterHappening.MoneyType");

        int row = 7;
        for (Iterator i = counts.iterator(); i.hasNext();) {
            String count = (String) i.next();
            TextBoxWithErrorText numberBox = new TextBoxWithErrorText("number"
                    + count);
            numberBox.setVisibleLength(10);
            table.setHTML(row, 0, count);
            table.setWidget(row, 1, numberBox);
            widgetGivesValue.add(count, numberBox.getTextBox());
            numberBox.addFocusListener(this);
            row++;
        }

        Button saveButton = new NamedButton("RegisterHappening.saveButton",
                messages.save());
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
        happeningCache = HappeningCache.getInstance(constants);
        happeningCache.fill(postListBox.getListbox());
    }

    public void onClick(Widget sender) {
        if (!validateSave()) {
            return;
        }
        doSave();
    }

    void doSave() {
        CountCache countCache = CountCache.getInstance(constants);

        StringBuffer sb = new StringBuffer();
        sb.append("action=save");

        Util.addPostParam(sb, "day", dayBox.getText());
        Util.addPostParam(sb, "desc", descriptionBox.getText());
        Util.addPostParam(sb, "attachment", attachmentBox.getText());
        Util.addPostParam(sb, "postnmb", postNmbBox.getText());
        final String money = Util.fixMoney(amountBox.getText());
        Util.addPostParam(sb, "amount", money);
        Util.addPostParam(sb, "post", postListBox.getText());

        for (Iterator i = widgetGivesValue.getWidgets().iterator(); i.hasNext();) {
            TextBox textBox = (TextBox) i.next();

            String value = textBox.getText();
            if (value.length() == 0) {
                continue;
            }

            String id = widgetGivesValue.findId(textBox);
            String key = countCache.getFieldForCount(id);

            Util.addPostParam(sb, key, value);
        }

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "accounting/addhappening.php");

        RequestCallback callback = new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                Window.alert(exception.getMessage());
            }

            public void onResponseReceived(Request request, Response response) {
                handleSaveResponse(response);
            }

        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(sb.toString(), callback);
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    void handleSaveResponse(Response response) {
        if (response.getText() == null || response.getText().length() == 0) {
            Window.alert(messages.failedConnect());
            return;
        }

        JSONValue value = JSONParser.parse(response.getText());

        if (value == null) {
            Window.alert(messages.failedConnect());
            return;
        }

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
        List amountBoxes = widgetGivesValue.getWidgets();

        for (Iterator i = amountBoxes.iterator(); i.hasNext();) {
            TextBox one = (TextBox) i.next();

            one.setText("");
        }
    }

    private boolean validateSave() {
        MasterValidator mv = new MasterValidator();

        mv.mandatory(messages.required_field(),
                new Widget[] { amountBox, descriptionBox, postListBox,
                        attachmentBox, dayBox, postNmbBox });

        mv.day(messages.illegal_day(), Integer.parseInt(registerStandards
                .getCurrentYear()), Integer.parseInt(registerStandards
                .getCurrentMonth()), new Widget[] { dayBox });

        mv.money(messages.field_money(), new Widget[] { amountBox });

        mv.range(messages.field_to_low_zero(), new Integer(1), null,
                new Widget[] { attachmentBox, postNmbBox });

        mv.range(messages.field_positive(), new Integer(0), null,
                widgetGivesValue.getWidgets());

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
        /* Recalc sums */

        double sum = 0;

        for (Iterator i = widgetGivesValue.getWidgets().iterator(); i.hasNext();) {
            TextBox widget = (TextBox) i.next();
            String value = widgetGivesValue.findId(widget);

            if (widget.getText().length() > 0) {
                sum += Double.parseDouble(value)
                        * Integer.parseInt(widget.getText());

            }
        }
        amountBox.getTextBox().setEnabled(sum == 0);
        amountBox.setText(String.valueOf(sum));
    }
}
