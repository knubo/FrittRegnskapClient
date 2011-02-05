package no.knubo.accounting.client.views.admin;

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

public class AdminStatsView extends Composite {
    private static AdminStatsView me;
    private AccountTable statsTable;
    private final I18NAccount messages;
    private final Constants constants;

    public static AdminStatsView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminStatsView(messages, constants, elements);
        }
        return me;
    }

    public AdminStatsView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;

        statsTable = new AccountTable("spred");
        statsTable.setText(0, 0, "", "headernobox desc");
        statsTable.setText(1, 0, elements.first_year(), "headernobox desc");
        statsTable.setText(2, 0, elements.year_count(), "headernobox desc");
        statsTable.setText(3, 0, elements.menuitem_useradm(), "headernobox desc");
        statsTable.setText(4, 0, elements.menu_people(), "headernobox desc");
        statsTable.setText(5, 0, elements.year_membership(), "headernobox desc");
        statsTable.setText(6, 0, elements.menuitem_membership_prices(), "headernobox desc");
        statsTable.setText(7, 0, elements.newsletter(), "headernobox desc");
        statsTable.setText(8, 0, elements.lines(), "headernobox desc");

        for (int i = 0; i <= 8; i++) {
            statsTable.alternateStyle(i, 0);
        }

        initWidget(statsTable);
    }

    public void init() {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject one = array.get(i).isObject();
                    JSONObject stats = one.get("data").isObject();

                    statsTable.setText(0, i + 1, Util.strSkipNull(one.get("hostprefix")), "center");
                    statsTable.setText(1, 1 + i, Util.strSkipNull(stats.get("first_year")), "right");
                    statsTable.setText(2, 1 + i, Util.strSkipNull(stats.get("year_count")), "right");
                    statsTable.setText(3, 1 + i, Util.strSkipNull(stats.get("user_count")), "right");
                    statsTable.setText(4, 1 + i, Util.strSkipNull(stats.get("person_count")), "right");
                    statsTable.setText(5, 1 + i, Util.strSkipNull(stats.get("member_count")), "right");
                    statsTable.setText(6, 1 + i, Util.strSkipNull(stats.get("max_year_cost")), "right");
                    statsTable.setText(7, 1 + i, Util.strSkipNull(stats.get("newsletter_count")), "right");
                    statsTable.setText(8, 1 + i, Util.strSkipNull(stats.get("line_count")), "right");
                }
            }
        };
        AuthResponder.get(constants, messages, callback, "admin/admin_stats.php");
    }

}
