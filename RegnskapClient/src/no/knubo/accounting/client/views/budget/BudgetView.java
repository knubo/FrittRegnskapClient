package no.knubo.accounting.client.views.budget;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedCheckBox;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class BudgetView extends Composite implements ClickHandler {

    static BudgetView me;
    I18NAccount messages;
    Constants constants;
    HelpPanel helpPanel;
    Elements elements;
    PosttypeCache posttypeCache;
    ListBox accountsEarnings;
    FlexTable budgetTable;
    TextBoxWithErrorText accountInputEarnings;
    TextBoxWithErrorText accountValueEarnings;
    RegisterStandards registerStandards;
    NamedButton addEarningRowButton;
    NamedButton addCostRowButton;
    ListBox accountsCost;
    TextBoxWithErrorText accountInputCost;
    TextBoxWithErrorText accountValueCost;
    Label costSumLabel;
    Label earningsSumLabel;
    CheckBox sumEarningCheckBox;
    CheckBox sumCostCheckBox;
    BudgetDrawDelegate budgetDrawDelegate;
    private NamedButton saveButton;
    private Label saveResultLabel;
    private NamedCheckBox hideNotInCurrentYearCheckbox;
    private NamedButton selectBudgetYearButton;
    private NamedCheckBox showSumsCheckbox;
    private NamedButton showMembershipCaclculctorCheckbox;
    private NamedButton showChartButton;
    private String selectedBudgetYear;
    private NamedButton deleteButton;

    public BudgetView(I18NAccount messages, Constants constants, HelpPanel helpPanel, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setStyleName("buttonpanel");

        selectBudgetYearButton = new NamedButton("select_budget_year", elements.select_budget_year());
        selectBudgetYearButton.addClickHandler(this);
        buttonPanel.add(selectBudgetYearButton);

        buttonPanel.add(new Label(elements.hide_from_other_year()));
        hideNotInCurrentYearCheckbox = new NamedCheckBox("hide_not_in_current_year");
        hideNotInCurrentYearCheckbox.addClickHandler(this);
        buttonPanel.add(hideNotInCurrentYearCheckbox);

        buttonPanel.add(new Label(elements.show_sums()));
        showSumsCheckbox = new NamedCheckBox("show_sums");
        showSumsCheckbox.addClickHandler(this);
        buttonPanel.add(showSumsCheckbox);

        showMembershipCaclculctorCheckbox = new NamedButton("membership_calculator", elements
                .show_membership_calculator());
        showMembershipCaclculctorCheckbox.addClickHandler(this);
        buttonPanel.add(showMembershipCaclculctorCheckbox);

        showChartButton = new NamedButton("show_chart", elements.show_graphs());
        showChartButton.addClickHandler(this);
        buttonPanel.add(showChartButton);

        dp.add(buttonPanel, DockPanel.NORTH);

        budgetTable = new FlexTable();
        budgetTable.setStyleName("tableborder");

        budgetTable.getFlexCellFormatter().setColSpan(0, 0, 3);
        budgetTable.setText(0, 0, elements.budgeted_earnins());

        budgetTable.setText(1, 1, elements.account());
        budgetTable.setText(1, 3, elements.value());
        budgetTable.getRowFormatter().setStyleName(0, "header");
        budgetTable.getRowFormatter().setStyleName(1, "header");
        budgetTable.addClickHandler(this);

        dp.add(budgetTable, DockPanel.NORTH);

        saveButton = new NamedButton("save", elements.save());
        saveButton.addClickHandler(this);
        saveResultLabel = new Label();

        deleteButton = new NamedButton("delete_line", elements.delete_line());
        deleteButton.addClickHandler(this);

        HorizontalPanel hp = new HorizontalPanel();

        hp.add(deleteButton);
        hp.add(saveButton);
        hp.add(saveResultLabel);
        dp.add(hp, DockPanel.NORTH);

        registerStandards = new RegisterStandards(constants, messages, elements);

        accountsEarnings = new ListBox();
        accountInputEarnings = new TextBoxWithErrorText("accountinputearnings");
        accountValueEarnings = registerStandards.createAmountBox();
        Util.syncListbox(accountsEarnings, accountInputEarnings.getTextBox());

        accountsCost = new ListBox();
        accountInputCost = new TextBoxWithErrorText("accountinputcost");
        accountValueCost = registerStandards.createAmountBox();
        Util.syncListbox(accountsCost, accountInputCost.getTextBox());

        posttypeCache = PosttypeCache.getInstance(constants, messages);
        posttypeCache.fillAllPosts(accountsEarnings);
        posttypeCache.fillAllPosts(accountsCost);

        addEarningRowButton = new NamedButton("earningbutton", elements.add());
        addEarningRowButton.addClickHandler(this);

        addCostRowButton = new NamedButton("costbutton", elements.add());
        addCostRowButton.addClickHandler(this);

        costSumLabel = new Label();
        costSumLabel.setStyleName("right");
        earningsSumLabel = new Label();
        earningsSumLabel.setStyleName("right");

        sumCostCheckBox = new CheckBox();
        sumEarningCheckBox = new CheckBox();

        selectedBudgetYear = "" + Util.currentYear();

        initWidget(dp);
    }

    public static BudgetView show(I18NAccount messages, Constants constants, HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new BudgetView(messages, constants, helpPanel, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void init(String... year) {

        while (budgetTable.getRowCount() > 2) {
            budgetTable.removeRow(2);
        }

        this.accountInputCost.setText("");
        this.accountInputEarnings.setText("");
        this.accountsCost.setSelectedIndex(0);
        this.accountsEarnings.setSelectedIndex(0);
        this.accountValueCost.setText("");
        this.accountValueEarnings.setText("");

        budgetDrawDelegate = new BudgetDrawDelegate(this, elements, messages, constants);
        budgetDrawDelegate.init();

        loadBudgetData(year);

        helpPanel.resize(this);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addEarningRowButton) {
            budgetDrawDelegate.addEarningClick();
        } else if (event.getSource() == budgetTable) {
            budgetDrawDelegate.budgetTableClick(event);
        } else if (event.getSource() == addCostRowButton) {
            budgetDrawDelegate.addCostClick();
        } else if (event.getSource() == saveButton) {
            saveBudget();
        } else if (event.getSource() == hideNotInCurrentYearCheckbox) {
            toggleHideUnusedAccounts();
        } else if (event.getSource() == showSumsCheckbox) {
            toggleShowSums();
        } else if (event.getSource() == selectBudgetYearButton) {
            BudgetSelectView.getInstance(elements, messages).selectBudgetYear(this);
        } else if (event.getSource() == showMembershipCaclculctorCheckbox) {
            BudgetMembershipCalculatorView instance = BudgetMembershipCalculatorView.getInstance(constants, elements,
                    messages);
            instance.setPopupPosition(100, 100);
            instance.show();
        } else if (event.getSource() == deleteButton) {
            deleteSelectedRows();
        }
    }

    private void deleteSelectedRows() {
        budgetDrawDelegate.deleteSelected();
    }

    private void toggleShowSums() {
        BudgetSumView budgetSumView = BudgetSumView.getInstance(elements, messages);

        if (showSumsCheckbox.getValue()) {
            Util.log("Showing popup");
            budgetSumView.setPopupPosition(200, 200);
            budgetSumView.show();
            calculateBudgetSumToSumView();
        } else {
            budgetSumView.hide();
        }
    }

    private void toggleHideUnusedAccounts() {
        if (hideNotInCurrentYearCheckbox.getValue()) {
            budgetDrawDelegate.removeAllRowsWithNoBudgetData();
        } else {
            init();
        }
    }

    private void saveBudget() {
        JSONArray data = budgetDrawDelegate.getBudgetData();

        ServerResponse rh = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                if (Util.str(responseObj).equals("1")) {
                    saveResultLabel.setText(messages.save_ok());
                } else {
                    saveResultLabel.setText(messages.save_failed());
                }
            }
        };
        StringBuffer params = new StringBuffer();
        params.append("action=save");
        Util.addPostParam(params, "budget", data.toString());
        Util.addPostParam(params, "year", budgetTable.getText(0, 1));

        AuthResponder.post(constants, messages, rh, params, constants.baseurl() + "accounting/budget.php");

        calculateBudgetSumToSumView();
    }

    private void calculateBudgetSumToSumView() {
        double cost = budgetDrawDelegate.calcBudgetCost();
        double earnings = budgetDrawDelegate.calcBudgetEarnings();

        BudgetSumView.getInstance(elements, messages).setSumData(budgetTable.getText(0, 1), earnings, cost,
                earnings - cost);
    }

    String postForYearMemberships;
    String postForCourseMemberships;

    private void loadBudgetData(String... year) {
        ServerResponse rh = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();
                JSONObject result = object.get("result").isObject();

                budgetDrawDelegate.fillYearData(result);
                BudgetSumView.getInstance(elements, messages).calculateSumPreviousYears(result);
                BudgetSelectView.getInstance(elements, messages).setBudgetYears(object.get("budgetYears").isArray());

                fillBudget(object.get("budget").isArray());

                BudgetMembershipCalculatorView calculator = BudgetMembershipCalculatorView.getInstance(constants,
                        elements, messages);

                JSONObject members = object.get("members").isObject();
                JSONObject price = object.get("price").isObject();
                JSONArray semesters = object.get("semesters").isArray();
                String budgetYear = budgetTable.getText(0, 1);
                postForYearMemberships = Util.str(object.get("year_post"));
                postForCourseMemberships = Util.str(object.get("course_post"));
                JSONObject membersbudget = object.get("membersbudget").isObject();
                calculator.init(me, members, price, semesters, budgetYear, membersbudget);

            }
        };
        String params = "action=init";
        if (year.length == 1) {
            params = params + "&year=" + year[0];
        }
        AuthResponder.get(constants, messages, rh, constants.baseurl() + "accounting/budget.php?" + params);
    }

    protected void fillBudget(JSONArray array) {
        if (array.size() == 0) {
            budgetTable.setText(0, 1, selectedBudgetYear);
            return;
        }

        int year = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();

            String account = Util.str(object.get("post_type"));
            JSONValue value = object.get("amount");
            JSONValue earning = object.get("earning");
            if (year == 0) {
                year = Util.getInt(object.get("year"));
            }

            budgetDrawDelegate.addBudget(account, Util.getBoolean(earning), Util.getDouble(value));
        }
        budgetTable.setText(0, 1, "" + year);

    }

    public void setBudgetYear(String selectedYear) {
        selectedBudgetYear = selectedYear;
        init(selectedYear);
    }

    public void sumsFromCalculator(double sumYear, double sumCourse) {
        Util.log("Calculator (Y):" + postForYearMemberships + " " + sumYear);
        Util.log("Calculator (C):" + postForCourseMemberships + " " + sumCourse);
        budgetDrawDelegate.addBudget(postForYearMemberships, true, sumYear);
        budgetDrawDelegate.addBudget(postForCourseMemberships, true, sumCourse);

    }

}
