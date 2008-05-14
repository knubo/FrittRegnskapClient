package no.knubo.accounting.client.views;


import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MonthEndView extends Composite implements ClickListener {

    private static MonthEndView me;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    private HTML dateHeader;

    private final ViewCallback callback;

    private final Elements elements;

    public static MonthEndView getInstance(Constants constants, I18NAccount messages,
            ViewCallback callback, Elements elements) {
        if (me == null) {
            me = new MonthEndView(constants, messages, callback, elements);
        }
        return me;
    }

    private MonthEndView(Constants constants, I18NAccount messages, ViewCallback callback,
            Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.callback = callback;
        this.elements = elements;

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.post());
        table.setHTML(0, 1, elements.amount());
        table.getRowFormatter().setStyleName(0, "header");

        dateHeader = new HTML();
        HTML header = new HTML(elements.end_month_explain());
        Button endButton = new NamedButton("MonthEndView.endButton", elements.end_month());
        endButton.addClickListener(this);

        DockPanel dp = new DockPanel();
        dp.add(dateHeader, DockPanel.NORTH);
        dp.add(header, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);
        dp.add(endButton, DockPanel.NORTH);

        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        ServerResponse rh = new ServerResponse() {
            public void serverResponse(JSONValue jsonValue) {
                PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);

                JSONObject root = jsonValue.isObject();

                String year = Util.str(root.get("year"));
                int month = Util.getInt(root.get("month"));

                dateHeader.setHTML("<h2>" + Util.monthString(elements, month) + " " + year
                        + "</h2>");

                JSONValue postsValue = root.get("posts");
                JSONObject object = postsValue.isObject();

                int row = 1;
                for (String post : object.keySet()) {

                    table.setHTML(row, 0, post + " -  " + posttypeCache.getDescription(post));
                    table.getCellFormatter().setStyleName(row, 0, "desc");

                    table.setHTML(row, 1, Util.money(object.get(post)));
                    table.getCellFormatter().setStyleName(row, 1, "right");

                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);

                    row++;
                }
            }

        };

        AuthResponder.get(constants, messages, rh, constants.baseurl() + "accounting/endmonth.php?action=status");
    }

    public void onClick(Widget sender) {

        boolean okContinue = Window.confirm(messages.end_month_confirm());

        if (!okContinue) {
            return;
        }

        ServerResponse rh = new ServerResponse() {

            public void serverResponse(JSONValue resonseObj) {
                if ("1".equals(Util.str(resonseObj.isString()))) {
                    callback.viewMonth();
                }
            }

        };

        AuthResponder.get(constants, messages, rh, constants.baseurl() + "accounting/endmonth.php?action=end");
    }
}
