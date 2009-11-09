package no.knubo.accounting.client.views.reporting;

import java.util.Set;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ReportMembersBirthGender extends Composite implements ClickHandler {
    private static ReportMembersBirthGender reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private final HelpPanel helpPanel;
    private FlexTable table;
    private Elements elements;
    private TextBox yearBox;

    public static ReportMembersBirthGender getInstance(Constants constants, I18NAccount messages,
            HelpPanel helpPanel, Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportMembersBirthGender(constants, messages, helpPanel, elements);
        }
        return reportInstance;
    }

    public ReportMembersBirthGender(Constants constants, I18NAccount messages, HelpPanel helpPanel,
            Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();

        HorizontalPanel hp = new HorizontalPanel();
        yearBox = new TextBox();
        yearBox.setText(""+Util.currentYear());
        Button yearButton = new Button(elements.do_report());
        yearButton.addClickHandler(this);
        hp.add(yearBox);
        hp.add(yearButton);
        
        dp.add(hp, DockPanel.NORTH);

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.title_report_membersbirth_gender());
        table.getFlexCellFormatter().setColSpan(0, 0, 5);
        table.getRowFormatter().setStyleName(0, "header");
        table.setHTML(1, 0, elements.age());
        table.setHTML(1, 1, elements.gender_females());
        table.setHTML(1, 2, elements.gender_males());
        table.setHTML(1, 3, elements.missing());
        table.setHTML(1, 4, elements.sum());
        table.getRowFormatter().setStyleName(1, "header");

        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }
        setDefaultTable();

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                Set<String> keys = object.keySet();

                for (String ageGenderKey : keys) {
                    char gender = ageGenderKey.charAt(0);
                    int age = -1;
                    if (ageGenderKey.length() > 1) {
                        age = Integer.parseInt(ageGenderKey.substring(1));
                        if (age > 61) {
                            age = 61;
                        }
                    }
                    addEntry(gender, age, object.get(ageGenderKey));
                    calcSums();
                }

                helpPanel.resize(reportInstance);
            }

        };

        table.setHTML(0, 0, elements.title_report_membersbirth_gender()+ " "+yearBox.getText());
        AuthResponder.get(constants, messages, callback, "reports/membership_birth_gender.php?year="+yearBox.getText());

    }

    protected void calcSums() {
        int colSum[] = new int[4];
        int totalsum = 0;
        for (int row = 2; row < 12; row++) {
            int rowsum = 0;
            for (int col = 1; col < 4; col++) {
                String value = table.getText(row, col);

                if (value.length() == 0) {
                    continue;
                }

                int c = Integer.parseInt(value);
                colSum[col] += c;
                rowsum += c;
                totalsum += c;
            }
            if (rowsum > 0) {
                table.setText(row, 4, String.valueOf(rowsum));
                table.getCellFormatter().addStyleName(row, 4, "right");
            }
        }

        for (int col = 1; col < 4; col++) {
            if (colSum[col] > 0) {
                table.setText(12, col, String.valueOf(colSum[col]));
                table.getCellFormatter().addStyleName(12, col, "showlineposts1 tablecells right");
            }
        }
        if (totalsum > 0) {
            table.setText(12, 4, String.valueOf(totalsum));
        }
        table.getCellFormatter().addStyleName(12, 4, "showlineposts1 tablecells right");

    }

    private void setDefaultTable() {
        table.setText(2, 0, "0-5");
        table.setText(3, 0, "6-12");
        table.setText(4, 0, "13-19");
        table.setText(5, 0, "20-25");
        table.setText(6, 0, "26-30");
        table.setText(7, 0, "31-40");
        table.setText(8, 0, "41-50");
        table.setText(9, 0, "51-60");
        table.setText(10, 0, "61+");
        
        table.setText(11, 0, elements.error());
        table.setText(12, 0, elements.sum());
        table.getCellFormatter().setStyleName(12, 0, "header");

        for (int row = 2; row < 12; row++) {
            table.setText(row, 1, "");
            table.setText(row, 2, "");
            table.setText(row, 3, "");

            String style = (row % 2 == 0) ? "showlineposts2 tablecells right"
                    : "showlineposts1 tablecells right";
            table.getCellFormatter().setStyleName(row, 1, style);
            table.getCellFormatter().setStyleName(row, 2, style);
            table.getCellFormatter().setStyleName(row, 3, style);
            table.getCellFormatter().setStyleName(row, 4, style);
            table.getCellFormatter().setStyleName(row, 0, "header");
        }
    }

    protected void addEntry(char gender, int age, JSONValue value) {
        int col = gender == '?' ? 3 : (gender == 'M' ? 2 : 1);
        int row = findRow(age);
        int count = Util.getInt(value);

        String existing = table.getText(row, col);
        String newValue;
        if (existing.length() == 0) {
            newValue = String.valueOf(count);
        } else {
            newValue = String.valueOf((Integer.parseInt(existing) + count));
        }
        table.setText(row, col, newValue);
    }

    private int findRow(int age) {
        if (age == -1) {
            return 11;
        }
        if (age <= 5) {
            return 2;
        }
        if (age <= 12) {
            return 3;
        }
        if (age <= 19) {
            return 4;
        }
        if (age <= 25) {
            return 5;
        }
        if (age <= 30) {
            return 6;
        }
        if (age <= 40) {
            return 7;
        }
        if (age <= 50) {
            return 8;
        }
        if (age <= 60) {
            return 9;
        }
        if (age > 60) {
            return 10;
        }
        return 0;
    }

    public void onClick(ClickEvent event) {
        init();
    }
}
