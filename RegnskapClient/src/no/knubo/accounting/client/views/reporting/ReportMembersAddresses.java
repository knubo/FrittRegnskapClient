package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class ReportMembersAddresses extends Composite implements ClickHandler {
    private static ReportMembersAddresses reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private final HelpPanel helpPanel;
    private FlexTable table;
    private final Elements elements;
    private NamedButton changeYearBox;
    private NamedButton exportButton;
    private String year = "0";
    private TextBoxWithErrorText yearBox;

    public static ReportMembersAddresses getInstance(Constants constants, I18NAccount messages, HelpPanel helpPanel,
            Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportMembersAddresses(constants, messages, helpPanel, elements);
        }
        return reportInstance;
    }

    public ReportMembersAddresses(Constants constants, I18NAccount messages, HelpPanel helpPanel, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        exportButton = new NamedButton("export_spreadsheet", elements.export_spreadsheet());

        HorizontalPanel hp = new HorizontalPanel();
        hp.add(exportButton);

        hp.add(new Label(elements.year()));
        yearBox = new TextBoxWithErrorText("year");
        hp.add(yearBox);
        changeYearBox = new NamedButton("change_year", elements.change_year());
        changeYearBox.addClickHandler(this);
        hp.add(changeYearBox);

        dp.add(hp, DockPanel.NORTH);
        exportButton.addClickHandler(this);
        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.title_report_membersaddresses());
        table.getFlexCellFormatter().setColSpan(0, 0, 11);
        table.getRowFormatter().setStyleName(0, "header");
        table.setHTML(1, 0, elements.firstname());
        table.setHTML(1, 1, elements.lastname());
        table.setHTML(1, 2, elements.address());
        table.setHTML(1, 3, elements.postnmb());
        table.setHTML(1, 4, elements.city());
        table.setHTML(1, 5, elements.email());
        table.setHTML(1, 6, elements.birthdate());
        table.setHTML(1, 7, elements.cellphone());
        table.setHTML(1, 8, elements.phone());
        table.setHTML(1, 9, elements.gender());
        table.setHTML(1, 10, elements.member_number());
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
            public void serverResponse(JSONValue value) {
                JSONArray array = value.isArray();

                int row = table.getRowCount();
                int badRow = row;
                table.setHTML(badRow, 0, elements.missing_address());
                table.getRowFormatter().setStyleName(badRow, "header");
                table.getFlexCellFormatter().setColSpan(badRow, 0, 11);
                badRow++;

                boolean yearSet = false;

                for (int i = 0; i < array.size(); i++) {
                    JSONValue persVal = array.get(i);
                    JSONObject object = persVal.isObject();

                    if (!yearSet) {
                        yearSet = true;
                        table.setHTML(0, 0, elements.title_report_membersaddresses() + " "
                                + Util.str(object.get("year")));
                    }

                    if (Util.isNull(object.get("address")) || Util.str(object.get("address")).length() == 0) {
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
                table.setHTML(row, 9, Util.strSkipNull(object.get("gender")));
                table.setHTML(row, 10, Util.strSkipNull(object.get("id")));

                String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                table.getRowFormatter().setStyleName(row, style);
            }

        };

        AuthResponder.get(constants, messages, callback, "reports/membership_addresses.php?year=" + year);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (!valiateYear()) {
            return;
        }
        year = yearBox.getText();

        if (event.getSource() == exportButton) {
            Window.open(this.constants.baseurl() + "reports/membership_addresses.php?action=spreadsheet", "_blank", "");
        }
        if (event.getSource() == changeYearBox) {
            init();
        }
    }

    boolean valiateYear() {
        if(yearBox.getText().isEmpty()) {
            return true;
        }
        MasterValidator mv = new MasterValidator();
        mv.year(messages.illegal_year(), yearBox);
        
        return mv.validateStatus();
    }
}
