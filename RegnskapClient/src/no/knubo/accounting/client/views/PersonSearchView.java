package no.knubo.accounting.client.views;

import java.util.HashMap;

import no.knubo.accounting.client.AccountingGWT;
import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.views.modules.UserSearchCallback;
import no.knubo.accounting.client.views.modules.UserSearchFields;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PersonSearchView extends Composite implements ClickHandler, UserSearchCallback {

    private static PersonSearchView me;

    private PersonPickCallback personPick;

    private HashMap<String, JSONObject> idGivesObject;

    public static PersonSearchView getInstance(ViewCallback caller, I18NAccount messages, Constants constants,
            Elements elements) {
        if (me == null) {
            me = new PersonSearchView(messages, constants, elements, true);
        }
        me.setCaller(caller);
        me.setVisible(true);
        return me;
    }

    public static PersonSearchView pick(PersonPickCallback personPick, I18NAccount messages, Constants constants,
            Elements elements) {
        PersonSearchView psv = new PersonSearchView(messages, constants, elements, false);
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

    private IdHolder<String, Image> idHolder;

    private final Elements elements;

    private UserSearchFields userSearchFields;

    private void setCaller(ViewCallback caller) {
        this.caller = caller;
        this.personPick = null;
    }

    private PersonSearchView(I18NAccount messages, Constants constants, Elements elements, boolean includeShowHidden) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        userSearchFields = new UserSearchFields(this, elements, includeShowHidden);

        this.idHolder = new IdHolder<String, Image>();

        DockPanel dp = new DockPanel();

        dp.add(userSearchFields.getSearchTable(), DockPanel.NORTH);

        resultTable = new FlexTable();
        dp.add(resultTable, DockPanel.NORTH);
        resultTable.setStyleName("tableborder");

        resultTable.getRowFormatter().setStyleName(0, "header");
        resultTable.setHTML(0, 0, elements.firstname());
        resultTable.setHTML(0, 1, elements.lastname() + " (" + elements.member_number() + ") ");
        resultTable.setHTML(0, 2, elements.email());
        resultTable.setHTML(0, 3, elements.address());
        resultTable.setHTML(0, 4, elements.phone());
        resultTable.setHTML(0, 5, elements.cellphone());
        resultTable.setHTML(0, 6, elements.employee());
        resultTable.setHTML(0, 7, elements.gender());
        resultTable.setHTML(0, 8, "");
        initWidget(dp);
    }
    

    @Override
    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        if (personPick == null) {
            doEditPerson(sender);
        } else {
            pickPerson(sender);
        }
    }

    private void pickPerson(Widget sender) {
        String id = idHolder.findId(sender);
        JSONObject personObj = idGivesObject.get(id);
        personPick.pickPerson(id, personObj);
    }

    private void doEditPerson(Widget sender) {
        String id = idHolder.findId(sender);

        setVisible(false);
        caller.editPerson(id);
    }

    @Override
    public void doSearch(StringBuffer searchRequest) {
        doClear();

        idGivesObject = new HashMap<String, JSONObject>();

        final PersonSearchView personSV = this;

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue value) {
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

                    String id = Util.str(obj.get("id"));
                    String firstname = Util.strSkipNull(obj.get("firstname"));
                    String lastname = Util.strSkipNull(obj.get("lastname"));
                    int row = i + 1;
                    resultTable.setHTML(row, 0, firstname);
                    resultTable.setHTML(row, 1, lastname + " (" + id + ")");
                    resultTable.setHTML(row, 2, Util.strSkipNull(obj.get("email")));

                    boolean secretAddress = "#SECRET#".equals(Util.strSkipNull(obj.get("address")));
                    if (secretAddress) {
                        resultTable.setHTML(row, 3, elements.secret_address());
                        resultTable.getCellFormatter().addStyleName(row, 3, "secret");
                    } else {
                        resultTable.setHTML(row, 3, Util.strSkipNull(obj.get("address")));
                        resultTable.setHTML(row, 4, Util.strSkipNull(obj.get("phone")));
                        resultTable.setHTML(row, 5, Util.strSkipNull(obj.get("cellphone")));
                    }

                    if ("1".equals(Util.str(obj.get("employee")))) {
                        resultTable.setHTML(row, 6, elements.x());
                    } else {
                        resultTable.setHTML(row, 6, "");
                    }
                    resultTable.getCellFormatter().setStyleName(row, 6, "center");

                    resultTable.setHTML(row, 7, Util.strSkipNull(obj.get("gender")));

                    String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    resultTable.getRowFormatter().setStyleName(row, style);

                    Image image = null;

                    boolean blocked = false;
                    if (personPick == null) {
                        if (secretAddress && !AccountingGWT.canSeeSecret) {
                            image = ImageFactory.unreadableImage("personSearchView_unreadable" + row);
                            blocked = true;
                        } else {
                            image = ImageFactory.editImage("personSearchView_editImage" + row);
                        }
                    } else {
                        image = ImageFactory.chooseImage("personSearchView_pickImage" + row);
                    }
                    if (!blocked) {
                        image.addClickHandler(personSV);
                        idHolder.add(id, image);
                        idGivesObject.put(id, obj);
                    }
                    resultTable.setWidget(row, 8, image);
                }
            }
        };

        AuthResponder.post(constants, messages, callback, searchRequest, "registers/persons.php");

    }

    @Override
    public void doClear() {
        while (resultTable.getRowCount() > 1) {
            resultTable.removeRow(1);
        }
    }
}
