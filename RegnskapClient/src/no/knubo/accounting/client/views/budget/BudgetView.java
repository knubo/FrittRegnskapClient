package no.knubo.accounting.client.views.budget;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.help.HelpPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
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
        table.setStyleName("tableborder");
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

        FlexTable table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.account());
        table.setText(0, 1, "");
        table.setText(0, 2, elements.description());
        table.setText(0, 3, elements.amount());
        table.setText(0, 4, elements.count());
        table.setText(0, 5, elements.sum());
        table.getRowFormatter().setStyleName(0, "header");

        vp.add(table);

        return vp;
    }

    private Widget createOtherEarningsView() {
        VerticalPanel vp = new VerticalPanel();

        FlexTable table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.account());
        table.setText(0, 1, "");
        table.setText(0, 2, elements.description());
        table.setText(0, 3, elements.amount());
        table.setText(0, 4, elements.count());
        table.setText(0, 5, elements.sum());
        table.getRowFormatter().setStyleName(0, "header");

        vp.add(table);

        return vp;
    }

    private Widget createCourseEarningsView() {
        VerticalPanel vp = new VerticalPanel();

        FlexTable springTable = createEarningsTable();
        FlexTable fallTable = createEarningsTable();

        vp.add(springTable);
        vp.add(fallTable);

        return vp;
    }

    private FlexTable createEarningsTable() {
        FlexTable earningsTable = new FlexTable();
        earningsTable.setStyleName("tableborder");
        earningsTable.setText(0, 0, elements.year_membership());
        earningsTable.setText(1, 0, elements.course_membership());
        earningsTable.setText(2, 0, elements.train_membership());
        earningsTable.setText(3, 0, elements.sum());
        earningsTable.getColumnFormatter().setStyleName(0, "header desc");
        return earningsTable;
    }

    private FlexTable buildStatusTable() {
        FlexTable flexTable = new FlexTable();
        flexTable.setStyleName("tableborder");
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

}
