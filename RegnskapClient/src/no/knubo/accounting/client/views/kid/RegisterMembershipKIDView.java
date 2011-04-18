package no.knubo.accounting.client.views.kid;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

public class RegisterMembershipKIDView extends Composite implements ClickHandler {

    private static RegisterMembershipKIDView me;
    final Elements elements;
    final I18NAccount messages;
    final Constants constants;
    private AccountTable table;

    public static RegisterMembershipKIDView show(I18NAccount messages, Constants constants, Elements elements,
            ViewCallback vc) {
        if (me == null) {
            me = new RegisterMembershipKIDView(messages, constants, elements, vc);
        }
        return me;
    }

    public RegisterMembershipKIDView(I18NAccount messages, Constants constants, Elements elements,
            ViewCallback viewcallback) {
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
        table.setText(1, 5, "");
        table.setText(1, 6, "");
        table.setHeaderRowStyle(1);
        this.viewcallback = viewcallback;
        initWidget(table);
    }

    private PriceMatcher priceMatcher;
    private JSONObject prices;
    private JSONArray kidData;
    private JSONObject posts;
    private ViewCallback viewcallback;
    RegisterStandards registerStandards;

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

                prices = p.isObject();
                priceMatcher = new PriceMatcher(prices);

                kidData = data.get("data").isArray();
                posts = data.get("posts").isObject();
                for (int i = 0; i < kidData.size(); i++) {
                    JSONObject object = kidData.get(i).isObject();

                    add(object);
                }

            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/register_by_kid.php?action=unhandled");

        registerStandards = new RegisterStandards(constants, messages, elements, viewcallback);
        registerStandards.fetchInitalData(false);
    }

    protected void add(JSONObject object) {
        int row = table.getRowCount();

        int id = Util.getInt(object.get("id"));
        table.setText(row, 0, Util.str(object.get("kid")), "desc");
        table.setText(row, 1, Util.formatDate(object.get("settlement_date")), "desc");
        table.setText(row, 2, Util.strSkipNull(object.get("firstname")) + " "
                + Util.strSkipNull(object.get("lastname")), "desc");
        table.setText(row, 3, Util.money(object.get("amount")), "right");

        String[] match = priceMatcher.matchPrices(Util.getDouble(object.get("amount")), !Util.isNull(object
                .get("memberid")), !Util.isNull(object.get("youth")) || !Util.isNull(object.get("train"))
                || !Util.isNull(object.get("course")));
        
        
        if (match.length == 0) {
            table.setText(row, 4, messages.kid_bad_payment(), "desc");
            table.setWidget(row, 5, ImageFactory.alertImage("fail" + id), "center");
        } else {
            object.put("payments", Util.toJsonArray(match));

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < match.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(elements.getString(match[i] + "_membership"));
            }
            table.setText(row, 4, sb.toString(), "desc");
            table.setWidget(row, 5, ImageFactory.okImage("ok" + id), "center");
        }

        Image editImage = ImageFactory.editImage("edit" + Util.str(object.get("id")));
        editImage.addClickHandler(this);
        table.setWidget(row, 6, editImage, "center");
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() instanceof Image) {
            Image image = (Image) event.getSource();

            String id = image.getElement().getId();

            editKID(id);
        }
    }

    private void editKID(String id) {
        for (int i = 0; i < kidData.size(); i++) {
            JSONObject kid = kidData.get(i).isObject();

            if (("edit" + Util.str(kid.get("id"))).equals(id)) {
                new EditKIDPopup(kid, prices, posts, this);
                return;
            }
        }
        Util.log("Did not find:" + id);
    }
}
