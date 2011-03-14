package no.knubo.accounting.client.views.ownings;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

public class OwningsListView extends Composite implements KeyUpHandler {

    private static OwningsListView instance;
    private Constants constants;
    private I18NAccount messages;
    private Elements elements;
    private AccountTable table;
    private TextBox belonging;
    private TextBox serial;
    private TextBox description;
    private CheckBox deletedCheckbox;
    private OwningsListView me;

    public static OwningsListView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new OwningsListView(constants, messages, elements);
        }
        return instance;
    }

    public OwningsListView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;

        table = new AccountTable("tableborder");

        table.setText(1, 1, elements.owning());
        table.setText(1, 2, elements.identifier());
        table.setText(1, 3, elements.description());
        table.setText(1, 4, elements.owning_buy_date());
        table.setText(1, 5, elements.owning_warrenty_date());
        table.setText(1, 6, elements.owning_purchase_price());
        table.setText(1, 7, elements.owning_year_to_deprecation());
        table.setText(1, 8, elements.owning_remaining(), "desc");
        table.setText(1, 9, elements.owning_month_deprecation(), "desc");
        table.setText(1, 11, elements.owning_account());
        table.setText(1, 11, elements.owning_deprecation());
        table.setText(1, 12, elements.deleted());
        table.setHeaderRowStyle(1);

        belonging = new TextBox();
        table.setWidget(0, 1, belonging, "smallinput");
        serial = new TextBox();
        table.setWidget(0, 2, serial, "smallinput");
        description = new TextBox();
        table.setWidget(0, 3, description, "smallinput");
        deletedCheckbox = new CheckBox();
        table.setWidget(0, 12, deletedCheckbox, "center");

        initWidget(table);
        me = this;

        addDelayedKeyUpHandler(belonging);
        addDelayedKeyUpHandler(serial);
        addDelayedKeyUpHandler(description);
        addDelayedCheck();
    }

    Timer timer;
    private int hash;

    private void addDelayedCheck() {
        ClickHandler handler = new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (timer != null) {
                    return;
                }
                timer = new Timer() {

                    @Override
                    public void run() {
                        me.onKeyUp(null);
                        timer = null;
                    }

                };
                timer.schedule(1000);
            }
        };
        deletedCheckbox.addClickHandler(handler);
    }

    /**
     * The event returned is always null. It is called after 1 second of
     * waiting.
     * 
     * @param handler
     */
    public void addDelayedKeyUpHandler(final TextBox box) {

        KeyUpHandler delayedHandler = new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (timer != null) {
                    return;
                }
                timer = new Timer() {

                    @Override
                    public void run() {
                        me.onKeyUp(null);
                        timer = null;
                    }

                };
                timer.schedule(1000);
            }
        };
        box.addKeyUpHandler(delayedHandler);
    }

    public void init() {
        hash = -1;
        filter();
        belonging.setFocus(true);
    }

    private void filter() {
        String serialText = serial.getText();
        String belongingText = belonging.getText();
        String descText = description.getText();
        boolean delValue = deletedCheckbox.getValue();

        int newHash = serialText.hashCode() + belongingText.hashCode() + descText.hashCode() + (delValue ? 1 : 0);

        if (hash == newHash) {
            return;
        }
        hash = newHash;

        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                fillResponse(responseObj.isArray());
            }
        };
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=list");
        Util.addPostParam(parameters, "serial", serialText);
        Util.addPostParam(parameters, "belonging", belongingText);
        Util.addPostParam(parameters, "description", descText);
        Util.addPostParam(parameters, "deleted", delValue ? "1" : "0");

        AuthResponder.post(constants, messages, callback, parameters, "accounting/belongings.php");
    }

    protected void fillResponse(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.get(i).isObject();

            table.setWidget(i + 2, 0, ImageFactory.editImage("id" + Util.str(object.get("id"))));
            table.setText(i + 2, 1, Util.str(object.get("belonging")));
            table.setText(i + 2, 2, Util.str(object.get("serial")));

            String desc = Util.strSkipNull(object.get("description"));
            if (desc.length() > 15) {
                desc = desc.substring(0, 15) + "...";
            }
            table.setText(i + 2, 3, desc);
            table.setText(i + 2, 4, Util.strSkipNull(object.get("purchase_date")), "right");
            table.setText(i + 2, 5, Util.strSkipNull(object.get("warrenty_date")), "right");
            table.setText(i + 2, 6, Util.money(Util.strSkipNull(object.get("purchase_price"))), "right");
            table.setText(i + 2, 7, Util.strSkipNull(object.get("year_deprecation")), "right");
            table.setText(i + 2, 8, Util.money(Util.strSkipNull(object.get("current_price"))), "right");
            table.setText(i + 2, 9, Util.money(Util.strSkipNull(object.get("deprecation_amount"))), "right");
            table.setText(i + 2, 10, Util.strSkipNull(object.get("owning_account")), "right");
            table.setText(i + 2, 11, Util.strSkipNull(object.get("deprecation_account")), "right");

            String deletedText = Util.strSkipNull(object.get("deleted")).equals("0") ? elements.admin_yes() : elements
                    .admin_no();
            table.setText(i + 2, 12, deletedText, "center");

            table.alternateStyle(i + 2, (i % 6) > 2);
        }
    }

    public void onKeyUp(KeyUpEvent event) {
        filter();
    }
}
