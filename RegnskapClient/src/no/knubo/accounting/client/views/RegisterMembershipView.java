package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.FocusCallback;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ErrorLabelWidget;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.validation.Validateable;
import no.knubo.accounting.client.views.modules.UserSearchCallback;
import no.knubo.accounting.client.views.modules.UserSearchFields;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class RegisterMembershipView extends Composite implements ClickListener, UserSearchCallback,
        FocusCallback {

    private static RegisterMembershipView me;

    public static RegisterMembershipView show(I18NAccount messages, Constants constants,
            HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new RegisterMembershipView(messages, constants, helpPanel, elements);
        }
        return me;
    }

    private final I18NAccount messages;

    private final Constants constants;

    private FlexTable resultTable;

    private HTML header;

    private IdHolder<String, TextBoxWithErrorText> idHolder;

    private UserSearchFields userSearchFields;

    protected String currentYear;

    protected int currentMonth;

    private final HelpPanel helpPanel;

    private final Elements elements;

    private RegisterMembershipView(I18NAccount messages, Constants constants, HelpPanel helpPanel,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        idHolder = new IdHolder<String, TextBoxWithErrorText>();
        userSearchFields = new UserSearchFields(this, elements);

        DockPanel dp = new DockPanel();

        header = new HTML();
        setHeader();

        HTML help = new HTML();
        help.setHTML(elements.register_membership_help());

        dp.add(header, DockPanel.NORTH);
        dp.add(help, DockPanel.NORTH);

        dp.add(userSearchFields.getSearchTable(), DockPanel.NORTH);

        resultTable = new FlexTable();
        resultTable.setStyleName("tableborder");

        resultTable.getRowFormatter().setStyleName(0, "header");
        resultTable.setHTML(0, 0, elements.firstname());
        resultTable.setHTML(0, 1, elements.lastname());
        resultTable.setHTML(0, 2, elements.email());
        resultTable.setHTML(0, 3, elements.year_membership());
        resultTable.setHTML(0, 4, elements.course_membership());
        resultTable.setHTML(0, 5, elements.train_membership());
        resultTable.setHTML(0, 6, elements.paid_day());
        resultTable.setHTML(0, 7, elements.post());
        dp.add(resultTable, DockPanel.NORTH);

        Button button = new NamedButton("RegisterMembershipView.registerMembershipButton", elements
                .register_membership());
        button.addClickListener(this);
        dp.add(button, DockPanel.NORTH);

        initWidget(dp);
    }

    public void init() {
        setVisible(true);
        userSearchFields.setFocus();
        helpPanel.resize(this);
    }

    public void doSearch(StringBuffer searchRequest) {
        doClear();
        idHolder.init();

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONArray array = value.isArray();

                if (array == null) {
                    Window.alert(messages.search_failed());
                    return;
                }

                if (array.size() == 0) {
                    resultTable.setHTML(1, 0, messages.no_result());
                    resultTable.getFlexCellFormatter().setColSpan(1, 0, 7);
                    return;
                }

                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    if (i > 30) {
                        Window.alert(messages.too_many_hits("30"));
                        return;
                    }

                    String firstname = Util.str(obj.get("firstname"));
                    String lastname = Util.str(obj.get("lastname"));

                    int row = i + 1;
                    resultTable.setHTML(row, 0, firstname);
                    resultTable.setHTML(row, 1, lastname);
                    resultTable.setHTML(row, 2, Util.str(obj.get("email")));

                    CheckBox yearCheck = new CheckBox();
                    resultTable.setWidget(row, 3, yearCheck);
                    resultTable.getCellFormatter().setStyleName(row, 3, "center");

                    CheckBox courseCheck = new CheckBox();
                    resultTable.setWidget(row, 4, courseCheck);
                    resultTable.getCellFormatter().setStyleName(row, 4, "center");

                    CheckBox trainCheck = new CheckBox();
                    resultTable.setWidget(row, 5, trainCheck);
                    resultTable.getCellFormatter().setStyleName(row, 5, "center");

                    Util.linkJustOne(courseCheck, trainCheck);

                    TextBoxWithErrorText dayBox = new TextBoxWithErrorText("day");
                    dayBox.setMaxLength(2);
                    dayBox.setVisibleLength(2);
                    dayBox.addFocusListener(me);
                    resultTable.setWidget(row, 6, dayBox);

                    enableDisableBoxes(obj, yearCheck, courseCheck, trainCheck);
                    idHolder.add(Util.str(obj.get("id")), dayBox);

                    ListBox payments = new ListBox();
                    payments.setVisibleItemCount(1);
                    PosttypeCache.getInstance(constants, messages).fillMembershipPayments(payments);

                    resultTable.setWidget(row, 7, payments);

                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    resultTable.getRowFormatter().setStyleName(row, style);

                }
            }

        };

        AuthResponder.post(constants, messages, callback, searchRequest, "registers/persons.php");

    }

    private void enableDisableBoxes(JSONObject obj, CheckBox yearCheck, CheckBox courseCheck,
            CheckBox trainCheck) {
        if ("1".equals(Util.str(obj.get("year")))) {
            yearCheck.setChecked(true);
            yearCheck.setEnabled(false);
        }

        if ("1".equals(Util.str(obj.get("course")))) {
            courseCheck.setChecked(true);
            courseCheck.setEnabled(false);
        }

        if ("1".equals(Util.str(obj.get("train")))) {
            trainCheck.setChecked(true);
            trainCheck.setEnabled(false);
        }

    }

    public void onClick(Widget sender) {
        StringBuffer sb = buildAddMemberParameters();

        if (sb == null) {
            return;
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONObject obj = value.isObject();

                String serverResponse = Util.str(obj.get("result"));

                if ("1".equals(serverResponse)) {
                    disableAfterOK();
                } else {
                    Window.alert(messages.save_failed_badly());
                }
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "accounting/addmembership.php");
    }

    protected void disableAfterOK() {
        for (int i = 1; i < resultTable.getRowCount(); i++) {
            CheckBox yearBox = (CheckBox) resultTable.getWidget(i, 3);
            CheckBox courseBox = (CheckBox) resultTable.getWidget(i, 4);
            CheckBox trainBox = (CheckBox) resultTable.getWidget(i, 5);

            if (yearBox.isChecked()) {
                yearBox.setEnabled(false);
            }
            if (courseBox.isChecked()) {
                courseBox.setEnabled(false);
            }
            if (trainBox.isChecked()) {
                trainBox.setEnabled(false);
            }
        }
    }

    private StringBuffer buildAddMemberParameters() {
        StringBuffer sb = new StringBuffer();

        sb.append("action=save");
        boolean ok = true;

        MasterValidator mv = new MasterValidator();

        for (int i = 1; i < resultTable.getRowCount(); i++) {
            CheckBox yearBox = (CheckBox) resultTable.getWidget(i, 3);
            CheckBox courseBox = (CheckBox) resultTable.getWidget(i, 4);
            CheckBox trainBox = (CheckBox) resultTable.getWidget(i, 5);
            TextBoxWithErrorText dayBox = (TextBoxWithErrorText) resultTable.getWidget(i, 6);
            ListBox post = (ListBox) resultTable.getWidget(i, 7);
            String id = idHolder.findId(dayBox);

            boolean doYear = yearBox.isEnabled() && yearBox.isChecked();
            boolean doCourse = courseBox.isEnabled() && courseBox.isChecked();
            boolean doTrain = trainBox.isEnabled() && trainBox.isChecked();

            if (doYear || doCourse || doTrain) {
                validateDay(mv, dayBox);
                ok = ok && mv.validateStatus();
            }

            /* If daybox is given, then a checkbox must be set. */
            if (dayBox.getText().length() > 0) {
                String message = messages.add_member_day_require_action();
                boolean shouldFail = !doYear && !doCourse && !doTrain;
                ok = ok && mv.fail(dayBox, shouldFail, message);
            }

            if (doYear) {
                Util.addPostParam(sb, "year" + id, "1");
            }
            if (doCourse) {
                Util.addPostParam(sb, "course" + id, "1");
            }
            if (doTrain) {
                Util.addPostParam(sb, "train" + id, "1");
            }
            if (dayBox.getText().length() > 0) {
                Util.addPostParam(sb, "day" + id, dayBox.getText());
                Util.addPostParam(sb, "post" + id, Util.getSelected(post));
            }
        }

        if (!ok) {
            return null;
        }

        return sb;
    }

    private void validateDay(MasterValidator mv, ErrorLabelWidget dayBox) {
        mv.day("!", messages.illegal_day(), Integer.parseInt(currentYear), currentMonth,
                new Widget[] { dayBox });
    }

    private void setHeader() {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {

                JSONObject object = value.isObject();

                String semester = Util.str(object.get("semester"));
                currentYear = Util.str(object.get("year"));
                currentMonth = Util.getInt(object.get("month"));

                String headerText = "<h2>" + elements.register_membership_header() + " - "
                        + semester + "-" + Util.monthString(elements, currentMonth) + "</h2>";
                header.setHTML(headerText);
            }
        };
        AuthResponder.get(constants, messages, callback, this.constants.baseurl() + "defaults/semester.php");

    }

    public void onFocus(Validateable me) {
        /* Not used */
    }

    public void onLostFocus(ErrorLabelWidget textbox) {
        /* Just flag the error. */

        validateDay(new MasterValidator(), textbox);
    }

    public void doClear() {
        while (resultTable.getRowCount() > 1) {
            resultTable.removeRow(1);
        }
    }

}
