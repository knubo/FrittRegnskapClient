package no.knubo.accounting.client.views.kid;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.PersonPickCallback;
import no.knubo.accounting.client.views.PersonPickView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ListKIDView extends Composite implements ClickHandler, PersonPickCallback {

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

    public void init() {
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == searchImage) {
            PersonPickView.show(messages, constants, this, helpPanel, elements).center();
        }
        
        if(event.getSource() == searchButton) {
            doSearch();
        }
    }

    private void doSearch() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
        
        if(!validate()) {
            return;
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("action=list");
        Util.addPostParam(sb, "from", fromDate.getText());
        
    }

    private boolean validate() {
        MasterValidator mv = new MasterValidator();
        mv.date(messages.date_format(), fromDate, toDate);
        mv.range(messages.field_to_low_zero(), 1, Integer.MAX_VALUE, memberBox);
        return mv.validateStatus();
    }

    public void pickPerson(String id, JSONObject personObj) {
        memberBox.setText(id);
        criteriatable.setText(2, 3, Util.str(personObj.get("firstname")) + " " + Util.str(personObj.get("lastname")));
    }
}
