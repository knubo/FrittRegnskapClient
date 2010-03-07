package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class StandardvaluesView extends Composite implements ClickHandler {

    private static StandardvaluesView me;

    public static StandardvaluesView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new StandardvaluesView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    private Button updateButton;

    private TextBoxWithErrorText yearBox;

    private TextBoxWithErrorText monthBox;

    private ListBoxWithErrorText semesterBox;

    private TextBoxWithErrorText costCourseBox;

    private TextBoxWithErrorText costPracticeBox;

    private TextBoxWithErrorText costMembershipBox;

    private final Constants constants;

    private HTML statusHTML;

    private final I18NAccount messages;

    private TextBoxWithErrorText emailBox;
    private TextBoxWithErrorText massletterDueDateBox;

    private final Elements elements;

    private ListBoxWithErrorText postYear;

    private ListBoxWithErrorText postCourse;

    public StandardvaluesView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        DockPanel dp = new DockPanel();

        HTML html = new HTML(elements.standardsettings());
        dp.add(html, DockPanel.NORTH);

        DecoratedTabPanel tabPanel = new DecoratedTabPanel();
        tabPanel.setAnimationEnabled(false);

        dp.add(tabPanel, DockPanel.NORTH);

        tabPanel.add(setupGeneralTab(), elements.setup_general());
        tabPanel.add(setupBudgetTab(), elements.setup_budget());

        tabPanel.selectTab(0);
        
        updateButton = new NamedButton("StandardValuesView.updateButton", elements.update());
        updateButton.addClickHandler(this);
        statusHTML = new HTML();

        FlowPanel fp = new FlowPanel();
        fp.add(updateButton);
        fp.add(statusHTML);

        dp.add(fp, DockPanel.NORTH);
        initWidget(dp);
    }

    private FlexTable setupBudgetTab() {
        FlexTable table = new FlexTable();
        table.setStyleName("edittable");

        table.setHTML(0,0,elements.setup_budget_post_year());
        table.setHTML(1,0,elements.setup_budget_post_course());
        
        postYear = new ListBoxWithErrorText("budget_post_year");
        table.setWidget(0, 1, postYear);
        postCourse = new ListBoxWithErrorText("budget_post_course");
        table.setWidget(1, 1, postCourse);
        
        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);
        
        posttypeCache.fillAllPosts(postYear);
        posttypeCache.fillAllPosts(postCourse);
        
        return table;
    }

    private FlexTable setupGeneralTab() {
        FlexTable table = new FlexTable();
        table.setStyleName("edittable");

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
        semesterBox = new ListBoxWithErrorText("semester");
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
        
        return table;
    }

    public void onClick(ClickEvent event) {
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
        Util.addPostParam(sb, "year_post", Util.getSelected(postYear.getListbox()));
        Util.addPostParam(sb, "course_post", Util.getSelected(postCourse.getListbox()));
        
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
        semesterBox.clear();

        /* Fills first semesters, then standard values */
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONArray arr = value.isArray();

                for (int i = 0; i < arr.size(); i++) {
                    JSONValue semVal = arr.get(i);
                    JSONObject obj = semVal.isObject();

                    semesterBox.addItem(obj.get("description"), obj.get("semester"));
                }

                fillStandardValues();
            }
        };

        AuthResponder.get(constants, messages, callback, "registers/semesters.php?action=all");
    }

    private void fillStandardValues() {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                yearBox.setText(Util.str(object.get("year")));
                monthBox.setText(Util.str(object.get("month")));
                semesterBox.setIndexByValue(object.get("semester"));
                costCourseBox.setText(Util.str(object.get("cost_course")));
                costPracticeBox.setText(Util.str(object.get("cost_practice")));
                costMembershipBox.setText(Util.str(object.get("cost_membership")));
                emailBox.setText(Util.str(object.get("email_sender")));
                massletterDueDateBox.setText(Util.str(object.get("massletter_due_date")));
                Util.setIndexByValue(postYear.getListbox(), Util.str(object.get("year_post")));
                Util.setIndexByValue(postCourse.getListbox(), Util.str(object.get("course_post")));
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/standard.php?action=get");
    }

    private boolean validate() {
        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] { yearBox, monthBox, semesterBox });

        masterValidator.range(messages.illegal_month(), new Integer(1), new Integer(12), new Widget[] { monthBox });

        masterValidator.date(messages.date_format(), new Widget[] { massletterDueDateBox });

        return masterValidator.validateStatus();

    }
}
