package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
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

    public static StandardvaluesView show(I18NAccount messages, Constants constants,
            Elements elements) {
        if (me == null) {
            me = new StandardvaluesView(messages, constants, elements);
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

    private TextBoxWithErrorText emailBox;
    private TextBoxWithErrorText massletterDueDateBox;

    public StandardvaluesView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        DockPanel dp = new DockPanel();

        HTML html = new HTML(elements.standardsettings());
        dp.add(html, DockPanel.NORTH);

        FlexTable table = new FlexTable();
        table.setStyleName("edittable");
        dp.add(table, DockPanel.NORTH);

        table.setHTML(0, 0, elements.year());
        table.setHTML(1, 0, elements.month());
        table.setHTML(2, 0, elements.semester());
        table.setHTML(3, 0, elements.cost_course());
        table.setHTML(4, 0, elements.cost_practice());
        table.setHTML(5, 0, elements.cost_membership());
        table.setHTML(6, 0, elements.mail_sender());
        table.setHTML(7, 0, elements.massletter_due_date());

        yearBox = new TextBoxWithErrorText("year");
        yearBox.setMaxLength(4);
        monthBox = new TextBoxWithErrorText("month");
        monthBox.setMaxLength(2);
        semesterBox = new TextBoxWithErrorText("semester");
        semesterBox.setMaxLength(4);
        costCourseBox = new TextBoxWithErrorText("costcourse");
        costCourseBox.setMaxLength(6);
        costCourseBox.setEnabled(false);
        costPracticeBox = new TextBoxWithErrorText("costpractice");
        costPracticeBox.setMaxLength(6);
        costPracticeBox.setEnabled(false);
        costMembershipBox = new TextBoxWithErrorText("costmembership");
        costMembershipBox.setMaxLength(6);
        costMembershipBox.setEnabled(false);
        emailBox = new TextBoxWithErrorText("mail_sender");
        emailBox.setVisibleLength(80);
        massletterDueDateBox = new TextBoxWithErrorText("massletter_due_date");
        massletterDueDateBox.setMaxLength(10);

        table.setWidget(0, 1, yearBox);
        table.setWidget(1, 1, monthBox);
        table.setWidget(2, 1, semesterBox);
        table.setWidget(3, 1, costCourseBox);
        table.setWidget(4, 1, costPracticeBox);
        table.setWidget(5, 1, costMembershipBox);
        table.setWidget(6, 1, emailBox);
        table.setWidget(7, 1, massletterDueDateBox);

        updateButton = new NamedButton("StandardValuesView.updateButton", elements.update());
        updateButton.addClickListener(this);
        statusHTML = new HTML();

        table.setWidget(8, 0, updateButton);
        table.setWidget(8, 1, statusHTML);
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
        Util.addPostParam(sb, "email_sender", emailBox.getText());
        Util.addPostParam(sb, "massletter_due_date", massletterDueDateBox.getText());

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue parse) {
                JSONObject object = parse.isObject();
                if ("1".equals(Util.str(object.get("result")))) {
                    statusHTML.setHTML(messages.save_ok());
                    init();
                } else {
                    statusHTML.setHTML(messages.save_failed());
                }

                Util.timedMessage(statusHTML, "", 10);
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "registers/standard.php");

    }

    public void init() {

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
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
                costMembershipBox.setText(Util.str(object.get("cost_membership")));
                emailBox.setText(Util.str(object.get("email_sender")));
                massletterDueDateBox.setText(Util.str(object.get("massletter_due_date")));
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/standard.php?action=get");

    }

    private boolean validate() {
        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] { yearBox, monthBox,
                semesterBox });

        masterValidator.range(messages.illegal_month(), new Integer(1), new Integer(12),
                new Widget[] { monthBox });

        masterValidator.date(messages.date_format(), new Widget[] { massletterDueDateBox });

        return masterValidator.validateStatus();

    }
}
