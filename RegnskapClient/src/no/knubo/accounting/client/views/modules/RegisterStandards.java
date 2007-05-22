package no.knubo.accounting.client.views.modules;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.ErrorLabelWidget;
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

    private final TextBoxWithErrorText attachmentBox;

    private final TextBoxWithErrorText postNmbBox;

    private final I18NAccount messages;

    private final HTML dateHeader;

    private final ErrorLabelWidget dayBox;

    private final ErrorLabelWidget descriptionBox;

    public RegisterStandards(Constants constants, I18NAccount messages,
            HTML dateHeader, TextBoxWithErrorText attachmentBox,
            TextBoxWithErrorText postNmbBox, ErrorLabelWidget dayBox,
            ErrorLabelWidget descriptionBox) {
        this.constants = constants;
        this.messages = messages;
        this.dateHeader = dateHeader;
        this.attachmentBox = attachmentBox;
        this.postNmbBox = postNmbBox;
        this.dayBox = dayBox;
        this.descriptionBox = descriptionBox;

    }

    public void fetchInitalData() {

        ResponseTextHandler rh = new ResponseTextHandler() {
            public void onCompletion(String responseText) {
                JSONValue jsonValue = JSONParser.parse(responseText);

                JSONObject root = jsonValue.isObject();

                currentYear = Util.str(root.get("year"));
                currentMonth = Util.str(root.get("month"));
                setDateHeader();

                attachmentBox.setText(Util.str(root.get("attachment")));
                postNmbBox.setText(Util.str(root.get("postnmb")));
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

}
