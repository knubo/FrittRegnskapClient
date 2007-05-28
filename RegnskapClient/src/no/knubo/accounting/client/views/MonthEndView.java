package no.knubo.accounting.client.views;

import java.util.Iterator;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
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

    public static MonthEndView getInstance(Constants constants,
            I18NAccount messages, ViewCallback callback) {
        if (me == null) {
            me = new MonthEndView(constants, messages, callback);
        }
        return me;
    }

    private MonthEndView(Constants constants, I18NAccount messages, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.callback = callback;

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.post());
        table.setHTML(0, 1, messages.amount());
        table.getRowFormatter().setStyleName(0, "header");

        dateHeader = new HTML();
        HTML header = new HTML(messages.end_month_explain());
        Button endButton = new Button(messages.end_month());
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

        ResponseTextHandler rh = new ResponseTextHandler() {
            public void onCompletion(String responseText) {
                PosttypeCache posttypeCache = PosttypeCache
                        .getInstance(constants);

                JSONValue jsonValue = JSONParser.parse(responseText);

                JSONObject root = jsonValue.isObject();

                String year = Util.str(root.get("year"));
                String month = Util.str(root.get("month"));

                dateHeader.setHTML("<h2>" + Util.monthString(messages, month)
                        + " " + year + "</h2>");

                JSONValue postsValue = root.get("posts");
                JSONObject object = postsValue.isObject();

                int row = 1;
                for (Iterator i = object.keySet().iterator(); i.hasNext();) {
                    String post = (String) i.next();

                    table.setHTML(row, 0, post + " -  "
                            + posttypeCache.getDescription(post));
                    table.getCellFormatter().setStyleName(row, 0, "desc");

                    table.setHTML(row, 1, Util.money(object.get(post)));
                    table.getCellFormatter().setStyleName(row, 1, "right");
                    
                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    table.getRowFormatter().setStyleName(row, style);

                    row++;
                }
            }

        };
        // TODO Report stuff as being loaded.
        if (!HTTPRequest.asyncGet(constants.baseurl() + "accounting/endmonth.php?action=status",
                rh)) {
            Window.alert(messages.failedConnect());
        }
    }

    public void onClick(Widget sender) {
        
        boolean okContinue = Window.confirm(messages.end_month_confirm());
        
        if(!okContinue) {
            return;
        }
        
        ResponseTextHandler rh = new ResponseTextHandler() {

            public void onCompletion(String responseText) {
                if("1".equals(responseText)) {
                    callback.viewMonth();
                } else {
                    Window.alert("Error from server:"+responseText);
                }
            }
            
        };
        if (!HTTPRequest.asyncGet(constants.baseurl() + "accounting/endmonth.php?action=end",
                rh)) {
            Window.alert(messages.failedConnect());
        }
    }
}
