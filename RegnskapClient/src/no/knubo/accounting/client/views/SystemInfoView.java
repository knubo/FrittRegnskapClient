package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class SystemInfoView extends Composite {

    private static SystemInfoView instance;

    private final Constants constants;

    private final I18NAccount messages;

    private HTML html;

    public static SystemInfoView getInstance(Constants constants, I18NAccount messages) {
        if (instance == null) {
            instance = new SystemInfoView(constants, messages);
        }
        
        instance.getServerInfo();

        return instance;
    }

    private void getServerInfo() {

        ServerResponse callback =new ServerResponseString() {

            @Override
            public void serverResponse(String response) {
                html.setHTML(response);
            }

            @Override
            public void serverResponse(JSONValue responseObj) {
                /* Unused */
            }
            
        };
        AuthResponder.get(constants, messages, callback , "defaults/systeminfo.php");
    }

    private SystemInfoView(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        html = new HTML();
        initWidget(html);
    }
}
