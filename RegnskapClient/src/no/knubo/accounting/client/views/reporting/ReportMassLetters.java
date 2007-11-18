package no.knubo.accounting.client.views.reporting;

import java.util.ArrayList;
import java.util.Iterator;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
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
    private ArrayList radiobuttons;
    private final Elements elements;

    public static ReportMassLetters getInstance(Constants constants,
            I18NAccount messages, Elements elements) {
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
        radiobuttons = new ArrayList();

        DockPanel dp = new DockPanel();
        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        radiobuttons.clear();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "reports/massletter.php?action=list");

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
                NamedButton button = new NamedButton("join_letters", elements
                        .join_letters());
                button.addClickListener(reportInstance);
                table.setWidget(row, 0, button);
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
        for (Iterator i = radiobuttons.iterator(); i.hasNext();) {
            RadioButton rb = (RadioButton) i.next();

            if (rb.isChecked()) {
                doLetter(rb.getText());
            }
        }
    }

    private void doLetter(String template) {
        Window.open(this.constants.baseurl()
                + "reports/massletter.php?action=pdf&template=" + template,
                "_blank", "");
    }
}
