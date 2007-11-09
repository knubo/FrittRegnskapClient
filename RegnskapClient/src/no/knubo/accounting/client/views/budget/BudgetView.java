package no.knubo.accounting.client.views.budget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BudgetView extends Composite {

    private static BudgetView me;
    private I18NAccount messages;
    private Constants constants;
    private HelpPanel helpPanel;
    private Elements elements;

    private FlexTable statusTable;
    private FlexTable springEarningsTable;
    private FlexTable fallEarningsTable;
    private FlexTable othersEarningsTable;
    private FlexTable expencesTable;

    public BudgetView(I18NAccount messages, Constants constants,
            HelpPanel helpPanel, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        statusTable = buildStatusTable();

        DockPanel dp = new DockPanel();

        TabPanel tabPanel = new TabPanel();
        tabPanel.add(createCourseEarningsView(), elements.earnings_courses());
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
        FlexTable table = new FlexTable();
        table.setStyleName("tablecells");
        table.setText(0, 0, elements.year());
        table.setText(0, 1, elements.budgeted_earnins());
        table.setText(0, 2, elements.budgeted_expences());
        table.setText(0, 3, elements.budgeted_result());
        table.setText(0, 4, elements.budget_result_actual());
        table.setText(0, 5, elements.budget_differance());
        table.getRowFormatter().setStyleName(0, "header desc");
        return table;
    }

    private Widget createExpencesView() {
        VerticalPanel vp = new VerticalPanel();

        expencesTable = new FlexTable();
        expencesTable.setStyleName("tablecells");
        expencesTable.setText(0, 0, elements.account());
        expencesTable.setText(0, 1, "");
        expencesTable.setText(0, 2, elements.description());
        expencesTable.setText(0, 3, elements.amount());
        expencesTable.setText(0, 4, elements.count());
        expencesTable.setText(0, 5, elements.sum());
        expencesTable.getRowFormatter().setStyleName(0, "header");

        vp.add(expencesTable);

        return vp;
    }

    private Widget createOtherEarningsView() {
        VerticalPanel vp = new VerticalPanel();

        othersEarningsTable = new FlexTable();
        othersEarningsTable.setStyleName("tablecells");
        othersEarningsTable.setText(0, 0, elements.account());
        othersEarningsTable.setText(0, 1, "");
        othersEarningsTable.setText(0, 2, elements.description());
        othersEarningsTable.setText(0, 3, elements.amount());
        othersEarningsTable.setText(0, 4, elements.count());
        othersEarningsTable.setText(0, 5, elements.sum());
        othersEarningsTable.getRowFormatter().setStyleName(0, "header");

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

    private FlexTable createEarningsTable() {
        FlexTable earningsTable = new FlexTable();
        earningsTable.setStyleName("tablecells");
        return earningsTable;
    }

    private void fillEarningsTable(FlexTable earningsTable) {
        earningsTable.setText(0, 0, "");
        earningsTable.setText(1, 0, elements.year_membership());
        earningsTable.setText(2, 0, elements.course_membership());
        earningsTable.setText(3, 0, elements.train_membership());
        earningsTable.setText(4, 0, elements.sum());
    }

    private FlexTable buildStatusTable() {
        FlexTable flexTable = new FlexTable();
        flexTable.setStyleName("tablecells");
        flexTable.setText(0, 0, elements.year());
        flexTable.setText(1, 0, elements.earnings_courses());
        flexTable.setText(2, 0, elements.earnings_all());
        flexTable.setText(3, 0, elements.expences());
        flexTable.getColumnFormatter().setStyleName(0, "header desc");

        return flexTable;
    }

    public static BudgetView show(I18NAccount messages, Constants constants,
            HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new BudgetView(messages, constants, helpPanel, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void init() {
        fillEarningsTable(springEarningsTable);
        fillEarningsTable(fallEarningsTable);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/members.php?action=overview");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue value = JSONParser.parse(responseText);

                JSONObject root = value.isObject();
                ArrayList keys = new ArrayList(root.keySet());
                Collections.sort(keys);
                init(keys, root);
                helpPanel.resize(me);
            }
        };
        try {
            builder.sendRequest("", new AuthResponder(constants, messages,
                    callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    protected void init(List keys, JSONObject root) {
        int pos = 0;

        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = (String) i.next();

            String year = key.substring(0, 4);
            String fall = key.substring(5).trim();

            JSONObject obj = root.get(key).isObject();

            int yearcount = Util.getInt(obj.get("year"));
            int coursecount = Util.getInt(obj.get("course"));
            int traincount = Util.getInt(obj.get("train"));

            int column = 1 + (pos++ * 2);

            if ("0".equals(fall)) {
                FlexTable table = springEarningsTable;

                Label label = new Label(elements.spring() + " " + year);
                Image editImage = ImageFactory.editImage("budget_edit_spring");

                fiillColumn(yearcount, coursecount, traincount, column, table,
                        label, editImage);
            } else {
                FlexTable table = fallEarningsTable;

                Label label = new Label(elements.fall() + " " + year);
                Image editImage = ImageFactory.editImage("budget_edit_spring");

                fiillColumn(yearcount, coursecount, traincount, column, table,
                        label, editImage);
            }
        }
        springEarningsTable.getColumnFormatter().setStyleName(0, "header desc");
        springEarningsTable.getRowFormatter().setStyleName(0, "header desc");
        fallEarningsTable.getColumnFormatter().setStyleName(0, "header desc");
        fallEarningsTable.getRowFormatter().setStyleName(0, "header desc");
    }

    private void fiillColumn(int yearcount, int coursecount, int traincount,
            int column, FlexTable table, Label label, Image editImage) {

        label.setStyleName("nowrap headernobox");
        HorizontalPanel header = new HorizontalPanel();
        header.setStyleName("noborder");
        header.add(label);
        header.add(editImage);

        table.setWidget(0, column, header);
        table.setText(0, column + 1, elements.sum());
        table.setText(1, column, String.valueOf(yearcount));
        table.setText(2, column, String.valueOf(coursecount));
        table.setText(3, column, String.valueOf(traincount));

        table.getCellFormatter().setStyleName(1, column, "center");
        table.getCellFormatter().setStyleName(2, column, "center");
        table.getCellFormatter().setStyleName(3, column, "center");
    }

    public static class YearSeason {
        final int year;
        final int season;

        public YearSeason(int year, int season) {
            this.year = year;
            this.season = season;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + season;
            result = prime * result + year;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof YearSeason))
                return false;
            YearSeason other = (YearSeason) obj;
            if (season != other.season)
                return false;
            if (year != other.year)
                return false;
            return true;
        }

    }
}
