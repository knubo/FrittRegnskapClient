package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;

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
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PersonSearchView extends Composite implements ClickListener {

    private static PersonSearchView me;

    public static PersonSearchView show(ViewCallback caller,
            I18NAccount messages, Constants constants) {
        if (me == null) {
            me = new PersonSearchView(caller, messages, constants);
        }
        return me;
    }

    private I18NAccount messages;

    private Constants constants;

    private TextBox firstnameBox;

    private TextBox lastnameBox;

    private TextBox emailBox;

    private ListBox employeeList;

    private Button searchButton;

    private Button clearButton;

    private FlexTable resultTable;

    private PersonSearchView(ViewCallback caller, I18NAccount messages,
            Constants constants) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();
        FlexTable searchTable = new FlexTable();
        searchTable.setStyleName("edittable");

        dp.add(searchTable, DockPanel.NORTH);

        firstnameBox = new TextBox();
        firstnameBox.setMaxLength(50);
        lastnameBox = new TextBox();
        lastnameBox.setMaxLength(50);
        emailBox = new TextBox();
        emailBox.setMaxLength(100);
        employeeList = new ListBox();
        employeeList.setVisibleItemCount(1);
        employeeList.addItem("", "");
        employeeList.addItem(messages.not_employee(), "0");
        employeeList.addItem(messages.employee(), "1");

        searchTable.setText(0, 0, messages.firstname());
        searchTable.setWidget(0, 1, firstnameBox);
        searchTable.setText(0, 2, messages.lastname());
        searchTable.setWidget(0, 3, lastnameBox);
        searchTable.setText(1, 0, messages.email());
        searchTable.setWidget(1, 1, emailBox);
        searchTable.getFlexCellFormatter().setColSpan(1, 1, 3);
        searchTable.setText(2, 0, messages.employee());
        searchTable.setWidget(2, 1, employeeList);

        searchButton = new Button(messages.search());
        searchButton.addClickListener(this);
        searchTable.setWidget(3, 0, searchButton);
        clearButton = new Button(messages.clear());
        clearButton.addClickListener(this);
        searchTable.setWidget(3, 1, clearButton);

        resultTable = new FlexTable();
        dp.add(resultTable, DockPanel.NORTH);
        resultTable.setStyleName("tableborder");

        resultTable.getRowFormatter().setStyleName(0, "header");
        resultTable.setHTML(0, 0, messages.firstname());
        resultTable.setHTML(0, 1, messages.lastname());
        resultTable.setHTML(0, 2, messages.email());
        resultTable.setHTML(0, 3, messages.address());
        resultTable.setHTML(0, 4, messages.phone());
        resultTable.setHTML(0, 5, messages.cellphone());
        resultTable.setHTML(0, 6, messages.employee());

        initWidget(dp);

    }

    public void onClick(Widget sender) {
        if (sender == searchButton) {
            doSearch();
        } else if (sender == clearButton) {
            doClear();
        }
    }

    private void doClear() {
        firstnameBox.setText("");
        lastnameBox.setText("");
        emailBox.setText("");
        employeeList.setSelectedIndex(0);
    }

    private void doSearch() {
        while (resultTable.getRowCount() > 1) {
            resultTable.removeRow(1);
        }

        StringBuffer sb = new StringBuffer();

        sb.append("action=search");
        Util.addPostParam(sb, "firstname", firstnameBox.getText());
        Util.addPostParam(sb, "lastname", lastnameBox.getText());
        Util.addPostParam(sb, "email", emailBox.getText());
        Util.addPostParam(sb, "employee", Util.getSelected(employeeList));

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

                    String firstname = Util.str(obj.get("firstname"));
                    String lastname = Util.str(obj.get("lastname"));
                    String cellphone = Util.str(obj.get("cellphone"));

                    int row = i + 1;
                    resultTable.setHTML(row, 0, firstname);
                    resultTable.setHTML(row, 1, lastname);
                    resultTable.setHTML(row, 2, Util.str(obj.get("address")));
                    resultTable.setHTML(row, 3, Util.str(obj.get("phone")));
                    resultTable.setHTML(row, 4, cellphone);
                    resultTable.setHTML(row, 5, Util.str(obj.get("email")));

                    if ("1".equals(Util.str(obj.get("employee")))) {
                        resultTable.setHTML(row, 6, messages.x());
                    } else {
                        resultTable.setHTML(row, 6, "");
                    }
                    resultTable.getCellFormatter().setStyleName(row, 6, "center");

                    String style = (row % 2 == 0) ? "showlineposts2"
                            : "showlineposts1";
                    resultTable.getRowFormatter().setStyleName(row, style);

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
