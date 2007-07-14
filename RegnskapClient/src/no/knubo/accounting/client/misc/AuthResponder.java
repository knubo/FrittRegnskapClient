package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

public class AuthResponder implements RequestCallback {

    private final Constants constants;
    private final ServerResponse callback;

    public AuthResponder(Constants constants, ServerResponse callback) {
        this.constants = constants;
        this.callback = callback;

    }

    public void onError(Request request, Throwable exception) {
        Window.alert("Error: "+request+" "+exception);
    }

    public void onResponseReceived(Request request, Response response) {
        if (response.getStatusCode() == 510) {
            Util.forward(constants.loginURL());
        } else {
            callback.serverResponse(response.getText());
        }
    }

}
