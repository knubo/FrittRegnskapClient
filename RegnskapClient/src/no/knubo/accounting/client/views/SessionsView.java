package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;

public class SessionsView extends Composite {

    private static SessionsView instance;

    private final Constants constants;

    private final I18NAccount messages;

    private AccountTable table;

    public static SessionsView getInstance(Constants constants, Elements elements, I18NAccount messages) {
        if (instance == null) {
            instance = new SessionsView(constants, messages, elements);
        }

        instance.getSessionInfo();

        return instance;
    }

    private void getSessionInfo() {
        while(table.getRowCount() > 2) {
            table.removeRow(2);
        }
        
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();
                
                for(int i=0; i < array.size(); i++) {
                    JSONObject session = array.get(i).isObject();
                    
                    table.setText(i+2, 0, Util.str(session.get("LastUpdate")));
                    table.setText(i+2, 1, Util.str(session.get("username")));
                    table.setText(i+2, 2, Util.str(session.get("ip")));
                    table.alternateStyle(i+1, 0);
                }
            }
        };
        AuthResponder.get(constants, messages, callback, "defaults/sessions.php");
    }

    private SessionsView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;

        table = new AccountTable("dotted tableborder");

        table.setHeaderRowStyle(1);
        table.setText(0, 0, elements.menuitem_sessioninfo());
        table.setColSpanAndRowStyle(0, 0, 3, "header");
        table.setText(1, 0, elements.date());
        table.setText(1, 1, elements.user());
        table.setText(1, 2, "IP");
        
        initWidget(table);
    }
}
