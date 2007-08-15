package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
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

public class ReportUsersEmail extends Composite {
    private static ReportUsersEmail reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private final HelpPanel helpPanel;
    private FlexTable table;

    public static ReportUsersEmail getInstance(Constants constants,
            I18NAccount messages, HelpPanel helpPanel) {
        if (reportInstance == null) {
            reportInstance = new ReportUsersEmail(constants, messages,
                    helpPanel);
        }
        return reportInstance;
    }

    public ReportUsersEmail(Constants constants, I18NAccount messages,
            HelpPanel helpPanel) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.title_report_users_email());
        table.getFlexCellFormatter().setColSpan(0, 0, 9);
        table.getRowFormatter().setStyleName(0, "header");
        table.setHTML(1, 0, messages.firstname());
        table.setHTML(1, 1, messages.lastname());
        table.setHTML(1, 2, messages.email());
        table.setHTML(1, 3, messages.newsletter());
        table.getRowFormatter().setStyleName(1, "header");

        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "reports/email.php?action=list&query=all");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue parse = JSONParser.parse(responseText);
                JSONArray array = parse.isArray();

                int row = table.getRowCount();

                for (int i = 0; i < array.size(); i++) {
                    JSONValue persVal = array.get(i);
                    JSONObject object = persVal.isObject();
                    setData(row++, object);
                }

                helpPanel.resize(reportInstance);
            }

            private void setData(int row, JSONObject object) {
                table.setHTML(row, 0, Util.str(object.get("firstname")));
                table.setHTML(row, 1, Util.str(object.get("lastname")));
                table.setHTML(row, 2, Util.str(object.get("email")));
                table.setHTML(row, 3, "1".equals(Util.str(object
                        .get("newsletter"))) ? messages.x() : "");
                table.getCellFormatter().setStyleName(row, 3, "center");

                String style = (row % 2 == 0) ? "showlineposts2"
                        : "showlineposts1";
                table.getRowFormatter().setStyleName(row, style);
            }
        };

        try {
            builder.sendRequest("", new AuthResponder(constants, messages,
                    callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }
}
