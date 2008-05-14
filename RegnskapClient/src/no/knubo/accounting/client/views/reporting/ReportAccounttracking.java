package no.knubo.accounting.client.views.reporting;


import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class ReportAccounttracking extends Composite {
    private static ReportAccounttracking reportInstance;

    public static ReportAccounttracking getInstance(Constants constants, I18NAccount messages,
            Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportAccounttracking(constants, messages, elements);
        }
        reportInstance.init();
        return reportInstance;
    }

    private Constants constants;
    private I18NAccount messages;
    private FlexTable table;

    private ReportAccounttracking(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.menuitem_report_accounttrack());
        table.getFlexCellFormatter().setColSpan(0, 0, 3);
        table.getRowFormatter().setStyleName(0, "header");
        table.setText(1, 0, elements.account());
        table.setText(1, 1, elements.description());
        table.setText(1, 2, elements.sum());
        table.getRowFormatter().setStyleName(1, "header");

        DockPanel dp = new DockPanel();
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    private void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONObject posts = value.isObject();

                int row = table.getRowCount();

                PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);

                for (String key : posts.keySet()) {
                    table.setText(row, 0, key);
                    table.setText(row, 1, posttypeCache.getDescription(key));
                    table.setText(row, 2, Util.str(posts.get(key)));

                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);
                    row++;
                }

                // helpPanel.resize(reportInstance);

            }

        };

        AuthResponder.get(constants, messages, callback, "reports/accounttrackstatus.php");

    }

}
