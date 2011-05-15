package no.knubo.accounting.client.views.kid;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.PersonPickCallback;
import no.knubo.accounting.client.views.PersonPickView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ListKIDView extends Composite implements ClickHandler, PersonPickCallback, KeyUpHandler {

    private static ListKIDView me;
    final Elements elements;
    final I18NAccount messages;
    final Constants constants;
    private AccountTable table;
    private AccountTable criteriatable;
    private TextBoxWithErrorText fromDate;
    private TextBoxWithErrorText toDate;
    private ListBoxWithErrorText statusDropDown;
    private TextBoxWithErrorText memberBox;
    private Image searchImage;
    private final HelpPanel helpPanel;
    private NamedButton searchButton;

    public static ListKIDView show(I18NAccount messages, Constants constants, Elements elements, HelpPanel helpPanel) {
        if (me == null) {
            me = new ListKIDView(messages, constants, elements, helpPanel);
        }
        return me;
    }

    public ListKIDView(I18NAccount messages, Constants constants, Elements elements, HelpPanel helpPanel) {

        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        this.helpPanel = helpPanel;

        criteriatable = new AccountTable("edittable");
        table = new AccountTable("tableborder");

        VerticalPanel vp = new VerticalPanel();
        vp.add(criteriatable);
        vp.add(table);

        criteriatable.setText(0, 0, elements.from_date());
        fromDate = new TextBoxWithErrorText("from_date");
        criteriatable.setWidget(0, 1, fromDate);
        criteriatable.setText(0, 2, elements.to_date());
        toDate = new TextBoxWithErrorText("to_date");
        criteriatable.setWidget(0, 3, toDate);
        criteriatable.setText(1, 0, elements.status());

        statusDropDown = new ListBoxWithErrorText("status");
        statusDropDown.addItem("", "");
        statusDropDown.addItem(elements.kid_status_handled(), "1");
        statusDropDown.addItem(elements.kid_status_handled_exception(), "2");
        statusDropDown.addItem(elements.kid_status_unhandled(), "0");
        criteriatable.setWidget(1, 1, statusDropDown);
        criteriatable.setText(2, 0, elements.member_number());
        memberBox = new TextBoxWithErrorText("member_number");
        memberBox.addDelayedKeyUpHandler(this);
        criteriatable.setWidget(2, 1, memberBox);
        searchImage = ImageFactory.searchImage("find_member");
        searchImage.addClickHandler(this);
        criteriatable.setWidget(2, 2, searchImage);

        searchButton = new NamedButton("search", elements.search());
        searchButton.addClickHandler(this);
        criteriatable.setWidget(3, 0, searchButton);

        table.setText(0, 0, "KID");
        table.setText(0, 1, elements.kid_transaction_number());
        table.setText(0, 2, elements.kid_settlement_date());
        table.setText(0, 3, elements.person());
        table.setText(0, 4, elements.status());

        table.setHeaderRowStyle(0);

        initWidget(vp);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == searchImage) {
            PersonPickView.show(messages, constants, this, helpPanel, elements).center();
        }

        if (event.getSource() == searchButton) {
            doSearch();
        }
    }

    private void doSearch() {
        if (!validate()) {
            return;
        }

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("action=list");
        Util.addPostParam(sb, "fromDate", fromDate.getText());
        Util.addPostParam(sb, "toDate", toDate.getText());
        Util.addPostParam(sb, "status", Util.getSelected(statusDropDown));
        Util.addPostParam(sb, "member", memberBox.getText());

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                showKIDlines(responseObj.isArray());

            }
        };
        AuthResponder.post(constants, messages, callback, sb, "accounting/register_by_kid.php");
    }

    protected void showKIDlines(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject kid = array.get(i).isObject();
            int row = i + 1;
            table.setText(row, 0, Util.str(kid.get("kid")));
            table.setText(row, 1, Util.str(kid.get("transaction_number")));
            table.setText(row, 2, Util.formatDate(kid.get("settlement_date")));
            table.setText(row, 3, Util.strSkipNull(kid.get("firstname")) + " " + Util.strSkipNull(kid.get("lastname")));
            table.setText(row, 4, findStatus(Util.str(kid.get("kid_status"))));
        }
    }

    private String findStatus(String str) {
        if ("0".equals(str)) {
            return elements.kid_status_unhandled();
        }

        if ("1".equals(str)) {
            return elements.kid_status_handled();
        }

        if ("2".equals(str)) {
            return elements.kid_status_handled_exception();
        }
        return "";
    }

    private boolean validate() {
        MasterValidator mv = new MasterValidator();
        mv.date(messages.date_format(), fromDate, toDate);
        if (memberBox.getText().length() > 0) {
            mv.range(messages.field_to_low_zero(), 1, Integer.MAX_VALUE, memberBox);
        }
        return mv.validateStatus();
    }

    public void pickPerson(String id, JSONObject personObj) {
        memberBox.setText(id);
        criteriatable.setText(2, 3, Util.str(personObj.get("firstname")) + " " + Util.str(personObj.get("lastname")));
    }

    public void onKeyUp(KeyUpEvent event) {

        if (memberBox.getText().length() == 0) {
            return;
        }
        MasterValidator mv = new MasterValidator();
        mv.range(messages.field_to_low_zero(), 1, Integer.MAX_VALUE, memberBox);

        if (!mv.validateStatus()) {
            return;
        }

        final String id = memberBox.getText();

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                if (array.size() == 0) {
                    criteriatable.setText(2, 3, messages.no_result());
                } else {
                    pickPerson(id, array.get(0).isObject());
                }

            }
        };

        AuthResponder.get(constants, messages, callback, "registers/persons.php?action=search&id=" + id);
    }
}
