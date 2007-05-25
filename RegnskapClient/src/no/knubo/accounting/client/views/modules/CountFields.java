package no.knubo.accounting.client.views.modules;

import java.util.Iterator;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CountCache;

public class CountFields {

    private FlexTable table;

    private final Constants constants;

    public FlexTable getTable() {
        return table;
    }

    public CountFields(Constants constants, I18NAccount messages) {

        this.constants = constants;
        table = new FlexTable();
        table.setStyleName("tableborder");

        table.setHTML(0, 0, messages.count_header());
        table.setText(0, 1, "");
        table.setText(0, 2, "");
        table.setText(0, 3, "");
        table.setHTML(1, 0, messages.value());
        table.setHTML(1, 1, messages.count());
        table.setText(1, 2, "");
        table.setText(1, 3, "");

        table.getRowFormatter().setStyleName(0, "header");
        table.getRowFormatter().setStyleName(1, "header");
    }

    public void init(String line) {
        table.setVisible(true);
        final CountCache countCache = CountCache.getInstance(constants);

        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        StringBuffer sb = new StringBuffer();

        sb.append("action=get");
        Util.addPostParam(sb, "line", line);

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "accounting/countget.php");

        RequestCallback callback = new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                Window.alert(exception.getMessage());
            }

            public void onResponseReceived(Request request, Response response) {
                JSONValue value = JSONParser.parse(response.getText());

                if (value == null) {
                    table.setVisible(false);
                    return;
                }

                JSONObject object = value.isObject();

                if (object == null) {
                    table.setVisible(false);
                    return;
                }
                int row = 2;

                for (Iterator i = countCache.getCounts().iterator(); i
                        .hasNext();) {
                    String count = (String) i.next();
                    String field = countCache.getFieldForCount(count);

                    table.setText(row, 0, count);
                    table.getCellFormatter().setStyleName(row, 0, "right");

                    if (object.containsKey(field)) {
                        String val = Util.strSkipNull(object.get(field));
                        
                        if(!val.equals("")) {
                            table.setText(row, 1, "x "+val);
                            table.setText(row, 2, "=");
                            table.setText(row, 3, String.valueOf(Double.parseDouble(val) * Double.parseDouble(count)));
                            table.getCellFormatter().setStyleName(row, 3, "right");
                        } else {
                            table.setText(row, 1, "");
                            table.setText(row, 2, "");
                            table.setText(row, 3, "");
                        }
                    }
                    table.getRowFormatter().setStyleName(
                            row,
                            (row % 2 == 0) ? "showlineposts2"
                                    : "showlineposts1");

                    row++;
                }
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(sb.toString(), callback);
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }
}
