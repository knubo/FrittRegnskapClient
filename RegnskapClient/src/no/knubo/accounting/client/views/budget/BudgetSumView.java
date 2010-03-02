package no.knubo.accounting.client.views.budget;

import java.util.HashMap;
import java.util.Set;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class BudgetSumView extends DialogBox {

    private static BudgetSumView me;
    private FlexTable table;
    private int lowestYear;
    private int highestYear;
    private final Elements elements;
    private final I18NAccount messages;

    private BudgetSumView(Elements elements, I18NAccount messages) {

        this.elements = elements;
        this.messages = messages;

        setText(elements.sum());
        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tablecells");

        table.setText(0, 0, elements.year());
        table.setText(1, 0, elements.sum_earnings());
        table.setText(2, 0, elements.sum_cost());
        table.setText(3, 0, elements.budget_result());

        table.getRowFormatter().setStyleName(0, "header");
        table.getColumnFormatter().setStyleName(0, "header");

        dp.add(table, DockPanel.NORTH);

        setModal(false);
        setWidget(dp);
    }

    public static BudgetSumView getInstance(Elements elements, I18NAccount messages) {
        if (me == null) {
            me = new BudgetSumView(elements, messages);
        }
        return me;
    }

    public void calculateSumPreviousYears(JSONObject resultData) {
        JSONObject costs = resultData.get("cost").isObject();
        JSONObject earnings = resultData.get("earnings").isObject();

        HashMap<String, Double> sumCosts = calcSum(costs);
        HashMap<String, Double> sumEarnings = calcSum(earnings);

        for (int year = lowestYear; year <= highestYear; year++) {
            int col = 2 + (highestYear - year);
            String yearS = String.valueOf(year);
            table.setText(0, col, yearS);
            table.setText(1, col, Util.money(default0(sumEarnings.get(yearS))));
            table.setText(2, col, Util.money(default0(sumCosts.get(yearS))));
            table.setText(3, col, Util.money(default0(sumEarnings.get(yearS)) - default0(sumCosts.get(yearS))));
            table.getCellFormatter().setStyleName(1, col, "right");
            table.getCellFormatter().setStyleName(2, col, "right");
            table.getCellFormatter().setStyleName(3, col, "right");
        }

    }

    private Double default0(Double double1) {
        if (double1 == null) {
            return 0d;
        }
        return double1;
    }

    private HashMap<String, Double> calcSum(JSONObject costs) {
        HashMap<String, Double> yearGivesSum = new HashMap<String, Double>();

        lowestYear = 10000;
        highestYear = 0;

        Set<String> keys = costs.keySet();

        for (String k : keys) {
            String year = k.substring(0, 4);

            int yearInt = Integer.parseInt(year);
            if (yearInt < lowestYear) {
                lowestYear = yearInt;
            } else if (yearInt > highestYear) {
                highestYear = yearInt;
            }

            double amount = Util.getDouble(costs.get(k));

            if (!yearGivesSum.containsKey(year)) {
                yearGivesSum.put(year, amount);
            } else {
                yearGivesSum.put(year, yearGivesSum.get(year) + amount);
            }
        }

        return yearGivesSum;
    }

    public void setSumData(String year, Double earning, Double cost, Double sum) {
        table.setText(0, 1, messages.budget(year));
        table.setText(1, 1, Util.money(earning));
        table.setText(2, 1, Util.money(cost));
        table.setText(3, 1, Util.money(sum));
        table.getCellFormatter().setStyleName(1, 1, "right");
        table.getCellFormatter().setStyleName(2, 1, "right");
        table.getCellFormatter().setStyleName(3, 1, "right");
    }
}
