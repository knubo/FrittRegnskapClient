package no.knubo.accounting.client.views;

import java.util.HashMap;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.views.modules.UserSearchCallback;
import no.knubo.accounting.client.views.modules.UserSearchFields;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
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

public class PersonSearchView extends Composite implements ClickListener,
        UserSearchCallback {

    private static PersonSearchView me;

    private PersonPickCallback personPick;

    private HashMap idGivesObject;

    public static PersonSearchView show(ViewCallback caller,
            I18NAccount messages, Constants constants) {
        if (me == null) {
            me = new PersonSearchView(messages, constants);
        }
        me.setCaller(caller);
        me.setVisible(true);
        return me;
    }

    public static PersonSearchView pick(PersonPickCallback personPick,
            I18NAccount messages, Constants constants) {
        PersonSearchView psv = new PersonSearchView(messages, constants);
        psv.setPicker(personPick);
        psv.setVisible(true);
        return psv;
    }

    private void setPicker(PersonPickCallback personPick) {
        this.personPick = personPick;
        this.caller = null;
    }

    private I18NAccount messages;

    private Constants constants;

    private ViewCallback caller;
    private FlexTable resultTable;

    private IdHolder idHolder;

    private void setCaller(ViewCallback caller) {
        this.caller = caller;
        this.personPick = null;
    }

    private PersonSearchView(I18NAccount messages, Constants constants) {
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
        if (personPick == null) {
            doEditPerson(sender);
        } else {
            pickPerson(sender);
        }
    }

    private void pickPerson(Widget sender) {
        String id = idHolder.findId(sender);
        JSONObject personObj = (JSONObject) idGivesObject.get(id);
        personPick.pickPerson(id, personObj);
    } 

    private void doEditPerson(Widget sender) {
        String id = idHolder.findId(sender);

        setVisible(false);
        caller.editPerson(id);
    }

    public void doSearch(StringBuffer searchRequest) {
        doClear();
        
        idGivesObject = new HashMap();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "registers/persons.php");

        final PersonSearchView personSV = this;

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String serverResponse) {
                JSONValue value = JSONParser.parse(serverResponse);

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

                    Image image = null;

                    if (personPick == null) {
                        image = ImageFactory
                                .editImage("personSearchView_editImage");
                    } else {
                        image = ImageFactory
                                .chooseImage("personSearchView_pickImage");
                    }
                    image.addClickListener(personSV);
                    String id = Util.str(obj.get("id"));
                    idHolder.add(id, image);
                    idGivesObject.put(id, obj);
                    resultTable.setWidget(row, 7, image);
                }
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest(searchRequest.toString(), new AuthResponder(constants, messages, callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    public void doClear() {
        while (resultTable.getRowCount() > 1) {
            resultTable.removeRow(1);
        }        
    }
}
