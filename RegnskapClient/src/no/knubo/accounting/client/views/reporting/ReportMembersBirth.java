package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class ReportMembersBirth extends Composite {
    private static ReportMembersBirth reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private final HelpPanel helpPanel;
    private FlexTable table;

    public static ReportMembersBirth getInstance(Constants constants,
            I18NAccount messages, HelpPanel helpPanel) {
        if (reportInstance == null) {
            reportInstance = new ReportMembersBirth(constants, messages,
                    helpPanel);
        }
        return reportInstance;
    }

    public ReportMembersBirth(Constants constants, I18NAccount messages,
            HelpPanel helpPanel) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.title_report_membersbirth());
        table.getFlexCellFormatter().setColSpan(0, 0, 4);
        table.getRowFormatter().setStyleName(0, "header");
        table.setHTML(1, 0, messages.lastname());
        table.setHTML(1, 1, messages.firstname());
        table.setHTML(1, 2, messages.birthdate());
        table.setHTML(1, 3, messages.age());
        table.getRowFormatter().setStyleName(1, "header");

        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ResponseTextHandler getValues = new ResponseTextHandler() {

            public void onCompletion(String responseText) {
                JSONValue value = JSONParser.parse(responseText);
                JSONObject object = value.isObject();

                JSONArray a19 = object.get("year_19").isArray();
                JSONArray a25 = object.get("year_25").isArray();
                JSONArray a30 = object.get("year_30").isArray();
                JSONArray a40 = object.get("year_40").isArray();
                JSONArray aabov = object.get("year_above").isArray();
                JSONArray auns = object.get("year_unset").isArray();
                JSONArray awrong = object.get("year_wrong").isArray();

                showCategory(messages.report_year_19(""+a19.size()), a19);
                showCategory(messages.report_year_25(""+a25.size()), a25);
                showCategory(messages.report_year_30(""+a30.size()), a30);
                showCategory(messages.report_year_40(""+a40.size()), a40);
                showCategory(messages.report_year_above(""+aabov.size()), aabov);
                showCategory(messages.report_year_unset(""+auns.size()), auns);
                showCategory(messages.report_year_wrong(""+awrong.size()), awrong);

                helpPanel.resize(reportInstance);
            }

            private void showCategory(String header, JSONArray array) {
                int row = table.getRowCount();
                table.setHTML(row, 0, header);
                table.getFlexCellFormatter().setColSpan(row, 0, 4);
                table.getRowFormatter().setStyleName(row, "header");
                
                row++;
                for (int i = 0; i < array.size(); i++) {
                    JSONValue person = array.get(i);
                    JSONObject personObj = person.isObject();

                    table.setHTML(row, 0, Util.str(personObj.get("lastname")));
                    table.setHTML(row, 1, Util.str(personObj.get("firstname")));
                    table.setHTML(row, 2, Util.str(personObj.get("birthdate")));
                    table.setHTML(row, 3, Util.str(personObj.get("age")));
                    table.getCellFormatter().setStyleName(row, 3, "center");
                    String style = (row % 2 == 0) ? "showlineposts2"
                            : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);
                    row++;
                }
            }
        };
        // TODO Report stuff as being loaded.
        if (!HTTPRequest.asyncGet(this.constants.baseurl()
                + "reports/membership_birth.php", getValues)) {
            Window.alert("Failed to load data.");
        }

    }
}
