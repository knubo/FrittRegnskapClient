package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class StandardvaluesView extends Composite implements ClickListener {

    private static StandardvaluesView me;

    public static StandardvaluesView show(I18NAccount messages,
            Constants constants) {
        if (me == null) {
            me = new StandardvaluesView(messages, constants);
        }
        me.setVisible(true);
        return me;
    }

    private Button updateButton;

    private TextBoxWithErrorText yearBox;

    private TextBoxWithErrorText monthBox;

    private TextBoxWithErrorText semesterBox;

    private TextBoxWithErrorText costCourseBox;

    private TextBoxWithErrorText costPracticeBox;

    private TextBoxWithErrorText costMembershipBox;

    private final Constants constants;

    private HTML statusHTML;

    private final I18NAccount messages;

    public StandardvaluesView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;
        DockPanel dp = new DockPanel();

        HTML html = new HTML(messages.standardsettings());
        dp.add(html, DockPanel.NORTH);

        FlexTable table = new FlexTable();
        table.setStyleName("edittable");
        dp.add(table, DockPanel.NORTH);

        table.setHTML(0, 0, messages.year());
        table.setHTML(1, 0, messages.month());
        table.setHTML(2, 0, messages.semester());
        table.setHTML(3, 0, messages.cost_course());
        table.setHTML(4, 0, messages.cost_practice());
        table.setHTML(5, 0, messages.cost_membership());

        yearBox = new TextBoxWithErrorText();
        yearBox.setMaxLength(4);
        monthBox = new TextBoxWithErrorText();
        monthBox.setMaxLength(2);
        semesterBox = new TextBoxWithErrorText();
        semesterBox.setMaxLength(4);
        costCourseBox = new TextBoxWithErrorText();
        costCourseBox.setMaxLength(6);
        costPracticeBox = new TextBoxWithErrorText();
        costPracticeBox.setMaxLength(6);
        costMembershipBox = new TextBoxWithErrorText();
        costMembershipBox.setMaxLength(6);

        table.setWidget(0, 1, yearBox);
        table.setWidget(1, 1, monthBox);
        table.setWidget(2, 1, semesterBox);
        table.setWidget(3, 1, costCourseBox);
        table.setWidget(4, 1, costPracticeBox);
        table.setWidget(5, 1, costMembershipBox);

        updateButton = new Button(messages.update());
        updateButton.addClickListener(this);
        statusHTML = new HTML();

        table.setWidget(6, 0, updateButton);
        table.setWidget(6, 1, statusHTML);
        initWidget(dp);
    }

    public void onClick(Widget sender) {
        if (!validate()) {
            return;
        }

        StringBuffer sb = new StringBuffer();

        sb.append("action=save");
        Util.addPostParam(sb, "year", yearBox.getText());
        Util.addPostParam(sb, "month", monthBox.getText());
        Util.addPostParam(sb, "semester", semesterBox.getText());
        Util.addPostParam(sb, "cost_course", costCourseBox.getText());
        Util.addPostParam(sb, "cost_practice", costPracticeBox.getText());
        Util.addPostParam(sb, "cost_membership", costMembershipBox.getText());

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "registers/standard.php");

        RequestCallback callback = new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                Window.alert(exception.getMessage());
            }

            public void onResponseReceived(Request request, Response response) {
                if ("1".equals(response.getText())) {
                    statusHTML.setHTML(messages.save_ok());
                } else {
                    statusHTML.setHTML(messages.save_failed());
                }

                Util.timedMessage(statusHTML, "", 10);
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

    public void init() {
        ResponseTextHandler getValues = new ResponseTextHandler() {

            public void onCompletion(String responseText) {
                JSONValue value = JSONParser.parse(responseText);

                if (value == null) {
                    Window.alert("Failed to load data.");
                    return;
                }
                JSONObject object = value.isObject();

                if (object == null) {
                    Window.alert("Failed to load data.");
                    return;
                }

                yearBox.setText(Util.str(object.get("year")));
                monthBox.setText(Util.str(object.get("month")));
                semesterBox.setText(Util.str(object.get("semester")));
                costCourseBox.setText(Util.str(object.get("cost_course")));
                costPracticeBox.setText(Util.str(object.get("cost_practice")));
                costMembershipBox.setText(Util.str(object
                        .get("cost_membership")));
            }

        };
        // TODO Report stuff as being loaded.
        if (!HTTPRequest.asyncGet(this.constants.baseurl()
                + "registers/standard.php?action=get", getValues)) {
            Window.alert("Failed to load data.");
        }
    }

    private boolean validate() {
        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] {
                yearBox, monthBox, semesterBox, costCourseBox, costPracticeBox,
                costMembershipBox });

        masterValidator.range(messages.illegal_month(), new Integer(1),
                new Integer(12), new Widget[] { monthBox });

        return masterValidator.validateStatus();

    }
}
