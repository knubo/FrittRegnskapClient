package no.knubo.accounting.client.views.budget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class BudgetView extends Composite implements ClickHandler {

    private static BudgetView me;
    private I18NAccount messages;
    private Constants constants;
    private HelpPanel helpPanel;
    private Elements elements;
    private PosttypeCache posttypeCache;
    private ListBox accountsEarnings;
    private FlexTable budgetTable;
    private TextBoxWithErrorText accountInputEarnings;
    private TextBoxWithErrorText accountValueEarnings;
    private RegisterStandards registerStandards;
    private NamedButton addEarningRowButton;
    private int lastEarningsRow;
    private int lastCostRow;
    private NamedButton addCostRowButton;
    private ListBox accountsCost;
    private TextBoxWithErrorText accountInputCost;
    private TextBoxWithErrorText accountValueCost;
    private int currentEarningsEditRow;
    private int currentCostEditRow;
    private Label costSumLabel;
    private Label earningsSumLabel;
    private CheckBox sumEarningCheckBox;
    private CheckBox sumCostCheckBox;
    private HashMap<String, Integer> rowsForAccounts;

    public BudgetView(I18NAccount messages, Constants constants, HelpPanel helpPanel, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        HorizontalPanel buttonPanel = new HorizontalPanel();

        buttonPanel.add(new Label(elements.hide_from_other_year()));
        buttonPanel.add(new NamedCheckBox("hide_not_in_current_year"));

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

        initWidget(dp);
    }

    public static BudgetView show(I18NAccount messages, Constants constants, HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new BudgetView(messages, constants, helpPanel, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void init() {
        while (budgetTable.getRowCount() > 2) {
            budgetTable.removeRow(2);
        }

        lastEarningsRow = 1;
        addInputEarningsRow();

        budgetTable.setWidget(lastEarningsRow + 1, 1, addEarningRowButton);
        sumEarningCheckBox = new CheckBox();
        budgetTable.setWidget(lastEarningsRow + 1, 2, sumEarningCheckBox);
        budgetTable.setWidget(lastEarningsRow + 1, 3, earningsSumLabel);

        addInitialCostRow();

        budgetTable.setWidget(lastCostRow + 1, 1, addCostRowButton);
        sumCostCheckBox = new CheckBox();
        budgetTable.setWidget(lastCostRow + 1, 2, sumCostCheckBox);
        budgetTable.setWidget(lastCostRow + 1, 3, costSumLabel);

        loadBudgetData();

        helpPanel.resize(this);
    }

    private void addInitialCostRow() {
        lastCostRow = lastEarningsRow + 3;
        budgetTable.setText(lastCostRow, 0, elements.budgeted_expences());
        budgetTable.getFlexCellFormatter().setColSpan(lastCostRow, 0, 3);
        budgetTable.getRowFormatter().setStyleName(lastCostRow, "header");

        addInputCostRow();
    }

    private void addInputCostRow() {

        removeCostInputsAndSetValueInItsPlace(false);
        // budgetTable.remove(addCostRowButton);

        lastCostRow++;
        budgetTable.insertRow(lastCostRow);
        budgetTable.getCellFormatter().setStyleName(lastCostRow, 3, "right");

        String rowStyle = (((lastCostRow + 1) % 6) < 3) ? "line2" : "line1";
        budgetTable.getRowFormatter().setStyleName(lastCostRow, rowStyle);

        addCostInputs(lastCostRow);
        budgetTable.setWidget(lastCostRow, 0, new CheckBox());
        // budgetTable.setWidget(lastCostRow + 1, 1, addCostRowButton);

        accountInputCost.setFocus(true);

    }

    private void addInputEarningsRow() {

        if (currentEarningsEditRow > 0) {
            removeEarningsInputsAndSetValueInItsPlace(false);
        }
        // budgetTable.remove(addEarningRowButton);

        lastEarningsRow++;
        lastCostRow++;
        currentCostEditRow++;

        budgetTable.insertRow(lastEarningsRow);
        budgetTable.getCellFormatter().setStyleName(lastEarningsRow, 3, "right");

        String rowStyle = (((lastEarningsRow + 1) % 6) < 3) ? "line2" : "line1";
        budgetTable.getRowFormatter().setStyleName(lastEarningsRow, rowStyle);

        addEarningsInputs(lastEarningsRow);

        budgetTable.setWidget(lastEarningsRow, 0, new CheckBox());
        // budgetTable.setWidget(lastEarningsRow + 1, 1, addEarningRowButton);

        accountInputEarnings.setFocus(true);
    }

    private void addEarningsInputs(int row) {
        currentEarningsEditRow = row;
        budgetTable.setWidget(row, 1, accountInputEarnings);
        budgetTable.setWidget(row, 2, accountsEarnings);
        budgetTable.setWidget(row, 3, accountValueEarnings);
    }

    private void addCostInputs(int row) {
        currentCostEditRow = row;
        budgetTable.setWidget(row, 1, accountInputCost);
        budgetTable.setWidget(row, 2, accountsCost);
        budgetTable.setWidget(row, 3, accountValueCost);
    }

    private void removeEarningsInputsAndSetValueInItsPlace(boolean giveDefault) {
        budgetTable.remove(accountInputEarnings);
        budgetTable.setText(currentEarningsEditRow, 1, giveDefaultQuestionmarks(accountInputEarnings.getText(),
                giveDefault));
        accountInputEarnings.setText("");

        budgetTable.remove(accountsEarnings);
        budgetTable.setText(currentEarningsEditRow, 2, Util.getSelectedText(accountsEarnings));
        accountsEarnings.setSelectedIndex(0);

        budgetTable.remove(accountValueEarnings);
        budgetTable.setText(currentEarningsEditRow, 3, Util.money(accountValueEarnings.getText()));
        accountValueEarnings.setText("");
    }

    private void removeCostInputsAndSetValueInItsPlace(boolean giveDefault) {
        budgetTable.remove(accountInputCost);
        budgetTable.setText(currentCostEditRow, 1, giveDefaultQuestionmarks(accountInputCost.getText(), giveDefault));
        accountInputCost.setText("");

        budgetTable.remove(accountsCost);
        budgetTable.setText(currentCostEditRow, 2, Util.getSelectedText(accountsCost));
        accountsCost.setSelectedIndex(0);

        budgetTable.remove(accountValueCost);
        budgetTable.setText(currentCostEditRow, 3, Util.money(accountValueCost.getText()));
        accountValueCost.setText("");
    }

    private String giveDefaultQuestionmarks(String text, boolean giveDefault) {
        if (giveDefault && text.isEmpty()) {
            return "???";
        }
        return text;
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == addEarningRowButton) {
            addEarningClick();
        }
        if (event.getSource() == budgetTable) {
            budgetTableClick(event);
        }
        if (event.getSource() == addCostRowButton) {
            addCostClick();
        }
    }

    private void budgetTableClick(ClickEvent event) {
        EventTarget eventTarget = event.getNativeEvent().getEventTarget();

        Element element = Element.as(eventTarget);
        Element parent = element.getParentElement();

        if (!"tr".equalsIgnoreCase(parent.getTagName())) {
            return;
        }

        TableRowElement row = TableRowElement.as(parent);

        int rowIndex = row.getRowIndex();

        /* Do not push button or header lines... */
        if (rowIndex < 2 || (rowIndex > lastEarningsRow && rowIndex < lastEarningsRow + 3) || rowIndex > lastCostRow) {
            return;
        }

        if (rowIndex <= lastEarningsRow) {
            if (!accountInputEarnings.getText().isEmpty() || accountsEarnings.getSelectedIndex() > 0
                    || !accountValueEarnings.getText().isEmpty()) {
                if (!validateEarningsInputs()) {
                    return;
                }
            }

            removeEarningsInputsAndSetValueInItsPlace(true);

            accountInputEarnings.setText(budgetTable.getText(rowIndex, 1));
            Util.setIndexByItemText(accountsEarnings, budgetTable.getText(rowIndex, 2));
            accountValueEarnings.setText(budgetTable.getText(rowIndex, 3));
            addEarningsInputs(rowIndex);
        } else {
            if (!accountInputCost.getText().isEmpty() || accountsCost.getSelectedIndex() > 0
                    || !accountValueCost.getText().isEmpty()) {
                if (!validateCostInputs()) {
                    return;
                }
            }

            removeCostInputsAndSetValueInItsPlace(true);

            accountInputCost.setText(budgetTable.getText(rowIndex, 1));
            Util.setIndexByItemText(accountsCost, budgetTable.getText(rowIndex, 2));
            accountValueCost.setText(budgetTable.getText(rowIndex, 3));
            addCostInputs(rowIndex);

        }
    }

    private void addEarningClick() {
        if (!validateEarningsInputs()) {
            return;
        }
        addInputEarningsRow();
    }

    private boolean validateEarningsInputs() {
        MasterValidator mv = new MasterValidator();
        mv.money(messages.field_money(), accountValueEarnings);
        mv.mandatory(messages.required_field(), accountInputEarnings);
        mv.mandatory(messages.required_field(), accountsEarnings);

        if (!mv.fail(accountInputEarnings, checkDuplicate(accountInputEarnings.getText()), messages
                .account_already_used())) {
            return false;
        }

        return mv.validateStatus();
    }

    private boolean validateCostInputs() {
        MasterValidator mv = new MasterValidator();
        mv.money(messages.field_money(), accountValueCost);
        mv.mandatory(messages.required_field(), accountInputCost);
        mv.mandatory(messages.required_field(), accountsCost);

        if (!mv.fail(accountInputCost, checkDuplicate(accountInputCost.getText()), messages.account_already_used())) {
            return false;
        }

        return mv.validateStatus();
    }

    private boolean checkDuplicate(String account) {
        int match = 0;

        for (int i = 2; i <= lastCostRow; i++) {
            if (budgetTable.getCellCount(i) < 4) {
                continue;
            }

            Widget widget = budgetTable.getWidget(i, 1);

            String comp = null;

            if (widget != null) {
                if (!(widget instanceof TextBoxWithErrorText)) {
                    continue;
                }
                TextBoxWithErrorText b = (TextBoxWithErrorText) widget;
                comp = b.getText();

            } else {
                comp = budgetTable.getText(i, 1);
            }

            if (account.equals(comp)) {
                if (match++ > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private void addCostClick() {
        if (!validateCostInputs()) {
            return;
        }
        addInputCostRow();
    }

    private void loadBudgetData() {
        ServerResponse rh = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();

                fillYearData(object.get("result").isObject());
            }
        };
        AuthResponder.get(constants, messages, rh, constants.baseurl() + "accounting/budget.php?action=init");
    }

    protected void fillYearData(JSONObject object) {
        JSONObject costs = object.get("cost").isObject();
        JSONObject earnings = object.get("earnings").isObject();

        rowsForAccounts = new HashMap<String, Integer>();
        int numberOfBudgetYears = fillEarningsAndYearHeaders(earnings);
        fillCosts(costs);

        fillBlanks(numberOfBudgetYears);
    }

    private void fillBlanks(int numberOfBudgetYears) {
        int colMax = 4 + numberOfBudgetYears;

        for (int col = 4; col <= colMax; col++) {
            
            for (int row = 2; row < lastEarningsRow; row++) {
                if (!budgetTable.isCellPresent(row, col)) {
                    budgetTable.setText(row, col, " ");
                }
            }

            for (int rowC = (lastEarningsRow + 4); rowC < lastCostRow; rowC++) {
                if (!budgetTable.isCellPresent(rowC, col)) {
                    budgetTable.setText(rowC, col, " ");
                }
            }
        }
    }

    private void fillCosts(JSONObject costs) {
        List<String> keySet = new ArrayList<String>(costs.keySet());
        Collections.sort(keySet);

        int lastYear = findYearFromKey(keySet.get(keySet.size() - 1));

        for (String yearAndAccount : keySet) {
            JSONValue value = costs.get(yearAndAccount);
            int year2 = findYearFromKey(yearAndAccount);
            String account = findAccountFromKey(yearAndAccount);

            insertCostRow((4 + (lastYear - year2)), account, value);
        }

    }

    private int fillEarningsAndYearHeaders(JSONObject earnings) {
        List<String> keySet = new ArrayList<String>(earnings.keySet());
        Collections.sort(keySet);

        int firstYear = findYearFromKey(keySet.get(0));
        int lastYear = findYearFromKey(keySet.get(keySet.size() - 1));

        for (int year = firstYear; year <= lastYear; year++) {
            budgetTable.setText(0, 2 + (lastYear - year), "" + year);
        }

        // TODO fetch budget year more correctly
        budgetTable.setText(0, 1, "2010");

        for (String yearAndAccount : keySet) {
            JSONValue value = earnings.get(yearAndAccount);
            int year2 = findYearFromKey(yearAndAccount);
            String account = findAccountFromKey(yearAndAccount);

            insertEarningsRow((4 + (lastYear - year2)), account, value);
        }

        return lastYear - firstYear;
    }

    private void insertEarningsRow(int column, String account, JSONValue value) {
        int row = findEarningsRow(account);
        budgetTable.setText(row, column, Util.money(value));
        budgetTable.setText(row, 1, account);
        budgetTable.setText(row, 2, posttypeCache.getDescription(account));
    }

    private void insertCostRow(int column, String account, JSONValue value) {
        int row = findCostRow(account);
        budgetTable.setText(row, column, Util.money(value));
        budgetTable.setText(row, 1, account);
        budgetTable.setText(row, 2, posttypeCache.getDescription(account));
    }

    private int findCostRow(String account) {
        Integer alreadyAdded = rowsForAccounts.get(account);

        if (alreadyAdded != null) {
            return alreadyAdded;
        }

        addInputCostRow();
        int row = lastCostRow - 1;

        rowsForAccounts.put(account, row);
        return row;
    }

    private int findEarningsRow(String account) {
        Integer alreadyAdded = rowsForAccounts.get(account);

        if (alreadyAdded != null) {
            return alreadyAdded;
        }

        addInputEarningsRow();
        int row = lastEarningsRow - 1;

        rowsForAccounts.put(account, row);
        return row;
    }

    private String findAccountFromKey(String yearAndAccount) {
        return yearAndAccount.substring(5);
    }

    private int findYearFromKey(String s) {
        return Integer.parseInt(s.substring(0, 4));
    }

}
