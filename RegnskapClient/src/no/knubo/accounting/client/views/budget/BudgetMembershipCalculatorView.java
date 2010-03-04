package no.knubo.accounting.client.views.budget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class BudgetMembershipCalculatorView extends DialogBox implements ClickHandler {

    private I18NAccount messages;
    private FlexTable table;
    private static BudgetMembershipCalculatorView me;
    private NamedButton transferButton;
    private NamedButton cancelButton;
    private ArrayList<TextBoxWithErrorText> inputs;
    private HashMap<String, Double> priceYear;
    private HashMap<String, Double> priceYearYouth;
    private HashMap<String, Double> priceCourse;
    private HashMap<String, Double> priceTrain;
    private HashMap<String, Double> priceYouth;
    private final Elements elements;

    private BudgetMembershipCalculatorView(Elements elements, I18NAccount messages) {

        this.elements = elements;
        this.messages = messages;

        setStyleName("popup");

        setText(elements.membership_numbers());
        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tablecells");

        table.getRowFormatter().setStyleName(0, "header");
        table.getColumnFormatter().setStyleName(0, "header");

        table.setText(0, 0, elements.year());
        table.setText(1, 0, elements.member_heading_year());
        table.setText(2, 0, elements.year_membership_youth());
        table.setText(3, 0, elements.member_heading_course() + " " + elements.spring());
        table.setText(4, 0, elements.member_heading_course() + " " + elements.fall());
        table.setText(5, 0, elements.member_heading_train() + " " + elements.spring());
        table.setText(6, 0, elements.member_heading_train() + " " + elements.fall());
        table.setText(7, 0, elements.youth_membership() + " " + elements.spring());
        table.setText(8, 0, elements.youth_membership() + " " + elements.fall());

        inputs = new ArrayList<TextBoxWithErrorText>();
        for (int i = 1; i <= 8; i++) {
            TextBoxWithErrorText text = new TextBoxWithErrorText("number_input", true);
            table.setWidget(i, 1, text);
            text.setVisibleLength(5);
            text.setMaxLength(5);
            inputs.add(text);
        }

        table.setText(9, 0, elements.earnings());
        table.getRowFormatter().setStyleName(9, "header");

        table.setText(10, 0, elements.member_heading_year());
        table.setText(11, 0, elements.year_membership_youth());
        table.setText(12, 0, elements.member_heading_course() + " " + elements.spring());
        table.setText(13, 0, elements.member_heading_course() + " " + elements.fall());
        table.setText(14, 0, elements.member_heading_train() + " " + elements.spring());
        table.setText(15, 0, elements.member_heading_train() + " " + elements.fall());
        table.setText(16, 0, elements.youth_membership() + " " + elements.spring());
        table.setText(17, 0, elements.youth_membership() + " " + elements.fall());
        table.setText(18, 0, elements.sum());

        dp.add(table, DockPanel.NORTH);
        HorizontalPanel hp = new HorizontalPanel();

        transferButton = new NamedButton("transfer_budget", elements.transfer_to_budget());
        transferButton.addClickHandler(this);
        hp.add(transferButton);

        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);
        hp.add(cancelButton);

        dp.add(hp, DockPanel.NORTH);

        setWidget(dp);
    }

    public static BudgetMembershipCalculatorView getInstance(Elements elements, I18NAccount messages) {
        if (me == null) {
            me = new BudgetMembershipCalculatorView(elements, messages);
        }
        return me;
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            hide();
        }
        if (event.getSource() == transferButton) {
            transferEarnings();
        }
    }

    private void transferEarnings() {
    }

    public void init(JSONObject members, JSONObject prices) {
        fillPrices(prices);
        fillMembers(members);
        calculateSumsForExistingYears();
    }

    private void calculateSumsForExistingYears() {
        // TODO Auto-generated method stub
        
    }

    private void fillPrices(JSONObject prices) {
        priceYear = buildYearPrice(prices.get("year").isArray(), "amount");
        priceYearYouth = buildYearPrice(prices.get("year").isArray(), "amountyouth");
        priceCourse = buildSemesterPrice(prices.get("course").isArray());
        priceTrain = buildSemesterPrice(prices.get("train").isArray());
        priceYouth = buildSemesterPrice(prices.get("youth").isArray());
    }

    private HashMap<String, Double> buildSemesterPrice(JSONArray array) {

        HashMap<String, Double> result = new HashMap<String, Double>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();
            String semester = Util.str(obj.get("semester"));
            double cost = Util.getDouble(obj.get("amount"));

            result.put(semester, cost);
        }
        return result;
    }

    private HashMap<String, Double> buildYearPrice(JSONArray array, String key) {

        HashMap<String, Double> result = new HashMap<String, Double>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();
            String year = Util.str(obj.get("year"));
            double cost = Util.getDouble(obj.get(key));

            result.put(year, cost);
        }
        return result;
    }

    private void fillMembers(JSONObject members) {
        Set<String> yearSemesters = members.keySet();

        List<String> years = findYearsSorted(yearSemesters, members);
        Map<String, Integer> yearColumns = makeYearCols(years);

        for (String yearSemester : yearSemesters) {
            JSONObject data = members.get(yearSemester).isObject();
            
            if (data.containsKey("budget")) {
                // TODO fix budget column.
                continue;
            }

            String year = yearSemester.substring(0, 4);
            boolean spring = yearSemester.endsWith("0");

            int col = 2 + yearColumns.get(year);


            try {
                if (spring) {
                    setYearData(year, col, data);
                    setYearYouthData(year, col, data);

                    setText(3, col, data, "course", priceCourse);
                    setText(5, col, data, "train", priceTrain);
                    setText(7, col, data, "youth", priceYouth);
                } else {
                    setText(4, col, data, "course", priceCourse);
                    setText(6, col, data, "train", priceTrain);
                    setText(8, col, data, "youth", priceYouth);
                }
            } catch (Exception e) {
                Util.log(e.toString());
            }

        }

        setYearHeaders(years);

    }

    private void setYearData(String year, int col, JSONObject data) {
        String strSkipNull = Util.strSkipNull(data.get("year"));

        table.setText(1, col, strSkipNull);
        table.getCellFormatter().setStyleName(1, col, "right");
        if (priceYear.containsKey(year)) {
            table.setText(10, col, Util.money(Util.getInt(strSkipNull) * priceYear.get(year)));
        } else {
            table.setText(10, col, messages.not_a_number());
        }
        table.getCellFormatter().setStyleName(10, col, "right");
    }

    private void setYearYouthData(String year, int col, JSONObject data) {
        if (!data.containsKey("yearyouth")) {
            return;
        }
        String strSkipNull = Util.strSkipNull(data.get("yearyouth"));
        table.setText(2, col, strSkipNull);
        table.getCellFormatter().setStyleName(2, col, "right");
        if (priceYearYouth.containsKey("year")) {
            table.setText(11, col, Util.money(Util.getInt(strSkipNull) * priceYearYouth.get(year)));
        } else {
            table.setText(11, col, messages.not_a_number());
        }
        table.getCellFormatter().setStyleName(11, col, "right");
    }

    private void setText(int row, int col, JSONObject data, String field, HashMap<String, Double> price) {
        String strSkipNull = Util.strSkipNull(data.get(field));
        String semester = Util.str(data.get("semester"));
        table.setText(row, col, strSkipNull);
        table.getCellFormatter().setStyleName(row, col, "right");

        if (price.containsKey(semester)) {
            table.setText(row + 9, col, Util.money(Util.getInt(strSkipNull) * price.get(semester)));
        } else {
            table.setText(row + 9, col, messages.not_a_number());
        }
        table.getCellFormatter().setStyleName(row + 9, col, "right");

    }

    private void setYearHeaders(List<String> years) {
        int col = 2;
        for (String year : years) {
            table.setText(0, col, year);
            table.setText(9, col, year);
            col++;
        }
    }

    private List<String> findYearsSorted(Set<String> yearSemesters, JSONObject members) {
        ArrayList<String> result = new ArrayList<String>();

        for (String yearAndSemester : yearSemesters) {
            String year = yearAndSemester.substring(0, 4);

            if (members.get(yearAndSemester).isObject().containsKey("budget")) {
                continue;
            }

            if (!result.contains(year)) {
                result.add(year);
            }
        }

        Collections.sort(result, Collections.reverseOrder());

        return result;
    }

    private Map<String, Integer> makeYearCols(List<String> result) {
        HashMap<String, Integer> yearCol = new HashMap<String, Integer>();

        int pos = 0;
        for (String string : result) {
            yearCol.put(string, pos++);
        }

        return yearCol;
    }

}
