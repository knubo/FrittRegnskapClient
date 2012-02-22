package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedCheckBox;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.modules.AccountSelected;
import no.knubo.accounting.client.views.modules.AccountSelector;

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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class StandardvaluesView extends Composite implements ClickHandler, AccountSelected {

    private static StandardvaluesView me;

    public static StandardvaluesView getInstance(I18NAccount messages, Constants constants, Elements elements) {
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
    private ListBoxWithErrorText postTrain;
    private ListBoxWithErrorText postYouth;

    private ListBoxWithErrorText endMonthPost;

    private ListBoxWithErrorText endYearPost;

    private Image editFordringerImage;

    private FlexTable accountsTable;

    private Image editMonthTransferImage;

    private Image editRegisterMembershipPostsImage;

    private NamedCheckBox birthdateRequiredBox;

    private ListBoxWithErrorText bankKidPost;

    private TextBoxWithErrorText lastMonthInSemesterBox;

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
        tabPanel.add(setupAccountsTab(), elements.setup_accounts());

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

    private FlexTable setupAccountsTab() {
        accountsTable = new FlexTable();
        accountsTable.setStyleName("edittable");

        accountsTable.setHTML(0, 0, elements.setup_budget_post_year());
        accountsTable.setHTML(1, 0, elements.setup_budget_post_course());
        accountsTable.setHTML(2, 0, elements.setup_budget_post_train());
        accountsTable.setHTML(3, 0, elements.setup_budget_post_youth());
        accountsTable.setHTML(4, 0, elements.setup_end_month_post());
        accountsTable.setHTML(5, 0, elements.setup_end_year_post());
        accountsTable.setHTML(6, 0, elements.setup_expected_income_or_cost_post());
        accountsTable.setHTML(7, 0, elements.setup_end_month_transfer_posts());
        accountsTable.setHTML(8, 0, elements.setup_register_membership_posts());
        accountsTable.setHTML(9, 0, elements.setup_kid_bank_post());

        postYear = new ListBoxWithErrorText("budget_post_year");
        accountsTable.setWidget(0, 1, postYear);
        postCourse = new ListBoxWithErrorText("budget_post_course");
        accountsTable.setWidget(1, 1, postCourse);
        postTrain = new ListBoxWithErrorText("budget_post_train");
        accountsTable.setWidget(2, 1, postTrain);

        postYouth = new ListBoxWithErrorText("budget_post_youth");
        accountsTable.setWidget(3, 1, postYouth);

        endMonthPost = new ListBoxWithErrorText("end_month_post");
        accountsTable.setWidget(4, 1, endMonthPost);

        endYearPost = new ListBoxWithErrorText("end_year_post");
        accountsTable.setWidget(5, 1, endYearPost);

        editFordringerImage = ImageFactory.editImage("edit_fordringer");
        editFordringerImage.addClickHandler(this);
        accountsTable.setWidget(6, 2, editFordringerImage);

        editMonthTransferImage = ImageFactory.editImage("edit_month_transfer");
        editMonthTransferImage.addClickHandler(this);
        accountsTable.setWidget(7, 2, editMonthTransferImage);

        editRegisterMembershipPostsImage = ImageFactory.editImage("register_membership_posts");
        editRegisterMembershipPostsImage.addClickHandler(this);
        accountsTable.setWidget(8, 2, editRegisterMembershipPostsImage);

        bankKidPost = new ListBoxWithErrorText("setup_kid_bank_post");

        accountsTable.setWidget(9, 1, bankKidPost);

        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);

        posttypeCache.fillAllPosts(postYear.getListbox(), null, true, true);
        posttypeCache.fillAllPosts(postCourse.getListbox(), null, true, true);
        posttypeCache.fillAllPosts(postYouth.getListbox(), null, true, true);
        posttypeCache.fillAllPosts(postTrain.getListbox(), null, true, true);
        posttypeCache.fillAllPosts(endMonthPost.getListbox(), null, true, true);
        posttypeCache.fillAllPosts(endYearPost.getListbox(), null, true, true);
        posttypeCache.fillBank(bankKidPost.getListbox());

        return accountsTable;
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
        table.setHTML(8, 0, elements.birthdate_required());
        table.setHTML(9, 0, elements.last_month_in_semester());

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

        birthdateRequiredBox = new NamedCheckBox("birthdate_required");

        lastMonthInSemesterBox = new TextBoxWithErrorText("last_month_in_semester");
        lastMonthInSemesterBox.setMaxLength(2);

        table.setWidget(0, 1, yearBox);
        table.setWidget(1, 1, monthBox);
        table.setWidget(2, 1, semesterBox);
        table.setWidget(3, 1, costCourseBox);
        table.setWidget(4, 1, costPracticeBox);
        table.setWidget(5, 1, costMembershipBox);
        table.setWidget(6, 1, emailBox);
        table.setWidget(7, 1, massletterDueDateBox);
        table.setWidget(8, 1, birthdateRequiredBox);
        table.setWidget(9, 1, lastMonthInSemesterBox);

        return table;
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == updateButton) {
            save();
        }
        if (event.getSource() == editFordringerImage) {
            AccountSelector instance = AccountSelector.getInstance(elements, constants, messages);
            instance.init(elements.setup_expected_income_or_cost_post(), getFordringerPost(), this);
            instance.center();
        }
        if (event.getSource() == editMonthTransferImage) {
            AccountSelector instance = AccountSelector.getInstance(elements, constants, messages);
            instance.init(elements.setup_end_month_transfer_posts(), getEndMonthTransferPosts(), this);
            instance.center();
        }
        if (event.getSource() == editRegisterMembershipPostsImage) {
            AccountSelector instance = AccountSelector.getInstance(elements, constants, messages);
            instance.init(elements.setup_register_membership_posts(), getRegisterMembershipPosts(), this);
            instance.center();
        }

    }

    private String getRegisterMembershipPosts() {
        return accountsTable.getText(8, 1);
    }

    private String getEndMonthTransferPosts() {
        return accountsTable.getText(7, 1);
    }

    private String getFordringerPost() {
        return accountsTable.getText(6, 1);
    }

    private void setRegisterMembershipPosts(String accounts) {
        accountsTable.setText(8, 1, accounts);
    }

    private void setEndMonthTransferPosts(String accounts) {
        accountsTable.setText(7, 1, accounts);
    }

    private void setFordringerPosts(String accounts) {
        accountsTable.setText(6, 1, accounts);
    }

    private void save() {
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
        Util.addPostParam(sb, "train_post", Util.getSelected(postTrain.getListbox()));
        Util.addPostParam(sb, "youth_post", Util.getSelected(postYouth.getListbox()));
        Util.addPostParam(sb, "end_month_post", Util.getSelected(endMonthPost.getListbox()));
        Util.addPostParam(sb, "end_year_post", Util.getSelected(endYearPost.getListbox()));
        Util.addPostParam(sb, "fordringer_posts", getFordringerPost());
        Util.addPostParam(sb, "end_month_transfer_posts", getEndMonthTransferPosts());
        Util.addPostParam(sb, "register_membership_posts", getRegisterMembershipPosts());
        Util.addPostParam(sb, "birthdate_required", birthdateRequiredBox.getValue() ? "1" : "0");
        Util.addPostParam(sb, "bank_kid_post", Util.getSelected(bankKidPost.getListbox()));
        Util.addPostParam(sb, "last_spring_month", lastMonthInSemesterBox.getText());
        ServerResponse callback = new ServerResponse() {

            @Override
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

            @Override
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

            @Override
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                yearBox.setText(Util.str(object.get("year")));
                monthBox.setText(Util.str(object.get("month")));
                semesterBox.setIndexByValue(object.get("semester"));
                costCourseBox.setText(Util.str(object.get("cost_course")));
                costPracticeBox.setText(Util.str(object.get("cost_practice")));
                costMembershipBox.setText(Util.str(object.get("cost_membership")));
                emailBox.setText(Util.strSkipNull(object.get("email_sender")));
                massletterDueDateBox.setText(Util.strSkipNull(object.get("massletter_due_date")));
                Util.setIndexByValue(postYear.getListbox(), Util.str(object.get("year_post")));
                Util.setIndexByValue(postCourse.getListbox(), Util.str(object.get("course_post")));
                Util.setIndexByValue(postTrain.getListbox(), Util.str(object.get("train_post")));
                Util.setIndexByValue(postYouth.getListbox(), Util.str(object.get("youth_post")));
                Util.setIndexByValue(endMonthPost.getListbox(), Util.str(object.get("end_month_post")));
                Util.setIndexByValue(endYearPost.getListbox(), Util.str(object.get("end_year_post")));
                Util.setIndexByValue(bankKidPost.getListbox(), Util.str(object.get("bank_kid_post")));

                setFordringerPosts(Util.strSkipNull(object.get("fordringer_posts")));
                setEndMonthTransferPosts(Util.strSkipNull(object.get("end_month_transfer_posts")));
                setRegisterMembershipPosts(Util.strSkipNull(object.get("register_membership_posts")));
                birthdateRequiredBox.setValue(Util.getBoolean(object.get("birthdate_required")));
                lastMonthInSemesterBox.setText(Util.strSkipNull(object.get("last_spring_month")));

            }

        };

        AuthResponder.get(constants, messages, callback, "registers/standard.php?action=get");
    }

    private boolean validate() {
        MasterValidator masterValidator = new MasterValidator();

        masterValidator.mandatory(messages.required_field(), new Widget[] { yearBox, monthBox, semesterBox,
                endMonthPost, endYearPost, postCourse, postTrain, postYear, postYouth, lastMonthInSemesterBox });

        masterValidator.range(messages.illegal_month(), new Integer(1), new Integer(12), new Widget[] { monthBox });

        masterValidator.range(messages.illegal_month(), new Integer(1), new Integer(11),
                new Widget[] { lastMonthInSemesterBox });

        masterValidator.date(messages.date_format(), new Widget[] { massletterDueDateBox });

        return masterValidator.validateStatus();

    }

    @Override
    public void selectedAccounts(String accounts, String title) {
        if (title.equals(elements.setup_expected_income_or_cost_post())) {
            setFordringerPosts(accounts);
        }
        if (title.equals(elements.setup_end_month_transfer_posts())) {
            setEndMonthTransferPosts(accounts);
        }
        if (title.equals(elements.setup_register_membership_posts())) {
            setRegisterMembershipPosts(accounts);
        }
    }

}
