package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class ReportMembersAddresses extends Composite implements ClickListener {
    private static ReportMembersAddresses reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private final HelpPanel helpPanel;
    private FlexTable table;

    public static ReportMembersAddresses getInstance(Constants constants,
            I18NAccount messages, HelpPanel helpPanel) {
        if (reportInstance == null) {
            reportInstance = new ReportMembersAddresses(constants, messages,
                    helpPanel);
        }
        return reportInstance;
    }

    public ReportMembersAddresses(Constants constants, I18NAccount messages,
            HelpPanel helpPanel) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();

        NamedButton exportButton = new NamedButton("export_spreadsheet", messages.export_spreadsheet());
        dp.add(exportButton, DockPanel.NORTH);
        exportButton.addClickListener(this);
        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.title_report_membersaddresses());
        table.getFlexCellFormatter().setColSpan(0, 0, 9);
        table.getRowFormatter().setStyleName(0, "header");
        table.setHTML(1, 0, messages.firstname());
        table.setHTML(1, 1, messages.lastname());
        table.setHTML(1, 2, messages.address());
        table.setHTML(1, 3, messages.postnmb());
        table.setHTML(1, 4, messages.city());
        table.setHTML(1, 5, messages.email());
        table.setHTML(1, 6, messages.birthdate());
        table.setHTML(1, 7, messages.cellphone());
        table.setHTML(1, 8, messages.phone());
        table.getRowFormatter().setStyleName(1, "header");

        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "reports/membership_addresses.php");

        ServerResponse callback = new ServerResponse() {


            public void serverResponse(String responseText) {
                JSONValue value = JSONParser.parse(responseText);
                JSONArray array = value.isArray();

                int row = table.getRowCount();
                int badRow = row;
                table.setHTML(badRow, 0, messages.missing_address());
                table.getRowFormatter().setStyleName(badRow, "header");
                table.getFlexCellFormatter().setColSpan(badRow, 0, 9);
                badRow++;

                for (int i = 0; i < array.size(); i++) {
                    JSONValue persVal = array.get(i);
                    JSONObject object = persVal.isObject();

                    if (Util.isNull(object.get("address"))
                            || Util.str(object.get("address")).length() == 0) {
                        table.insertRow(badRow);
                        setData(badRow, object);
                        badRow++;
                    } else {
                        table.insertRow(row);
                        setData(row, object);
                        row++;
                    }

                }

                helpPanel.resize(reportInstance);
            }

            private void setData(int row, JSONObject object) {
                table.setHTML(row, 0, Util.str(object.get("firstname")));
                table.setHTML(row, 1, Util.str(object.get("lastname")));
                table.setHTML(row, 2, Util.strSkipNull(object.get("address")));
                table.setHTML(row, 3, Util.strSkipNull(object.get("postnmb")));
                table.setHTML(row, 4, Util.strSkipNull(object.get("city")));
                table.setHTML(row, 5, Util.strSkipNull(object.get("email")));
                table.setHTML(row, 6, Util.str(object.get("birthdate")));
                table.setHTML(row, 7, Util.strSkipNull(object.get("cellphone")));
                table.setHTML(row, 8, Util.strSkipNull(object.get("phone")));

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

    public void onClick(Widget sender) {
        Window.open(this.constants.baseurl()
                + "reports/membership_addresses.php?action=spreadsheet", "_blank", "");
    }
}
