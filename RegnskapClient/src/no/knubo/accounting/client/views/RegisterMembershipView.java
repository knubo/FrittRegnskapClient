package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.views.modules.UserSearchCallback;
import no.knubo.accounting.client.views.modules.UserSearchFields;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegisterMembershipView extends Composite implements ClickListener,
        UserSearchCallback {

    private static RegisterMembershipView me;

    private final I18NAccount messages;

    private final Constants constants;

    private FlexTable resultTable;

    private RegisterMembershipView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;

        UserSearchFields userSearchFields = new UserSearchFields(messages, this);

        DockPanel dp = new DockPanel();

        HTML header = new HTML();
        String headerText = "<h2>" + messages.register_membership_header()
                + "</h2>";
        header.setHTML(headerText);

        HTML help = new HTML();
        help.setHTML(messages.register_membership_help());

        dp.add(header, DockPanel.NORTH);
        dp.add(help, DockPanel.NORTH);

        dp.add(userSearchFields.getSearchTable(), DockPanel.NORTH);

        resultTable = new FlexTable();
        resultTable.setStyleName("tableborder");

        resultTable.getRowFormatter().setStyleName(0, "header");
        resultTable.setHTML(0, 0, messages.firstname());
        resultTable.setHTML(0, 1, messages.lastname());
        resultTable.setHTML(0, 2, messages.email());
        resultTable.setHTML(0, 3, messages.year_membership());
        resultTable.setHTML(0, 4, messages.course_membership());
        resultTable.setHTML(0, 5, messages.train_membership());
        resultTable.setHTML(0, 6, messages.paid_day());
        resultTable.setHTML(0, 7, messages.post());
        dp.add(resultTable, DockPanel.NORTH);

        Button button = new Button(messages.register_membership());
        button.addClickListener(this);
        dp.add(button, DockPanel.NORTH);

        initWidget(dp);
    }

    public static RegisterMembershipView show(I18NAccount messages,
            Constants constants, ViewCallback caller) {
        if (me == null) {
            me = new RegisterMembershipView(messages, constants);
        }
        return me;
    }

    public void onClick(Widget sender) {

    }

    public void doSearch(StringBuffer searchRequest) {
        while (resultTable.getRowCount() > 1) {
            resultTable.removeRow(1);
        }
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "registers/persons.php");

        RequestCallback callback = new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                Window.alert(exception.getMessage());
            }

            public void onResponseReceived(Request request, Response response) {
                JSONValue value = JSONParser.parse(response.getText());

                if (value == null) {
                    Window.alert(messages.search_failed());
                    return;
                }
                JSONArray array = value.isArray();

                if (array == null) {
                    Window.alert(messages.search_failed());
                    return;
                }

                if (array.size() == 0) {
                    resultTable.setHTML(1, 0, messages.no_result());
                    resultTable.getFlexCellFormatter().setColSpan(1, 0, 7);
                    return;
                }

                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    if (i > 30) {
                        Window.alert(messages.too_many_hits("30"));
                        return;
                    }

                    String firstname = Util.str(obj.get("firstname"));
                    String lastname = Util.str(obj.get("lastname"));

                    int row = i + 1;
                    resultTable.setHTML(row, 0, firstname);
                    resultTable.setHTML(row, 1, lastname);
                    resultTable.setHTML(row, 2, Util.str(obj.get("email")));

                    resultTable.setWidget(row, 3, new CheckBox());
                    resultTable.getCellFormatter().setStyleName(row, 3,
                            "center");

                    resultTable.setWidget(row, 4, new CheckBox());
                    resultTable.getCellFormatter().setStyleName(row, 4,
                            "center");

                    resultTable.setWidget(row, 5, new CheckBox());
                    resultTable.getCellFormatter().setStyleName(row, 5,
                            "center");

                    TextBox dayBox = new TextBox();
                    dayBox.setMaxLength(2);
                    dayBox.setVisibleLength(2);
                    resultTable.setWidget(row, 6, dayBox);

                    ListBox payments = new ListBox();
                    payments.setVisibleItemCount(1);
                    PosttypeCache.getInstance(constants)
                            .fillMembershipPayments(payments);

                    resultTable.setWidget(row, 7, payments);

                    String style = (row % 2 == 0) ? "showlineposts2"
                            : "showlineposts1";
                    resultTable.getRowFormatter().setStyleName(row, style);

                }
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(searchRequest.toString(), callback);
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

}
