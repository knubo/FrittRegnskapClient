package no.knubo.accounting.client.views.budget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BudgetSimpleTracking extends Composite implements ClickHandler {

    private static BudgetSimpleTracking me;
    private ListBoxWithErrorText yearBox;
    private final I18NAccount messages;
    private final Constants constants;
    private NamedButton selectButton;
    private FlexTable table;
    private PosttypeCache posttypeCache;
    private final Elements elements;

    public BudgetSimpleTracking(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        DockPanel dp = new DockPanel();

        HorizontalPanel hp = new HorizontalPanel();
        dp.add(hp, DockPanel.NORTH);
        hp.add(new Label(elements.choose_year()));

        yearBox = new ListBoxWithErrorText("budget_years");
        hp.add(yearBox);

        selectButton = new NamedButton("select_year", elements.select_budget_year());
        selectButton.addClickHandler(this);
        dp.add(selectButton, DockPanel.NORTH);

        table = new FlexTable();
        table.setStyleName("tableborder");

        table.setText(0, 0, elements.account());
        table.setText(0, 1, elements.description());
        table.setText(0, 2, elements.budget());
        table.setText(0, 3, elements.budget_result_actual());
        table.setText(0, 4, elements.budget_differance());
        table.setText(0, 5, "%");
        table.setText(0, 6, elements.status());

        table.getRowFormatter().setStyleName(0, "header desc");

        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public static Widget getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new BudgetSimpleTracking(messages, constants, elements);
        }
        return me;
    }

    public void init() {
        posttypeCache = PosttypeCache.getInstance(constants, messages);
        ServerResponse rh = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                yearBox.clear();

                for (int i = 0; i < array.size(); i++) {
                    yearBox.addItem(array.get(i).isObject().get("year"));
                }
            }
        };
        AuthResponder.get(constants, messages, rh, "accounting/budget.php?action=years");

    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == selectButton) {
            selectBudgetYear();
        }
    }

    private void selectBudgetYear() {
        String year = Util.getSelected(yearBox.getListbox());
        if (year == null || year.isEmpty()) {
            return;
        }

        ServerResponse rh = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                try {
                    fillBudgetAndResult(responseObj.isObject());
                } catch (Exception e) {
                    Util.log(e.toString());
                }
            }
        };
        AuthResponder.get(constants, messages, rh, "accounting/budget.php?action=simplestatus&year=" + year);

    }

    protected void fillBudgetAndResult(JSONObject object) {
        cleanTable();
        addEarningsHeader();

        HashMap<String, Row> rows = new HashMap<String, Row>();
        parseBudget(object.get("budget").isArray(), rows);
        parseActual(object.get("result").isObject(), rows);

        List<Row> values = new ArrayList<Row>(rows.values());
        Collections.sort(values);

        printValues(values);

    }

    private void parseActual(JSONObject object, Map<String, Row> rows) {
        JSONObject costs = object.get("cost").isObject();
        JSONObject earnings = object.get("earnings").isObject();

        loopAndAddActual(rows, costs, false);
        loopAndAddActual(rows, earnings, true);
    }

    private void loopAndAddActual(Map<String, Row> rows, JSONObject costs, boolean flag) {
        Set<String> costKeys = costs.keySet();
        for (String key : costKeys) {
            double value = getDouble(costs.get(key));
            String postType = key.substring(key.indexOf('-') + 1);

            addActual(rows, postType, value, flag);
        }
    }

    private double getDouble(JSONValue jsonValue) {
        String strSkipNull = Util.strSkipNull(jsonValue);

        if (strSkipNull.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(strSkipNull.trim());
    }

    private void addActual(Map<String, Row> rows, String postType, double value, boolean b) {
        Row row = rows.get(postType);
        if (row == null) {
            row = new Row();
            row.account = postType;
            rows.put(postType, row);
        }
        row.actual = value;
        row.earning = b;
    }

    private void addBudgeted(Map<String, Row> rows, String postType, double value, boolean b) {
        Row row = rows.get(postType);
        if (row == null) {
            row = new Row();
            row.account = postType;
            rows.put(postType, row);
        }
        row.budget = value;
        row.earning = b;
    }

    private void parseBudget(JSONArray array, Map<String, Row> rows) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject budgeted = array.get(i).isObject();

            addBudgeted(rows, Util.str(budgeted.get("post_type")), getDouble(budgeted.get("amount")), Util.str(
                    budgeted.get("earning")).equals("1") ? true : false);
        }
    }

    private void cleanTable() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
    }

    private void printValues(List<Row> values) {
        int row = 2;
        String rowStyle = "line1";

        double sumBudget = 0;
        double sumActual = 0;
        double earningsBudget = 0;
        double earningsActual = 0;
        boolean givenCostHeader = false;

        for (Row value : values) {
            if (!givenCostHeader && !value.earning) {
                row += addCostHeader(row, sumBudget, sumActual);

                earningsBudget = sumBudget;
                earningsActual = sumActual;
                sumBudget = 0;
                sumActual = 0;
                givenCostHeader = true;
            }

            table.setText(row, 0, value.account);
            table.setText(row, 1, posttypeCache.getDescription(value.account));
            table.setText(row, 2, Util.money(value.budget));
            table.getCellFormatter().setStyleName(row, 2, "right");
            table.setText(row, 3, Util.money(value.actual));
            table.getCellFormatter().setStyleName(row, 3, "right");
            table.setText(row, 4, Util.money(value.getDifference()));
            table.getCellFormatter().setStyleName(row, 4, "right");
            table.setText(row, 5, value.getPercent());
            table.getCellFormatter().setStyleName(row, 5, "right");

            sumBudget += value.budget;
            sumActual += value.actual;

            table.getCellFormatter().setStyleName(row, 6, value.getColor());

            if ((row - 2) % 3 == 0) {
                rowStyle = (rowStyle.equals("line1")) ? "line2" : "line1";
            }

            table.getRowFormatter().setStyleName(row, rowStyle + " desc");

            row++;
        }
        addSumCostAndTotal(row, sumBudget, sumActual, earningsBudget, earningsActual);
    }

    private void addSumCostAndTotal(int row, double sumCostBudget, double sumCostActual, double sumEarningsBudget,
            double sumEarningsActual) {
        addSumline(row, sumCostBudget, sumCostActual, false);

        table.setText(row + 1, 0, elements.budget_result());
        table.getRowFormatter().setStyleName(row + 1, "header");
        table.getFlexCellFormatter().setColSpan(row + 1, 0, 7);
        addSumline(row + 2, sumEarningsBudget - sumCostBudget, sumEarningsActual - sumCostActual, true);

    }

    private void addEarningsHeader() {
        table.setText(1, 0, elements.budgeted_earnins());
        table.getFlexCellFormatter().setColSpan(1, 0, 7);
        table.getRowFormatter().setStyleName(1, "header");
    }

    private int addCostHeader(int row, double sumBudget, double sumActual) {
        addSumline(row, sumBudget, sumActual, true);

        table.setText(row + 1, 0, elements.budgeted_expences());
        table.getFlexCellFormatter().setColSpan(row + 1, 0, 7);
        table.getRowFormatter().setStyleName(row + 1, "header");
        return 2;
    }

    private void addSumline(int row, double sumBudget, double sumActual, boolean isEarnings) {
        Row sum = new Row();
        sum.actual = sumActual;
        sum.budget = sumBudget;
        sum.earning = isEarnings;

        table.setText(row, 1, elements.sum());
        table.setText(row, 2, Util.money(sumBudget));
        table.setText(row, 3, Util.money(sumActual));
        table.setText(row, 4, Util.money(sum.getDifference()));
        table.setText(row, 5, sum.getPercent());

        table.getCellFormatter().setStyleName(row, 2, "right");
        table.getCellFormatter().setStyleName(row, 3, "right");
        table.getCellFormatter().setStyleName(row, 4, "right");
        table.getCellFormatter().setStyleName(row, 5, "right");
        table.getCellFormatter().setStyleName(row, 6, sum.getColor());

        table.getRowFormatter().setStyleName(row, "sumline");
    }

    static class Row implements Comparable<Row> {
        String account;
        double budget;
        double actual;
        boolean earning;

        @Override
        public int compareTo(Row r) {

            int earningComp = Boolean.valueOf(r.earning).compareTo(Boolean.valueOf(earning));
            if (earningComp != 0) {
                return earningComp;
            }

            return r.account.compareTo(account);
        }

        public double getDifference() {
            return actual - budget;
        }

        public String getColor() {
            if (earning) {
                if (actual > budget) {
                    return "green";
                }
            }
            if (!earning) {
                double p = percentOnly();
                if (p > 100) {
                    return "red";
                }
                if (p > 90) {
                    return "yellow";
                }
            }

            return "";
        }

        public String getPercent() {
            if (budget == 0) {
                return "";
            }
            return Util.numberDecimalFormat(String.valueOf(percentOnly())) + " %";
        }

        private double percentOnly() {
            return (actual / budget) * 100;
        }
    }

}
