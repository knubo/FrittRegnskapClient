package no.knubo.accounting.client.views.kid;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class RegisterMembershipKIDView extends Composite {

    private static RegisterMembershipKIDView me;
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;
    private AccountTable table;

    public static RegisterMembershipKIDView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new RegisterMembershipKIDView(messages, constants, elements);
        }
        return me;
    }

    public RegisterMembershipKIDView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        table = new AccountTable("tableborder");
        table.setText(0, 0, elements.kid_incoming_transactions());
        table.setColSpanAndRowStyle(0, 0, 8, "header");

        table.setText(1, 0, "KID");
        table.setText(1, 1, elements.kid_settlement_date());
        table.setText(1, 2, elements.name());
        table.setText(1, 3, elements.amount());

        table.setText(1, 4, elements.kid_payments());
        table.setText(1, 5, elements.status());
        table.setHeaderRowStyle(1);

        initWidget(table);
    }

    private PriceMatcher priceMatcher;

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONObject data = responseObj.isObject();

                JSONValue p = data.get("price");

                if (p == null) {
                    Window.alert(messages.dashboard_missing_semester_price_current());
                    return;
                }

                priceMatcher = new PriceMatcher(p.isObject());

                JSONArray array = data.get("data").isArray();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = array.get(i).isObject();

                    add(object);
                }

            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/register_by_kid.php?action=unhandled");
    }

    protected void add(JSONObject object) {
        int row = table.getRowCount();

        table.setText(row, 0, Util.str(object.get("kid")), "desc");
        table.setText(row, 1, Util.formatDate(object.get("settlement_date")), "desc");
        table.setText(row, 2, Util.strSkipNull(object.get("firstname")) + " "
                + Util.strSkipNull(object.get("lastname")), "desc");
        table.setText(row, 3, Util.money(object.get("amount")), "right");

        String[] match = priceMatcher.matchPrices(Util.getDouble(object.get("amount")), !Util.isNull(object
                .get("memberid")));

        if (match.length == 0) {
            table.setText(row, 4, messages.kid_bad_payment(), "desc");
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < match.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(elements.getString(match[i] + "_membership"));
            }
            table.setText(row, 4, sb.toString(), "desc");
        }
    }
}
