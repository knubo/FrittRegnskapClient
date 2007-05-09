package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
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
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PersonSearchView extends Composite implements ClickListener, UserSearchCallback {

    private static PersonSearchView me;

    public static PersonSearchView show(ViewCallback caller,
            I18NAccount messages, Constants constants) {
        if (me == null) {
            me = new PersonSearchView(caller, messages, constants);
        }
        me.setVisible(true);
        return me;
    }

    private I18NAccount messages;

    private Constants constants;

    private final ViewCallback caller;

    private FlexTable resultTable;

    private IdHolder idHolder;


    private PersonSearchView(ViewCallback caller, I18NAccount messages,
            Constants constants) {
        this.caller = caller;
        this.messages = messages;
        this.constants = constants;
        UserSearchFields userSearchFields = new UserSearchFields(messages, this);

        this.idHolder = new IdHolder();

        DockPanel dp = new DockPanel();

        dp.add(userSearchFields.getSearchTable(), DockPanel.NORTH);

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
        resultTable.setHTML(0, 7, "");
        initWidget(dp);

    }

    public void onClick(Widget sender) {

        doEditPerson(sender);
    }

    private void doEditPerson(Widget sender) {
        String id = idHolder.findId(sender);

        setVisible(false);
        caller.editPerson(id);
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

                    String firstname = Util.str(obj.get("firstname"));
                    String lastname = Util.str(obj.get("lastname"));
                    String cellphone = Util.str(obj.get("cellphone"));

                    int row = i + 1;
                    resultTable.setHTML(row, 0, firstname);
                    resultTable.setHTML(row, 1, lastname);
                    resultTable.setHTML(row, 2, Util.str(obj.get("email")));
                    resultTable.setHTML(row, 3, Util.str(obj.get("address")));
                    resultTable.setHTML(row, 4, Util.str(obj.get("phone")));
                    resultTable.setHTML(row, 5, cellphone);

                    if ("1".equals(Util.str(obj.get("employee")))) {
                        resultTable.setHTML(row, 6, messages.x());
                    } else {
                        resultTable.setHTML(row, 6, "");
                    }
                    resultTable.getCellFormatter().setStyleName(row, 6,
                            "center");

                    String style = (row % 2 == 0) ? "showlineposts2"
                            : "showlineposts1";
                    resultTable.getRowFormatter().setStyleName(row, style);

                    Image image = ImageFactory.editImage();
                    image.addClickListener(me);
                    idHolder.add(Util.str(obj.get("id")), image);
                    resultTable.setWidget(row, 7, image);
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
