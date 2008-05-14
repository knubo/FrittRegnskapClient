package no.knubo.accounting.client.views.modules;


import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CountCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponseWithErrorFeedback;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FlexTable;

public class CountFields {

    private FlexTable table;

    private final Constants constants;

    private final I18NAccount messages;

    public FlexTable getTable() {
        return table;
    }

    public CountFields(Constants constants, I18NAccount messages, Elements elements) {

        this.constants = constants;
        this.messages = messages;
        table = new FlexTable();
        table.setStyleName("tableborder");

        table.setHTML(0, 0, elements.count_header());
        table.setText(0, 1, "");
        table.setText(0, 2, "");
        table.setText(0, 3, "");
        table.setHTML(1, 0, elements.value());
        table.setHTML(1, 1, elements.count());
        table.setText(1, 2, "");
        table.setText(1, 3, "");

        table.getRowFormatter().setStyleName(0, "header");
        table.getRowFormatter().setStyleName(1, "header");
    }

    public void init(String line) {
        table.setVisible(true);
        final CountCache countCache = CountCache.getInstance(constants, messages);

        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        StringBuffer sb = new StringBuffer();

        sb.append("action=get");
        Util.addPostParam(sb, "line", line);

        ServerResponseWithErrorFeedback callback = new ServerResponseWithErrorFeedback() {

            public void serverResponse(JSONValue value) {

                JSONObject object = value.isObject();

                if (object == null) {
                    table.setVisible(false);
                    return;
                }
                int row = 2;

                for (String count: countCache.getCounts()) {
                    String field = countCache.getFieldForCount(count);

                    table.setText(row, 0, count);
                    table.getCellFormatter().setStyleName(row, 0, "right");

                    if (object.containsKey(field)) {
                        String val = Util.strSkipNull(object.get(field));

                        if (!val.equals("")) {
                            table.setText(row, 1, "x " + val);
                            table.setText(row, 2, "=");
                            table.setText(row, 3, String.valueOf(Double.parseDouble(val)
                                    * Double.parseDouble(count)));
                            table.getCellFormatter().setStyleName(row, 3, "right");
                        } else {
                            table.setText(row, 1, "");
                            table.setText(row, 2, "");
                            table.setText(row, 3, "");
                        }
                    }
                    table.getRowFormatter().setStyleName(row,
                            (row % 2 == 0) ? "showlineposts2" : "showlineposts1");

                    row++;
                }
            }

            public void onError() {
                table.setVisible(false);
            }
        };

        AuthResponder.post(constants, messages, callback, sb, "accounting/countget.php");
    }
}
