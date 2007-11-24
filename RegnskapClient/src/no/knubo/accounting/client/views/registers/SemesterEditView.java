package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

public class SemesterEditView extends Composite {
    private static SemesterEditView me;
    private I18NAccount messages;
    private Constants constants;
    private FlexTable table;

    public SemesterEditView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.menuitem_semesters());
        table.setText(1, 0, elements.year());

        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public static SemesterEditView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new SemesterEditView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void init() {
        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONArray arr = value.isArray();

                arr.toString();
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/semester.php?action=all");
    }

}
