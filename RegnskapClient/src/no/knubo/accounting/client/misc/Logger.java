package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Util;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class Logger {
    
    
    private final Constants constants;

    public Logger(Constants constants) {
        this.constants = constants;
    }

     void log(String category, String action, String message) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, constants.baseurl()
                + "logging.php");

        StringBuffer sb = new StringBuffer();
        sb.append("action=log");
        Util.addPostParam(sb, "category", category);
        Util.addPostParam(sb, "logaction", action);
        Util.addPostParam(sb, "message", message);

        try {
            builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
            builder.sendRequest(sb.toString(), nullCallback());
        } catch (RequestException e) {
            /* Silent */
        }

    }

    private static RequestCallback nullCallback() {
        return new RequestCallback() {

            @Override
            public void onError(Request request, Throwable exception) {
                /* Silent */
            }

            @Override
            public void onResponseReceived(Request request, Response response) {
                /* Silent */
            }

        };
    }

    public void error(String action, String message) {
        log("error", action, message);
    }

    public void info(String action, String message) {
        log("info", action, message);
    }

}
