package no.knubo.accounting.client.views.budget;

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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
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
                JSONValue yearObj = root.get("year");
                JSONValue trainObj = root.get("train");
                JSONValue courseObj = root.get("course");
                if (yearObj != null) {
                    initForYear(yearObj.isArray());
                }
                if (trainObj != null) {
                    initForTrain(trainObj.isArray());
                }
                if (courseObj != null) {
                    initForCourse(courseObj.isArray());
                }

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

    protected void initForCourse(JSONArray array) {
    }

    protected void initForTrain(JSONArray array) {

    }

    protected void initForYear(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONValue value = array.get(i);

            JSONObject object = value.isObject();

            String year = Util.str(object.get("year"));
            int count = Util.getInt(object.get("C"));

            int springCount = count / 2;
            int fallCount = count - springCount;

            int column = 1 + (i * 2);

            Label springLabel = new Label(elements.spring() + " " + year);
            springLabel.setStyleName("nowrap headernobox");
            HorizontalPanel hpSpring = new HorizontalPanel();
            hpSpring.setStyleName("noborder");
            hpSpring.add(springLabel);

            Image editSpringImage = ImageFactory
                    .editImage("budget_edit_spring");
            hpSpring.add(editSpringImage);

            springEarningsTable.setWidget(0, column, hpSpring);
            springEarningsTable.setText(0, column + 1, elements.sum());
            springEarningsTable.setText(1, column, String.valueOf(springCount));
            springEarningsTable.getCellFormatter().setStyleName(1, column,
                    "center");

            Label fallLabel = new Label(elements.fall() + " " + year);
            fallLabel.setStyleName("nowrap headernobox");
            HorizontalPanel hpFall = new HorizontalPanel();
            hpFall.setStyleName("noborder");
            hpFall.add(fallLabel);

            Image editFallImage = ImageFactory.editImage("budget_edit_spring");
            hpFall.add(editFallImage);

            fallEarningsTable.setWidget(0, column, hpFall);
            fallEarningsTable.setText(0, column + 1, elements.sum());
            fallEarningsTable.setText(1, column, String.valueOf(fallCount));
            fallEarningsTable.getCellFormatter().setStyleName(1, column,
                    "center");
        }
        fallEarningsTable.getColumnFormatter().setStyleName(0, "header desc");
        fallEarningsTable.getRowFormatter().setStyleName(0, "header desc");
        springEarningsTable.getColumnFormatter().setStyleName(0, "header desc");
        springEarningsTable.getRowFormatter().setStyleName(0, "header desc");

    }
}
