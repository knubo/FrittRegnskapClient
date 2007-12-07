package no.knubo.accounting.client.views.budget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BudgetView extends Composite implements ClickListener {

    private static BudgetView me;
    private I18NAccount messages;
    private Constants constants;
    private HelpPanel helpPanel;
    private Elements elements;

    private AccountTable statusTable;
    private AccountTable springEarningsTable;
    private AccountTable fallEarningsTable;
    private AccountTable othersEarningsTable;
    private AccountTable expencesTable;
    private HashMap coursePrices;
    private HashMap trainPrices;
    private HashMap yearPrices;
    private HashMap yearSums;
    private IdHolder membershipIdHolder;
    private BudgetEditMembershipEditFields editFields;

    public BudgetView(I18NAccount messages, Constants constants, HelpPanel helpPanel,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        statusTable = buildStatusTable();

        DockPanel dp = new DockPanel();

        membershipIdHolder = new IdHolder();

        TabPanel tabPanel = new TabPanel();
        tabPanel.add(createCourseEarningsView(), elements.earnings_memberships());
        tabPanel.add(createOtherEarningsView(), elements.earnings_other());
        tabPanel.add(createExpencesView(), elements.expences());
        tabPanel.add(createResultView(), elements.budget_result());

        tabPanel.selectTab(0);

        dp.add(statusTable, DockPanel.NORTH);
        dp.setSpacing(5);
        dp.add(tabPanel, DockPanel.NORTH);

        initWidget(dp);
    }

    private Widget createResultView() {
        AccountTable table = new AccountTable("tablecells");
        table.setText(0, 0, elements.year());
        table.setText(0, 1, elements.budgeted_earnins());
        table.setText(0, 2, elements.budgeted_expences());
        table.setText(0, 3, elements.budgeted_result());
        table.setText(0, 4, elements.budget_result_actual());
        table.setText(0, 5, elements.budget_differance());
        table.setHeaderRowStyle(0);
        return table;
    }

    private Widget createExpencesView() {
        VerticalPanel vp = new VerticalPanel();

        expencesTable = new AccountTable("tablecells");
        expencesTable.setText(0, 0, elements.account());
        expencesTable.setText(0, 1, "");
        expencesTable.setText(0, 2, elements.description());
        expencesTable.setText(0, 3, elements.amount());
        expencesTable.setText(0, 4, elements.count());
        expencesTable.setText(0, 5, elements.sum());
        expencesTable.setHeaderRowStyle(0);

        vp.add(expencesTable);

        return vp;
    }

    private Widget createOtherEarningsView() {
        VerticalPanel vp = new VerticalPanel();

        othersEarningsTable = new AccountTable("tablecells");
        othersEarningsTable.setText(0, 0, elements.account());
        othersEarningsTable.setText(0, 1, "");
        othersEarningsTable.setText(0, 2, elements.description());
        othersEarningsTable.setText(0, 3, elements.amount());
        othersEarningsTable.setText(0, 4, elements.count());
        othersEarningsTable.setText(0, 5, elements.sum());
        othersEarningsTable.setHeaderRowStyle(0);

        vp.add(othersEarningsTable);

        return vp;
    }

    private Widget createCourseEarningsView() {
        VerticalPanel vp = new VerticalPanel();

        springEarningsTable = createEarningsTable();
        fallEarningsTable = createEarningsTable();

        vp.add(springEarningsTable);
        vp.add(fallEarningsTable);

        return vp;
    }

    private AccountTable createEarningsTable() {
        AccountTable earningsTable = new AccountTable("tablecells");
        return earningsTable;
    }

    private void initEarningsTable(AccountTable earningsTable) {
        earningsTable.setText(0, 0, "");
        earningsTable.setText(1, 0, elements.year_membership());
        earningsTable.setText(2, 0, elements.course_membership());
        earningsTable.setText(3, 0, elements.train_membership());
        earningsTable.setText(4, 0, elements.sum());
    }

    private AccountTable buildStatusTable() {
        AccountTable AccountTable = new AccountTable("tablecells");
        AccountTable.setText(0, 0, elements.year());
        AccountTable.setText(1, 0, elements.earnings_memberships());
        AccountTable.setText(2, 0, elements.earnings_all());
        AccountTable.setText(3, 0, elements.expences());
        AccountTable.setHeaderRowStyle(0);
        AccountTable.setHeaderColStyle(0);
        return AccountTable;
    }

    public static BudgetView show(I18NAccount messages, Constants constants, HelpPanel helpPanel,
            Elements elements) {
        if (me == null) {
            me = new BudgetView(messages, constants, helpPanel, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void init() {
        membershipIdHolder.init();
        initEarningsTable(springEarningsTable);
        initEarningsTable(fallEarningsTable);
        yearSums = new HashMap();

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {

                JSONObject root = value.isObject();
                fillPrices(root.get("price").isObject());
                JSONObject members = root.get("members").isObject();
                ArrayList keys = new ArrayList(members.keySet());
                Collections.sort(keys);
                fillMemberships(keys, members);
                fillOverallView();
                helpPanel.resize(me);
            }

        };

        AuthResponder.get(constants, messages, callback, "accounting/budget.php?action=init");
    }

    protected void fillOverallView() {
        ArrayList sums = new ArrayList(yearSums.values());
        Collections.sort(sums);

        int col = 1;
        for (Iterator i = sums.iterator(); i.hasNext();) {
            YearSum yearSum = (YearSum) i.next();

            statusTable.setInt(0, col, yearSum.getYear());
            statusTable.setMoney(1, col, yearSum.getCourse());
            statusTable.setMoney(2, col, yearSum.getTotal());
            col++;
        }
    }

    protected void fillPrices(JSONObject priceObj) {
        JSONArray course = priceObj.get("course").isArray();
        JSONArray train = priceObj.get("train").isArray();
        JSONArray year = priceObj.get("year").isArray();

        coursePrices = new HashMap();
        trainPrices = new HashMap();
        yearPrices = new HashMap();

        for (int i = 0; i < course.size(); i++) {
            JSONObject obj = course.get(i).isObject();
            coursePrices.put(Util.str(obj.get("semester")), Util.str(obj.get("amount")));
        }

        for (int i = 0; i < train.size(); i++) {
            JSONObject obj = train.get(i).isObject();
            trainPrices.put(Util.str(obj.get("semester")), Util.str(obj.get("amount")));
        }

        for (int i = 0; i < year.size(); i++) {
            JSONObject obj = year.get(i).isObject();
            yearPrices.put(Util.str(obj.get("year")), Util.str(obj.get("amount")));
        }
    }

    protected void fillMemberships(List yearFallKeys, JSONObject members) {
        int fallCol = 1;
        int springCol = 1;

        for (Iterator i = yearFallKeys.iterator(); i.hasNext();) {
            String key = (String) i.next();

            String year = key.substring(0, 4);
            String fall = key.substring(5).trim();

            JSONObject obj = members.get(key).isObject();

            if ("0".equals(fall)) {
                String label = elements.spring() + " " + year;

                springCol += fillColumn(obj, year, springCol, springEarningsTable, label);
            } else {
                String label = elements.fall() + " " + year;

                fallCol += fillColumn(obj, year, fallCol, fallEarningsTable, label);
            }
        }
        springEarningsTable.setHeaderColStyle(0);
        springEarningsTable.setHeaderRowStyle(0);
        fallEarningsTable.setHeaderColStyle(0);
        fallEarningsTable.setHeaderRowStyle(0);
    }

    private int fillColumn(JSONObject obj, String year, int column, AccountTable table, String label) {
        int yearcount = Util.getInt(obj.get("year"));
        int coursecount = Util.getInt(obj.get("course"));
        int traincount = Util.getInt(obj.get("train"));
        String semester = Util.str(obj.get("semester"));

        table.setText(0, column, label);

        /* If budget column, add a column with the edit image */
        if (obj.containsKey("budget")) {
            Image editImage = ImageFactory.editImage("edit_budget_memberships");
            editImage.addClickListener(this);
            membershipIdHolder.addObject(obj, editImage);

            table.setWidget(0, column + 1, editImage);
            table.getFlexCellFormatter().setColSpan(1, column, 2);
            table.getFlexCellFormatter().setColSpan(2, column, 2);
            table.getFlexCellFormatter().setColSpan(3, column, 2);
            table.getFlexCellFormatter().setColSpan(4, column, 2);
        }
        int addpos = (obj.containsKey("budget") ? 2 : 1);

        table.setText(0, column + addpos, elements.sum());

        double sumYear = calcSum(yearcount, year, yearPrices);
        table.setInt(1, column, yearcount);
        table.setMoney(1, column + 1, sumYear);
        table.setTooltip(1, column + 1, elements.cost_membership() + ":" + yearPrices.get(year));

        double sumCourse = calcSum(coursecount, semester, coursePrices);
        table.setInt(2, column, coursecount);
        table.setMoney(2, column + 1, sumCourse);
        table.setTooltip(2, column + 1, elements.cost_course() + ":" + coursePrices.get(semester));

        double sumTrain = calcSum(traincount, semester, trainPrices);
        table.setInt(3, column, traincount);
        table.setMoney(3, column + 1, sumTrain);
        table.setTooltip(3, column + 1, elements.cost_practice() + ":" + trainPrices.get(semester));

        double sum = sumYear + sumCourse + sumTrain;
        addYearCourse(year, sum);
        table.setMoney(4, column + 1, sum);

        table.getCellFormatter().setStyleName(1, column, "center");
        table.getCellFormatter().setStyleName(2, column, "center");
        table.getCellFormatter().setStyleName(3, column, "center");

        return 1 + addpos;
    }

    private void addYearCourse(String year, double sum) {
        YearSum data = (YearSum) yearSums.get(year);

        if (data == null) {
            data = new YearSum(Integer.parseInt(year));
            yearSums.put(year, data);
        }

        data.addCourse(sum);
    }

    private double calcSum(int count, String key, HashMap prices) {
        if (prices == null) {
            return 0;
        }

        String price = (String) prices.get(key);
        if (price == null) {
            return 0;
        }
        return count * Double.parseDouble(price);
    }

    public void onClick(Widget sender) {
        Object obj = membershipIdHolder.findObject(sender);

        if (obj != null) {
            doEdit((JSONObject) obj, sender);

        }
    }

    private void doEdit(JSONObject obj, Widget sender) {
        if (editFields == null) {
            editFields = new BudgetEditMembershipEditFields(messages, constants, elements);
        }

        int left = sender.getAbsoluteLeft() + 10;

        int top = sender.getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        editFields.init(this, obj);
        editFields.show();

    }

}
