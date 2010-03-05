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
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class BudgetDrawDelegate {

    int lastEarningsRow;
    int lastCostRow;
    HashMap<String, Integer> rowsForAccounts;
    int currentEarningsEditRow;
    int currentCostEditRow;

    final BudgetView view;
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;
    private PosttypeCache posttypeCache;

    public BudgetDrawDelegate(BudgetView view, Elements elements, I18NAccount messages, Constants constants) {
        this.view = view;
        this.elements = elements;
        this.messages = messages;
        this.constants = constants;
    }

    public void init() {
        rowsForAccounts = new HashMap<String, Integer>();
        posttypeCache = PosttypeCache.getInstance(constants, messages);

        lastEarningsRow = 1;
        addInputEarningsRow();

        view.budgetTable.setWidget(lastEarningsRow + 1, 1, view.addEarningRowButton);
        view.budgetTable.setWidget(lastEarningsRow + 1, 2, view.sumEarningCheckBox);
        view.budgetTable.setWidget(lastEarningsRow + 1, 3, view.earningsSumLabel);

        addInitialCostRow();

        view.budgetTable.setWidget(lastCostRow + 1, 1, view.addCostRowButton);
        view.budgetTable.setWidget(lastCostRow + 1, 2, view.sumCostCheckBox);
        view.budgetTable.setWidget(lastCostRow + 1, 3, view.costSumLabel);

    }

    void addInitialCostRow() {
        lastCostRow = lastEarningsRow + 3;
        view.budgetTable.setText(lastCostRow, 0, elements.budgeted_expences());
        view.budgetTable.getFlexCellFormatter().setColSpan(lastCostRow, 0, 3);
        view.budgetTable.getRowFormatter().setStyleName(lastCostRow, "header");

        addInputCostRow();
    }

    void addInputCostRow() {

        removeCostInputsAndSetValueInItsPlace(false);
        // view.budgetTable.remove(addCostRowButton);

        lastCostRow++;
        view.budgetTable.insertRow(lastCostRow);
        view.budgetTable.getCellFormatter().setStyleName(lastCostRow, 3, "right");

        String rowStyle = (((lastCostRow + 1) % 6) < 3) ? "line2" : "line1";
        view.budgetTable.getRowFormatter().setStyleName(lastCostRow, rowStyle);

        addCostInputs(lastCostRow);
        view.budgetTable.setWidget(lastCostRow, 0, new CheckBox());
        // view.budgetTable.setWidget(lastCostRow + 1, 1, addCostRowButton);

    }

    void budgetTableClick(ClickEvent event) {
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
            if (!view.accountInputEarnings.getText().isEmpty() || view.accountsEarnings.getSelectedIndex() > 0
                    || !view.accountValueEarnings.getText().isEmpty()) {
                if (!validateEarningsInputs()) {
                    return;
                }
            }

            removeEarningsInputsAndSetValueInItsPlace(true);

            view.accountInputEarnings.setText(view.budgetTable.getText(rowIndex, 1));
            Util.setIndexByItemText(view.accountsEarnings, view.budgetTable.getText(rowIndex, 2));
            view.accountValueEarnings.setText(view.budgetTable.getText(rowIndex, 3));
            addEarningsInputs(rowIndex);
        } else {
            if (!view.accountInputCost.getText().isEmpty() || view.accountsCost.getSelectedIndex() > 0
                    || !view.accountValueCost.getText().isEmpty()) {
                if (!validateCostInputs()) {
                    return;
                }
            }

            removeCostInputsAndSetValueInItsPlace(true);

            view.accountInputCost.setText(view.budgetTable.getText(rowIndex, 1));
            Util.setIndexByItemText(view.accountsCost, view.budgetTable.getText(rowIndex, 2));
            view.accountValueCost.setText(view.budgetTable.getText(rowIndex, 3));
            addCostInputs(rowIndex);

        }
    }

    void addInputEarningsRow() {

        if (currentEarningsEditRow > 0) {
            removeEarningsInputsAndSetValueInItsPlace(false);
        }
        // view.budgetTable.remove(addEarningRowButton);

        lastEarningsRow++;
        lastCostRow++;
        currentCostEditRow++;

        view.budgetTable.insertRow(lastEarningsRow);
        view.budgetTable.getCellFormatter().setStyleName(lastEarningsRow, 3, "right");

        String rowStyle = (((lastEarningsRow + 1) % 6) < 3) ? "line2" : "line1";
        view.budgetTable.getRowFormatter().setStyleName(lastEarningsRow, rowStyle);

        addEarningsInputs(lastEarningsRow);

        view.budgetTable.setWidget(lastEarningsRow, 0, new CheckBox());
    }

    void addEarningsInputs(int row) {
        currentEarningsEditRow = row;

        String account = view.budgetTable.getText(row, 1);

        view.budgetTable.setWidget(row, 3, view.accountValueEarnings);

        if (!rowsForAccounts.containsKey(account)) {
            view.budgetTable.setWidget(row, 1, view.accountInputEarnings);
            view.budgetTable.setWidget(row, 2, view.accountsEarnings);

            view.accountInputEarnings.setFocus(true);
        } else {
            view.accountValueEarnings.setFocus(true);
        }

    }

    void addCostInputs(int row) {
        currentCostEditRow = row;
        String account = view.budgetTable.getText(row, 1);

        view.budgetTable.setWidget(row, 3, view.accountValueCost);
        if (!rowsForAccounts.containsKey(account)) {
            view.budgetTable.setWidget(row, 1, view.accountInputCost);
            view.budgetTable.setWidget(row, 2, view.accountsCost);

            view.accountInputCost.setFocus(true);
        } else {
            view.accountValueCost.setFocus(true);
        }
    }

    void removeEarningsInputsAndSetValueInItsPlace(boolean giveDefault) {
        view.budgetTable.remove(view.accountInputEarnings);
        view.budgetTable.setText(currentEarningsEditRow, 1, giveDefaultQuestionmarks(view.accountInputEarnings
                .getText(), giveDefault));
        view.accountInputEarnings.setText("");

        view.budgetTable.remove(view.accountsEarnings);
        view.budgetTable.setText(currentEarningsEditRow, 2, Util.getSelectedText(view.accountsEarnings));
        view.accountsEarnings.setSelectedIndex(0);

        view.budgetTable.remove(view.accountValueEarnings);
        view.budgetTable.setText(currentEarningsEditRow, 3, Util.money(view.accountValueEarnings.getText()));
        view.accountValueEarnings.setText("");
    }

    void removeCostInputsAndSetValueInItsPlace(boolean giveDefault) {
        view.budgetTable.remove(view.accountInputCost);
        view.budgetTable.setText(currentCostEditRow, 1, giveDefaultQuestionmarks(view.accountInputCost.getText(),
                giveDefault));
        view.accountInputCost.setText("");

        view.budgetTable.remove(view.accountsCost);
        view.budgetTable.setText(currentCostEditRow, 2, Util.getSelectedText(view.accountsCost));
        view.accountsCost.setSelectedIndex(0);

        view.budgetTable.remove(view.accountValueCost);
        view.budgetTable.setText(currentCostEditRow, 3, Util.money(view.accountValueCost.getText()));
        view.accountValueCost.setText("");
    }

    String giveDefaultQuestionmarks(String text, boolean giveDefault) {
        if (giveDefault && text.isEmpty()) {
            return "???";
        }
        return text;
    }

    protected void fillYearData(JSONObject object) {
        JSONObject costs = object.get("cost").isObject();
        JSONObject earnings = object.get("earnings").isObject();

        int numberOfBudgetYears = fillEarningsAndYearHeaders(earnings);
        fillCosts(costs);

        fillBlanks(numberOfBudgetYears);
    }

    void fillBlanks(int numberOfBudgetYears) {
        int colMax = 4 + numberOfBudgetYears;

        for (int col = 4; col <= colMax; col++) {

            for (int row = 2; row < lastEarningsRow; row++) {
                if (!view.budgetTable.isCellPresent(row, col)) {
                    view.budgetTable.setText(row, col, " ");
                }
            }

            for (int rowC = (lastEarningsRow + 4); rowC < lastCostRow; rowC++) {
                if (!view.budgetTable.isCellPresent(rowC, col)) {
                    view.budgetTable.setText(rowC, col, " ");
                }
            }
        }
    }

    void fillCosts(JSONObject costs) {
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

    int fillEarningsAndYearHeaders(JSONObject earnings) {
        List<String> keySet = new ArrayList<String>(earnings.keySet());
        Collections.sort(keySet);

        int firstYear = findYearFromKey(keySet.get(0));
        int lastYear = findYearFromKey(keySet.get(keySet.size() - 1));

        for (int year = firstYear; year <= lastYear; year++) {
            view.budgetTable.setText(0, 2 + (lastYear - year), "" + year);
        }

        for (String yearAndAccount : keySet) {
            JSONValue value = earnings.get(yearAndAccount);
            int year2 = findYearFromKey(yearAndAccount);
            String account = findAccountFromKey(yearAndAccount);

            insertEarningsRow((4 + (lastYear - year2)), account, value);
        }

        return lastYear - firstYear;
    }

    void insertEarningsRow(int column, String account, JSONValue value) {
        int row = findEarningsRow(account);
        view.budgetTable.setText(row, column, Util.money(value));
        view.budgetTable.getCellFormatter().setStyleName(row, column, "right");
        view.budgetTable.setText(row, 1, account);
        view.budgetTable.setText(row, 2, posttypeCache.getDescription(account));
    }

    void insertCostRow(int column, String account, JSONValue value) {
        int row = findCostRow(account);
        view.budgetTable.setText(row, column, Util.money(value));
        view.budgetTable.getCellFormatter().setStyleName(row, column, "right");
        view.budgetTable.setText(row, 1, account);
        view.budgetTable.setText(row, 2, posttypeCache.getDescription(account));
    }

    int findCostRow(String account) {
        Integer alreadyAdded = rowsForAccounts.get(account);

        if (alreadyAdded != null) {
            return alreadyAdded;
        }

        addInputCostRow();
        int row = lastCostRow - 1;

        rowsForAccounts.put(account, row);
        return row;
    }

    int findEarningsRow(String account) {
        Integer alreadyAdded = rowsForAccounts.get(account);

        if (alreadyAdded != null) {
            return alreadyAdded;
        }

        addInputEarningsRow();
        int row = lastEarningsRow - 1;

        rowsForAccounts.put(account, row);
        return row;
    }

    void addEarningClick() {
        if (!validateEarningsInputs()) {
            return;
        }
        addInputEarningsRow();
    }

    private boolean validateEarningsInputs() {
        MasterValidator mv = new MasterValidator();

        if (!(view.accountValueEarnings.getText().isEmpty() && rowsForAccounts.containsKey(view.accountInputEarnings
                .getText()))) {
            mv.money(messages.field_money(), view.accountValueEarnings);
        }
        mv.mandatory(messages.required_field(), view.accountInputEarnings);
        mv.mandatory(messages.required_field(), view.accountsEarnings);

        if (!mv.fail(view.accountInputEarnings, checkDuplicate(view.accountInputEarnings.getText()), messages
                .account_already_used())) {
            return false;
        }

        return mv.validateStatus();
    }

    private boolean validateCostInputs() {
        MasterValidator mv = new MasterValidator();
        if (!(view.accountValueCost.getText().isEmpty() && rowsForAccounts.containsKey(view.accountInputCost.getText()))) {
            mv.money(messages.field_money(), view.accountValueCost);
        }
        mv.mandatory(messages.required_field(), view.accountInputCost);
        mv.mandatory(messages.required_field(), view.accountsCost);

        if (!mv.fail(view.accountInputCost, checkDuplicate(view.accountInputCost.getText()), messages
                .account_already_used())) {
            return false;
        }

        return mv.validateStatus();
    }

    private boolean checkDuplicate(String account) {
        int match = 0;

        for (int i = 2; i <= lastCostRow; i++) {
            if (view.budgetTable.getCellCount(i) < 4) {
                continue;
            }

            Widget widget = view.budgetTable.getWidget(i, 1);

            String comp = null;

            if (widget != null) {
                if (!(widget instanceof TextBoxWithErrorText)) {
                    continue;
                }
                TextBoxWithErrorText b = (TextBoxWithErrorText) widget;
                comp = b.getText();

            } else {
                comp = view.budgetTable.getText(i, 1);
            }

            if (account.equals(comp)) {
                if (match++ > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    void addCostClick() {
        if (!validateCostInputs()) {
            return;
        }
        addInputCostRow();
    }

    String findAccountFromKey(String yearAndAccount) {
        return yearAndAccount.substring(5);
    }

    int findYearFromKey(String s) {
        return Integer.parseInt(s.substring(0, 4));
    }

    public JSONArray getBudgetData() {
        JSONArray data = new JSONArray();
        int pos = 0;
        for (int row = 2; row < lastEarningsRow; row++) {
            String value = getValue(row, 3);

            if (value.trim().isEmpty()) {
                continue;
            }

            JSONObject obj = new JSONObject();

            obj.put("value", new JSONString(value));
            obj.put("postType", new JSONString(getValue(row, 1)));
            obj.put("earning", new JSONString("true"));

            data.set(pos++, obj);
        }

        for (int rowC = (lastEarningsRow + 4); rowC < lastCostRow; rowC++) {
            String value = getValue(rowC, 3);

            if (value.trim().isEmpty()) {
                continue;
            }

            JSONObject obj = new JSONObject();

            obj.put("value", new JSONString(value));
            obj.put("postType", new JSONString(getValue(rowC, 1)));
            obj.put("cost", new JSONString("true"));

            data.set(pos++, obj);
        }

        return data;
    }

    private String getValue(int row, int column) {
        TextBoxWithErrorText widget = (TextBoxWithErrorText) view.budgetTable.getWidget(row, column);

        if (widget != null) {
            return widget.getText();
        }
        return view.budgetTable.getText(row, column);
    }

    public void addBudget(String account, JSONValue value, boolean earning) {
        Integer row = null;
        if (!rowsForAccounts.containsKey(account)) {
            if (earning) {
                addInputEarningsRow();
                row = lastEarningsRow - 1;
            } else {
                addInputCostRow();
                row = lastCostRow - 1;
            }
            view.budgetTable.setText(row, 1, account);
            view.budgetTable.setText(row, 2, posttypeCache.getDescription(account));
        } else {
            row = rowsForAccounts.get(account);
        }

        view.budgetTable.setText(row, 3, Util.money(value));
    }

    public void removeAllRowsWithNoBudgetData() {
        for (int rowC = (lastCostRow - 1); rowC >= (lastEarningsRow + 4); rowC--) {
            if (getValue(rowC, 3).isEmpty()) {
                view.budgetTable.removeRow(rowC);
                lastCostRow--;
                currentCostEditRow--;
            }
        }

        for (int row = (lastEarningsRow - 1); row >= 2; row--) {

            if (getValue(row, 3).isEmpty()) {
                view.budgetTable.removeRow(row);
                lastCostRow--;
                lastEarningsRow--;
                currentEarningsEditRow--;
                currentCostEditRow--;
            }
        }

    }

    public double calcBudgetCost() {
        double result = 0;

        for (int rowC = (lastEarningsRow + 4); rowC < lastCostRow; rowC++) {
            String value = getValue(rowC, 3);

            if(value.trim().isEmpty()) {
                continue;
            }
            
            result += Double.parseDouble(value.trim().replaceAll(",", ""));
        }

        return result;
    }

    public double calcBudgetEarnings() {
        double result = 0;

        for (int row = 2; row < lastEarningsRow; row++) {
            String value = getValue(row, 3);

            if(value.trim().isEmpty()) {
                continue;
            }
            result += Double.parseDouble(value.trim().replaceAll(",", ""));
        }

        return result;
    }

    public void clearBudgetFields() {
        
    }
}
