package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class ReportUsersEmail extends Composite {
    private static ReportUsersEmail reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private final HelpPanel helpPanel;
    private FlexTable table;
    private final Elements elements;

    public static ReportUsersEmail getInstance(Constants constants, I18NAccount messages,
            HelpPanel helpPanel, Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportUsersEmail(constants, messages, helpPanel, elements);
        }
        return reportInstance;
    }

    public ReportUsersEmail(Constants constants, I18NAccount messages, HelpPanel helpPanel,
            Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.title_report_users_email());
        table.getFlexCellFormatter().setColSpan(0, 0, 9);
        table.getRowFormatter().setStyleName(0, "header");
        table.setHTML(1, 0, elements.firstname());
        table.setHTML(1, 1, elements.lastname());
        table.setHTML(1, 2, elements.email());
        table.setHTML(1, 3, elements.newsletter());
        table.getRowFormatter().setStyleName(1, "header");

        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue parse) {
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
                table.setHTML(row, 3, "1".equals(Util.str(object.get("newsletter"))) ? elements.x()
                        : "");
                table.getCellFormatter().setStyleName(row, 3, "center");

                String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                table.getRowFormatter().setStyleName(row, style);
            }
        };

        AuthResponder.get(constants, messages, callback, "reports/email.php?action=list&query=all");

    }
}
