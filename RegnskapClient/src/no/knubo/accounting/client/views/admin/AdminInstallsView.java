package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlexTable;

public class AdminInstallsView {
    private static AdminInstallsView me;
    private final I18NAccount messages;
    private final Constants constants;
    private final Elements elements;
    private FlexTable table;

    public static AdminInstallsView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminInstallsView(messages, constants, elements);
        }
        return me;
    }

    public AdminInstallsView(I18NAccount messages, Constants constants, Elements elements) {

        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.admin_installs());
        table.setHTML(1, 0, elements.admin_hostprefix());
        table.setHTML(1, 1, elements.admin_dbprefix());
        table.setHTML(1, 2, elements.admin_database());
        table.setHTML(1, 3, elements.admin_wikilogin());
        table.setHTML(1, 4, elements.admin_diskqvota());
        table.getRowFormatter().setStyleName(0, "header");
        table.getRowFormatter().setStyleName(1, "header");
    }

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                fillInstalls(responseObj.isArray());
            }
        };
        AuthResponder.get(constants, messages, callback, "admin/installs.php?action=list");

    }

    protected void fillInstalls(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();
            table.setText(i+2, 0, Util.str(obj.get("hostprefix")));
        }
    }
}
