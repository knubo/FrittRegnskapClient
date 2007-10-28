package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;

public class AboutView extends Composite {

    /** This must match Version.php's version */
    public static final String CLIENT_VERSION = "1.2";

    private static AboutView instance;

    private final Constants constants;

    private final I18NAccount messages;

    public static AboutView getInstance(Constants constants,
            I18NAccount messages) {
        if (instance == null) {
            instance = new AboutView(constants, messages);
        }

        instance.checkServerVersion();
        return instance;
    }

    private void checkServerVersion() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "defaults/about.php?");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue value = JSONParser.parse(responseText);

                JSONObject object = value.isObject();

                String serverVersion = Util.str(object.get("serverversion"));

                if (!(CLIENT_VERSION.equals(serverVersion))) {
                    Window.alert(messages.version_mismatch(CLIENT_VERSION,
                            serverVersion));
                }

            }
        };
        try {
            builder.sendRequest("", new AuthResponder(constants, messages,
                    callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    private AboutView(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;
        Frame frame = new Frame("about.html");
        frame.setSize("600", "600");
        initWidget(frame);
    }
}
