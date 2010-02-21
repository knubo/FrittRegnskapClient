package no.knubo.accounting.client.views.budget;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.help.HelpPanel;
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

        budgetTable.getFlexCellFormatter().setColSpan(0, 0, 4);
        budgetTable.setText(0, 0, "2010 " + elements.budgeted_earnins());

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
        lastEarningsRow = 1;
        addInputEarningsRow();
        addInitialCostRow();
    }

    private void addInitialCostRow() {
        lastCostRow = lastEarningsRow + 3;
        budgetTable.setText(lastCostRow, 0, "2010 " + elements.budgeted_expences());
        budgetTable.getFlexCellFormatter().setColSpan(lastCostRow, 0, 4);
        budgetTable.getRowFormatter().setStyleName(lastCostRow, "header");

        addInputCostRow();
    }

    private void addInputCostRow() {

        removeCostInputsAndSetValueInItsPlace(false);
        budgetTable.remove(addCostRowButton);

        lastCostRow++;
        budgetTable.insertRow(lastCostRow);
        budgetTable.getCellFormatter().setStyleName(lastCostRow, 3, "right");

        String rowStyle = (((lastCostRow + 1) % 6) < 3) ? "line2" : "line1";
        budgetTable.getRowFormatter().setStyleName(lastCostRow, rowStyle);

        addCostInputs(lastCostRow);
        budgetTable.setWidget(lastCostRow, 0, new CheckBox());
        budgetTable.setWidget(lastCostRow + 1, 1, addCostRowButton);

        accountInputCost.setFocus(true);

    }

    private void addInputEarningsRow() {

        if (currentEarningsEditRow > 0) {
            removeEarningsInputsAndSetValueInItsPlace(false);
        }
        budgetTable.remove(addEarningRowButton);

        lastEarningsRow++;
        lastCostRow++;
        currentCostEditRow++;
        
        budgetTable.insertRow(lastEarningsRow);
        budgetTable.getCellFormatter().setStyleName(lastEarningsRow, 3, "right");

        String rowStyle = (((lastEarningsRow + 1) % 6) < 3) ? "line2" : "line1";
        budgetTable.getRowFormatter().setStyleName(lastEarningsRow, rowStyle);

        addEarningsInputs(lastEarningsRow);

        budgetTable.setWidget(lastEarningsRow, 0, new CheckBox());
        budgetTable.setWidget(lastEarningsRow + 1, 1, addEarningRowButton);

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
}
