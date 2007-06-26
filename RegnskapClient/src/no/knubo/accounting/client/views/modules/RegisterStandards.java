package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class RegisterStandards {

    protected String currentYear;
    protected String currentMonth;

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

    public RegisterStandards(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        dateHeader = new HTML();
        attachmentBox = new TextBoxWithErrorText();
        postNmbBox = new TextBoxWithErrorText();
    }

    public void fetchInitalData(final boolean fillFields) {

        ResponseTextHandler rh = new ResponseTextHandler() {
            public void onCompletion(String responseText) {
                JSONValue jsonValue = JSONParser.parse(responseText);

                JSONObject root = jsonValue.isObject();

                currentYear = Util.str(root.get("year"));
                currentMonth = Util.str(root.get("month"));

                if (fillFields) {
                    setDateHeader();
                    attachmentBox.setText(Util.str(root.get("attachment")));
                    postNmbBox.setText(Util.str(root.get("postnmb")));
                }
            }

        };
        // TODO Report stuff as being loaded.
        if (!HTTPRequest.asyncGet(constants.baseurl() + "defaults/newline.php",
                rh)) {
            Window.alert(messages.failedConnect());
        }
    }

    public boolean validateTop() {

        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] {
                descriptionBox, attachmentBox, dayBox, postNmbBox });

        masterValidator.range(messages.field_to_low_zero(), new Integer(1),
                null, new Widget[] { attachmentBox, postNmbBox });

        masterValidator.day(messages.illegal_day(), Integer
                .parseInt(currentYear), Integer.parseInt(currentMonth),
                new Widget[] { dayBox });

        return masterValidator.validateStatus();
    }

    public void setDateHeader() {
        dateHeader.setHTML(Util.monthString(messages, currentMonth) + " "
                + currentYear);
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
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
        dayBox = new TextBoxWithErrorText();
        dayBox.setMaxLength(2);
        dayBox.setVisibleLength(2);
        return dayBox;
    }

    public TextBoxWithErrorText createDayBox(HTML errorLabel) {
        dayBox = new TextBoxWithErrorText(errorLabel);
        dayBox.setMaxLength(2);
        dayBox.setVisibleLength(2);
        return dayBox;
    }

    public TextBoxWithErrorText createDescriptionBox() {
        descriptionBox = new TextBoxWithErrorText();
        descriptionBox.setMaxLength(40);
        descriptionBox.setVisibleLength(40);
        return descriptionBox;
    }

    public TextBoxWithErrorText createAmountBox() {
        amountBox = new TextBoxWithErrorText();
        amountBox.setVisibleLength(10);
        return amountBox;
    }

    public HTML getDateHeader() {
        return dateHeader;
    }

    public TextBoxWithErrorText createMonthBox(HTML errorLabelForDate) {
        monthBox = new TextBoxWithErrorText(errorLabelForDate);
        monthBox.setMaxLength(2);
        monthBox.setVisibleLength(2);
        return monthBox;
    }

    public TextBoxWithErrorText createYearBox(HTML errorLabelForDate) {
        yearBox = new TextBoxWithErrorText(errorLabelForDate);
        yearBox.setMaxLength(4);
        yearBox.setVisibleLength(4);
        return yearBox;
    }
}
