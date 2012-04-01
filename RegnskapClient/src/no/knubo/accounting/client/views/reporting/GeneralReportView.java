package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class GeneralReportView extends Composite implements ClickHandler, ServerResponseString {

    private static GeneralReportView me;

    private final Constants constants;

    private final I18NAccount messages;

    private NamedButton reportButton;

    private HTML result;

    private TextBoxWithErrorText yearTextBox;

    private TextBoxWithErrorText monthTextBox;

    private HorizontalPanel hp;

    private Label monthLabel;

    enum Mode {
        SUM_YEARS, MISSING_YEAR_MEMBERS, MISSING_SEMESTER_MEMBERS
    }

    Mode currentMode = null;

    private Label semesterLabel;

    private ListBox semesterBox;

    private Label yearLabel;

    public GeneralReportView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();

        reportButton = new NamedButton("GeneralReport.reportButton", elements.do_report());
        reportButton.addClickHandler(this);

        yearTextBox = new TextBoxWithErrorText("year");
        monthTextBox = new TextBoxWithErrorText("month");

        semesterLabel = new Label(elements.semester());
        semesterBox = new ListBox();

        hp = new HorizontalPanel();
        yearLabel = new Label(elements.year());
        hp.add(yearLabel);
        hp.add(yearTextBox);
        monthLabel = new Label(elements.month());
        hp.add(monthLabel);
        hp.add(monthTextBox);

        hp.add(semesterLabel);
        hp.add(semesterBox);

        dp.add(hp, DockPanel.NORTH);
        dp.add(reportButton, DockPanel.NORTH);

        result = new HTML();
        dp.add(result, DockPanel.NORTH);

        initWidget(dp);
    }

    void init() {
        hp.setVisible(false);
        monthLabel.setVisible(false);
        monthTextBox.setVisible(false);
        reportButton.setVisible(false);
        semesterBox.setVisible(false);
        semesterLabel.setVisible(false);
        yearLabel.setVisible(false);
        yearTextBox.setVisible(false);
        result.setHTML("");

    }

    public void initSumYears() {
        init();
        hp.setVisible(true);
        monthLabel.setVisible(true);
        monthTextBox.setVisible(true);
        reportButton.setVisible(true);
        yearLabel.setVisible(true);
        yearTextBox.setVisible(true);
        result.setHTML("");
        currentMode = Mode.SUM_YEARS;
    }

    public void initBelongings() {
        init();
        result.setHTML("");
        AuthResponder.get(constants, messages, this, "reports/belongings_responsible.php");
    }

    public void initMissingYearMembers() {
        init();
        hp.setVisible(true);
        reportButton.setVisible(true);
        yearLabel.setVisible(true);
        yearTextBox.setVisible(true);
        result.setHTML("");
        currentMode = Mode.MISSING_YEAR_MEMBERS;
    }

    public void initMissingSemesterMembers() {
        init();
        hp.setVisible(true);
        reportButton.setVisible(true);
        result.setHTML("");
        semesterBox.setVisible(true);
        semesterLabel.setVisible(true);
        fillSemesters();
        currentMode = Mode.MISSING_SEMESTER_MEMBERS;

    }

    private void fillSemesters() {
        semesterBox.clear();

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject obj = responseObj.isObject();

                JSONArray semesters = obj.get("semesters").isArray();

                for (int i = 0; i < semesters.size(); i++) {
                    JSONObject semester = semesters.get(i).isObject();
                    semesterBox.addItem(Util.str(semester.get("description")),Util.str(semester.get("semester")));
                }
                Util.setIndexByValue(semesterBox, Util.str(obj.get("current")));
            }
        };

        AuthResponder.get(constants, messages, callback, "registers/semesters.php?action=allWithActive");

    }

    public static GeneralReportView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new GeneralReportView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    @Override
    public void onClick(ClickEvent event) {

        switch(currentMode) {
        case MISSING_YEAR_MEMBERS:
        case SUM_YEARS:
            MasterValidator mv = new MasterValidator();
            mv.mandatory(messages.year_required(), yearTextBox);
            
            if (!mv.validateStatus()) {
                return;
            }
            break;
        case MISSING_SEMESTER_MEMBERS:
            break;
        }
    

        StringBuffer sb = new StringBuffer();
        sb.append("year=");
        sb.append(yearTextBox.getText());
        Util.addPostParam(sb, "semester", Util.getSelected(semesterBox));

        Util.addPostParam(sb, "month", monthTextBox.getText());
        AuthResponder.post(constants, messages, this, sb, getUrl());

    }

    private String getUrl() {
        switch (currentMode) {
        case MISSING_SEMESTER_MEMBERS:
            return "reports/missing_memberships.php?action=semester";
        case MISSING_YEAR_MEMBERS:
            return "reports/missing_memberships.php?action=year";
        case SUM_YEARS:
            return "reports/sum_years.php";
        default:
            return "???";
        }
    }

    @Override
    public void serverResponse(String response) {
        result.setHTML(response);
    }

    @Override
    public void serverResponse(JSONValue resonseObj) {
        /* Not used */
    }

}
