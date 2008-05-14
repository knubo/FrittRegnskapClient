package no.knubo.accounting.client.views.reporting;

import java.util.ArrayList;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class ReportMassLetters extends Composite implements ClickListener {
    private static ReportMassLetters reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private FlexTable table;
    private ArrayList<RadioButton> radiobuttons;
    private final Elements elements;

    public static ReportMassLetters getInstance(Constants constants, I18NAccount messages,
            Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportMassLetters(constants, messages, elements);
        }
        return reportInstance;
    }

    public ReportMassLetters(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;

        table = new FlexTable();
        table.setStyleName("edittable");

        table.setText(0, 0, elements.letter_template());
        table.getRowFormatter().setStyleName(0, "header");
        radiobuttons = new ArrayList<RadioButton>();

        DockPanel dp = new DockPanel();
        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        radiobuttons.clear();

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue parse) {

                JSONArray array = parse.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONValue value = array.get(i);

                    RadioButton rb = new RadioButton("letters");
                    rb.setText(Util.str(value));
                    rb.setStyleName("desc");
                    radiobuttons.add(rb);
                    table.setWidget(1 + i, 0, rb);
                }
                int row = table.getRowCount();
                NamedButton button = new NamedButton("join_letters", elements.join_letters());
                button.addClickListener(reportInstance);
                table.setWidget(row, 0, button);
            }
        };

        AuthResponder.get(constants, messages, callback, "reports/massletter.php?action=list");

    }

    public void onClick(Widget sender) {
        for (RadioButton rb : radiobuttons) {

            if (rb.isChecked()) {
                doLetter(rb.getText());
            }
        }
    }

    private void doLetter(String template) {
        Window.open(this.constants.baseurl() + "reports/massletter.php?action=pdf&template="
                + template, "_blank", "");
    }
}
