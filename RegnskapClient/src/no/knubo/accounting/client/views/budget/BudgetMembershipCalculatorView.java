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
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class BudgetMembershipCalculatorView extends DialogBox implements ClickHandler, KeyUpHandler {

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
    private String budgetYear;
    private String budgetSemesterSpring;
    private String budgetSemesterFall;
    private BudgetView budgetView;
    private final Constants constants;

    private BudgetMembershipCalculatorView(Constants constants, Elements elements, I18NAccount messages) {

        this.constants = constants;
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
            text.addDelayedKeyUpHandler(this);
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
        transferButton.setAccessKey('t');
        hp.add(transferButton);

        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.setAccessKey('c');
        cancelButton.addClickHandler(this);
        hp.add(cancelButton);

        dp.add(hp, DockPanel.NORTH);

        setWidget(dp);
    }

    public static BudgetMembershipCalculatorView getInstance(Constants constants, Elements elements, I18NAccount messages) {
        if (me == null) {
            me = new BudgetMembershipCalculatorView(constants, elements, messages);
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
        MasterValidator mv = validateInputs();
        if (!mv.validateStatus()) {
            return;
        }

        saveMembershipBudget();

        double sumYear = getValue(10, 1) + getValue(11, 1);
        double sumCourse = getValue(12, 1) + getValue(13, 1) + getValue(14, 1) + getValue(15, 1) + getValue(16, 1)
                + getValue(17, 1);

        budgetView.sumsFromCalculator(sumYear, sumCourse);
        hide();

    }

    private void saveMembershipBudget() {
        JSONObject data = new JSONObject();
        data.put("year", new JSONString(budgetYear));
        data.put("year_members", inputValue(1));
        data.put("year_youth", inputValue(2));
        data.put("spring_course", inputValue(3));
        data.put("fall_course", inputValue(4));
        data.put("spring_train", inputValue(5));
        data.put("fall_train", inputValue(6));
        data.put("spring_youth", inputValue(7));
        data.put("fall_youth", inputValue(8));
        
        StringBuffer params = new StringBuffer();
        params.append("action=saveMemberships");
        Util.addPostParam(params, "memberships", data.toString());

        ServerResponse rh = new ServerResponse() {
            
            public void serverResponse(JSONValue responseObj) {
                if(responseObj == null) {
                    Window.alert("No server result from save of budget membership. They were probably not saved.");
                }
            }
        };
        AuthResponder.post(constants, messages, rh , params, constants.baseurl() + "accounting/budget.php");

    }

    private JSONString inputValue(int row) {
         TextBoxWithErrorText t = (TextBoxWithErrorText) table.getWidget(row, 1);
        
         if(t.getText().isEmpty()) {
             return new JSONString("0");
         }
        
        return new JSONString(t.getText());
    }

    public void init(BudgetView budgetView, JSONObject members, JSONObject prices, JSONArray semesters,
            String budgetYear, JSONObject membersbudget) {
        this.budgetView = budgetView;
        this.budgetYear = budgetYear;
        try {
            fillPrices(prices);
            fillMembers(members);
            calculateSumsForExistingYears();
            fillBudget(budgetYear);
            fillSemestersForBudgetYear(semesters);
            setStylesForBudgetColumn();
            fillBudgetInputs(membersbudget);
        } catch (Exception e) {
            Util.log(e.toString());
        }
    }

    private void fillBudgetInputs(JSONObject membersbudget) {
        Util.log(membersbudget.toString());
        setInputValue(1, membersbudget, "year_members");
        setInputValue(2, membersbudget, "year_youth");
        setInputValue(3, membersbudget, "spring_course");
        setInputValue(4, membersbudget, "spring_train");
        setInputValue(5, membersbudget, "spring_youth");
        setInputValue(6, membersbudget, "fall_course");
        setInputValue(7, membersbudget, "fall_train");
        setInputValue(8, membersbudget, "fall_youth");

        onKeyUp(null);
    }

    private void setInputValue(int row, JSONObject membersbudget, String key) {
        TextBoxWithErrorText t = (TextBoxWithErrorText) table.getWidget(row, 1);
        t.setText(Util.str(membersbudget.get(key)));
    }

    private void fillSemestersForBudgetYear(JSONArray semesters) {

        budgetSemesterSpring = null;
        budgetSemesterFall = null;

        for (int i = 0; i < semesters.size(); i++) {
            JSONObject semester = semesters.get(i).isObject();

            if (Util.str(semester.get("year")).equals(budgetYear)) {
                if (Util.str(semester.get("fall")).equals("1")) {
                    budgetSemesterFall = Util.str(semester.get("semester"));
                } else {
                    budgetSemesterSpring = Util.str(semester.get("semester"));
                }
            }
        }

        if (budgetSemesterFall == null || budgetSemesterSpring == null) {
            String msg = "No semesters defined for budget year:" + budgetYear
                    + ". This dialog will not work properly. (" + budgetSemesterSpring + "," + budgetSemesterFall + ")";
            Window.alert(msg);
        }
    }

    private void fillBudget(String budgetYear) {
        table.setText(0, 1, budgetYear);
        table.setText(9, 1, budgetYear);
    }

    private void calculateSumsForExistingYears() {

        int columnCount = table.getCellCount(0);

        for (int col = 2; col < columnCount; col++) {
            double sum = 0;
            for (int row = 11; row <= 17; row++) {
                if (table.isCellPresent(row, col)) {
                    sum += getValue(row, col);
                }
            }
            table.setText(18, col, Util.money(sum));
            table.getCellFormatter().setStyleName(18, col, "right");
        }
    }

    private double getValue(int row, int col) {
        String text = table.getText(row, col);
        if (text.isEmpty() || text.equals(messages.not_a_number())) {
            return 0;
        }
        return Double.parseDouble(text.replaceAll(",", ""));
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

    public void onKeyUp(KeyUpEvent event) {
        MasterValidator mv = validateInputs();
        if (!mv.validateStatus()) {
            return;
        }

        calcBudgetPrice(1, priceYear.get(budgetYear));
        calcBudgetPrice(2, priceYearYouth.get(budgetYear));
        calcBudgetPrice(3, priceCourse.get(this.budgetSemesterSpring));
        calcBudgetPrice(4, priceCourse.get(this.budgetSemesterFall));
        calcBudgetPrice(5, priceTrain.get(this.budgetSemesterSpring));
        calcBudgetPrice(6, priceTrain.get(this.budgetSemesterFall));
        calcBudgetPrice(7, priceYouth.get(this.budgetSemesterSpring));
        calcBudgetPrice(8, priceYouth.get(this.budgetSemesterFall));
        calcNewBudgetTotal();
    }

    private MasterValidator validateInputs() {
        MasterValidator mv = new MasterValidator();
        for (int row = 1; row <= 8; row++) {
            mv.range(messages.not_a_number(), 0, 99999, table.getWidget(row, 1));
        }
        return mv;
    }

    private void calcNewBudgetTotal() {
        double sum = 0;
        for (int row = 11; row <= 17; row++) {
            sum += getValue(row, 1);
        }
        table.setText(18, 1, Util.money(sum));
    }

    private void calcBudgetPrice(int row, Double price) {
        int count = getCount(row);

        if (count == 0) {
            table.setText(row + 9, 1, "");
        } else if (price == null) {
            table.setText(row + 9, 1, messages.not_a_number());
        } else {
            table.setText(row + 9, 1, Util.money(price * count));
        }
    }

    private void setStylesForBudgetColumn() {
        for (int row = 10; row <= 18; row++) {
            table.setText(row, 1, "");
            table.getCellFormatter().setStyleName(row, 1, "right");
        }
    }

    private int getCount(int row) {
        TextBoxWithErrorText text = (TextBoxWithErrorText) table.getWidget(row, 1);
        if (text.getText().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text.getText());
    }

}
