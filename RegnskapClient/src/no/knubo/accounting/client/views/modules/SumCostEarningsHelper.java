package no.knubo.accounting.client.views.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlexTable;

public class SumCostEarningsHelper {

    private final FlexTable earningsAndCostAccounts;
    private final FlexTable otherAccounts;
    private final Elements elements;
    private final Constants constants;
    private final I18NAccount messages;
    private PosttypeCache posttypeCache;

    public SumCostEarningsHelper(Elements elements, Constants constants, I18NAccount messages) {
        this.elements = elements;
        this.constants = constants;
        this.messages = messages;
        earningsAndCostAccounts = new FlexTable();
        earningsAndCostAccounts.setStyleName("tableborder");
        otherAccounts = new FlexTable();
        otherAccounts.setStyleName("tableborder");
    }

    public FlexTable getEarningsAndCost() {
        return earningsAndCostAccounts;
    }

    public FlexTable getOther() {
        return otherAccounts;
    }

    public void renderResult(JSONArray array, Boolean render) {
        try {
            earningsAndCostAccounts.removeAllRows();
            otherAccounts.removeAllRows();

            posttypeCache = PosttypeCache.getInstance(constants, messages);

            if (!Boolean.TRUE.equals(render)) {
                return;
            }

            HashMap<String, Double> earnings = new HashMap<String, Double>();
            HashMap<String, Double> costs = new HashMap<String, Double>();
            HashMap<String, Double> other = new HashMap<String, Double>();
            fill(array, earnings, costs, other);

            earningsAndCostAccounts.setText(0, 0, elements.earnings());
            earningsAndCostAccounts.getRowFormatter().setStyleName(0, "header");
            earningsAndCostAccounts.getFlexCellFormatter().setColSpan(0, 0, 3);
            double sumEarning = fillTable(earningsAndCostAccounts, earnings);

            int row = earningsAndCostAccounts.getRowCount();
            earningsAndCostAccounts.setText(row, 0, elements.expences());
            earningsAndCostAccounts.getFlexCellFormatter().setColSpan(row, 0, 3);
            earningsAndCostAccounts.getRowFormatter().setStyleName(row, "header");
            double sumCost = fillTable(earningsAndCostAccounts, costs);

            fillResult(sumEarning, sumCost);

            otherAccounts.setText(0, 0, elements.other_accounts());
            otherAccounts.getRowFormatter().setStyleName(0, "header");
            otherAccounts.getFlexCellFormatter().setColSpan(0, 0, 3);
            fillTable(otherAccounts, other);
        } catch (Exception e) {
            Util.log(e.toString());
        }

    }

    private void fillResult(double sumEarning, double sumCost) {
        int row = earningsAndCostAccounts.getRowCount();

        earningsAndCostAccounts.setText(row, 0, elements.budget_result());
        earningsAndCostAccounts.getRowFormatter().setStyleName(row, "header");
        earningsAndCostAccounts.getFlexCellFormatter().setColSpan(row, 0, 3);
        row++;
        earningsAndCostAccounts.setText(row, 1, elements.sum());
        earningsAndCostAccounts.setText(row, 2, Util.money(sumEarning - sumCost));
        earningsAndCostAccounts.getCellFormatter().setStyleName(row, 2, "right");
        earningsAndCostAccounts.getRowFormatter().setStyleName(row, "sumline");
    }

    private double fillTable(FlexTable table, HashMap<String, Double> posts) {
        ArrayList<String> l = new ArrayList<String>(posts.keySet());
        Collections.sort(l);

        double sum = 0;

        int row = table.getRowCount();
        for (String account : l) {
            Double value = posts.get(account);

            sum += value;

            table.setText(row, 0, account);
            table.setText(row, 1, posttypeCache.getDescription(account));
            table.setText(row, 2, Util.money(value));
            table.getCellFormatter().setStyleName(row, 2, "right");

            String style = (row % 2 == 0) ? "smallerfont showlineposts2" : "smallerfont showlineposts1";
            table.getRowFormatter().setStyleName(row, style);

            row++;
        }

        table.setText(row, 1, elements.sum());
        table.setText(row, 2, Util.money(sum));
        table.getCellFormatter().setStyleName(row, 2, "right");

        table.getRowFormatter().setStyleName(row, "sumline");

        return sum;

    }

    private void fill(JSONArray array, HashMap<String, Double> earnings, HashMap<String, Double> costs,
            HashMap<String, Double> other) {

        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();

            if (!object.containsKey("postArray")) {
                continue;
            }

            JSONValue postArrVal = object.get("postArray");

            if (postArrVal == null) {
                continue;
            }

            JSONArray postArr = postArrVal.isArray();

            if (postArr == null) {
                continue;
            }

            for (int j = 0; j < postArr.size(); j++) {
                JSONValue postVal = postArr.get(j);

                JSONObject postObj = postVal.isObject();
                String posttype = Util.str(postObj.get("Post_type"));
                int debet = Util.getInt(postObj.get("Debet"));

                double amount = Util.getDouble(postObj.get("Amount"));
                boolean earning = Util.getBoolean(postObj.get("Earning"));
                boolean cost = Util.getBoolean(postObj.get("Cost"));

                if (earning) {
                    addToMap(earnings, posttype, amount * debet * -1);
                } else if (cost) {
                    addToMap(costs, posttype, amount * debet);
                } else {
                    addToMap(other, posttype, amount * debet);
                }
            }
        }
    }

    private void addToMap(HashMap<String, Double> map, String posttype, double d) {
        if (!map.containsKey(posttype)) {
            map.put(posttype, d);
            return;
        }

        map.put(posttype, map.get(posttype) + d);
    }

}
