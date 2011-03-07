package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class RegisterStandards {

    protected int currentYear;
    protected int currentMonth;

    private final Constants constants;
    private final I18NAccount messages;

    private TextBoxWithErrorText attachmentBox;
    private TextBoxWithErrorText postNmbBox;
    private TextBoxWithErrorText dayBox;
    private TextBoxWithErrorText descriptionBox;

    private HTML dateHeader;
    private TextBoxWithErrorText amountBox;
    private TextBoxWithErrorText monthBox;
    private TextBoxWithErrorText yearBox;
    private final Elements elements;
    private final ViewCallback callback;

    public RegisterStandards(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.callback = callback;
        dateHeader = new HTML();
        attachmentBox = new TextBoxWithErrorText("attachment");
        postNmbBox = new TextBoxWithErrorText("postnmb");
    }

    public void fetchInitalData(final boolean fillFields) {

        ServerResponse rh = new ServerResponse() {
            public void serverResponse(JSONValue jsonValue) {

                JSONObject root = jsonValue.isObject();

                String debet = Util.strSkipNull(root.get("debet"));
                String kredit = Util.strSkipNull(root.get("kredit"));
                
                if(!debet.equals(kredit)) {
                    sendToEditLine(Util.str(root.get("line")));
                    return;
                }
                
                currentYear = Util.getInt(root.get("year"));
                currentMonth = Util.getInt(root.get("month"));

                if (fillFields) {
                    fillFields(root);
                }
            }

        };

        AuthResponder.get(constants, messages, rh, "defaults/newline.php");
    }

    protected void sendToEditLine(String str) {
        Window.alert(messages.line_debet_kredit_mismatch());
        callback.openDetails(str);
        
    }

    public boolean validateTop() {

        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] { descriptionBox, attachmentBox, dayBox,
                postNmbBox });

        masterValidator.range(messages.field_to_low_zero(), new Integer(1), null, new Widget[] { attachmentBox,
                postNmbBox });

        masterValidator.day(messages.illegal_day(), currentYear, currentMonth, new Widget[] { dayBox });

        return masterValidator.validateStatus();
    }

    public void setDateHeader() {
        dateHeader.setHTML(Util.monthString(elements, currentMonth) + " " + currentYear);
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public TextBoxWithErrorText getAttachmentBox() {
        attachmentBox.setMaxLength(7);
        attachmentBox.setVisibleLength(7);
        return attachmentBox;
    }

    public TextBoxWithErrorText getPostNmbBox() {
        postNmbBox.setMaxLength(7);
        postNmbBox.setVisibleLength(5);
        return postNmbBox;
    }

    public TextBoxWithErrorText createDayBox() {
        dayBox = new TextBoxWithErrorText("day");
        dayBox.setMaxLength(2);
        dayBox.setVisibleLength(2);
        return dayBox;
    }

    public TextBoxWithErrorText createDayBox(HTML errorLabel, String label) {
        dayBox = new TextBoxWithErrorText(label, errorLabel);
        dayBox.setMaxLength(2);
        dayBox.setVisibleLength(2);
        return dayBox;
    }

    public TextBoxWithErrorText createDescriptionBox() {
        descriptionBox = new TextBoxWithErrorText("description");
        descriptionBox.setMaxLength(40);
        descriptionBox.setVisibleLength(40);
        return descriptionBox;
    }

    public TextBoxWithErrorText createAmountBox() {
        amountBox = new TextBoxWithErrorText("amount");
        amountBox.setVisibleLength(10);
        return amountBox;
    }

    public HTML getDateHeader() {
        return dateHeader;
    }

    public TextBoxWithErrorText createMonthBox(HTML errorLabelForDate) {
        monthBox = new TextBoxWithErrorText("month", errorLabelForDate);
        monthBox.setMaxLength(2);
        monthBox.setVisibleLength(2);
        return monthBox;
    }

    public TextBoxWithErrorText createYearBox(HTML errorLabelForDate) {
        yearBox = new TextBoxWithErrorText("year", errorLabelForDate);
        yearBox.setMaxLength(4);
        yearBox.setVisibleLength(4);
        return yearBox;
    }

    public void fillFields(JSONObject root) {
        setDateHeader();
        attachmentBox.setText(Util.str(root.get("attachment")));
        postNmbBox.setText(Util.str(root.get("postnmb")));
    }
}
//