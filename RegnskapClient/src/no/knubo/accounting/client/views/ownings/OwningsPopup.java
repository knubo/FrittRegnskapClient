package no.knubo.accounting.client.views.ownings;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.views.PersonPickCallback;
import no.knubo.accounting.client.views.PersonPickView;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class OwningsPopup extends DialogBox implements ClickHandler, PersonPickCallback {

    AccountTable table = new AccountTable("edittable");
    private TextBoxWithErrorText owning;
    private NamedTextArea description;
    private TextBoxWithErrorText serial;
    private TextBoxWithErrorText purchaseDate;
    private TextBoxWithErrorText warrentyDate;
    private TextBoxWithErrorText purchasePrice;
    private TextBoxWithErrorText accountOwning;
    private TextBoxWithErrorText accountDeprecation;
    private ListBoxWithErrorText owningListbox;
    private ListBoxWithErrorText deprecationListbox;
    private TextBoxWithErrorText remaining;
    private TextBoxWithErrorText eachMonth;
    private HTML eachMonthLabel;
    private HTML remainingLabel;
    private NamedButton updateButton;
    private NamedButton closeButton;
    private final I18NAccount messages;
    private final Constants constants;
    private Image addImage;
    private Label responsibleLabel;
    private final Elements elements;
    private final HelpPanel helpPanel;

    public OwningsPopup(int id, Elements elements, Constants constants, I18NAccount messages, HelpPanel helpPanel) {
        this.elements = elements;
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;
        setModal(true);

        setText(elements.owning_edit());
        
        table = new AccountTable("tableborder full");

        HTML errorOwning = new HTML();
        owning = new TextBoxWithErrorText("description");
        owning.setMaxLength(40);
        owning.setVisibleLength(40);

        description = new NamedTextArea("description");

        HTML errorSerial = new HTML();
        serial = new TextBoxWithErrorText("serial", errorSerial);

        HTML errorPurchaseDate = new HTML();
        purchaseDate = new TextBoxWithErrorText("purchase_date", errorPurchaseDate);
        HTML errorWarrentyDate = new HTML();
        warrentyDate = new TextBoxWithErrorText("warrenty_date", errorWarrentyDate);

        HTML errorPurchasePrice = new HTML();
        purchasePrice = new TextBoxWithErrorText("purchase_price", errorPurchasePrice);

        HTML errorOwningAccount = new HTML();
        accountOwning = new TextBoxWithErrorText("owning_account", errorOwningAccount);
        accountOwning.setWidth("5em");

        HTML errorAccountDeprecation = new HTML();
        accountDeprecation = new TextBoxWithErrorText("owning_deprecation", errorAccountDeprecation);
        accountDeprecation.setWidth("5em");

        owningListbox = new ListBoxWithErrorText("owning_listbox");
        deprecationListbox = new ListBoxWithErrorText("deprecation_listbox");

        remainingLabel = new HTML();
        eachMonthLabel = new HTML();
        remaining = new TextBoxWithErrorText("remaining", remainingLabel);
        eachMonth = new TextBoxWithErrorText("each_month", eachMonthLabel);

        HorizontalPanel owningPanel = new HorizontalPanel();
        owningPanel.add(accountOwning);
        owningPanel.add(owningListbox);

        HorizontalPanel deprecationPanel = new HorizontalPanel();
        deprecationPanel.add(accountDeprecation);
        deprecationPanel.add(deprecationListbox);

        table.setText(1, 0, elements.owning());
        table.setText(1, 1, elements.identifier());
        table.setWidget(2, 0, errorOwning);
        table.setWidget(2, 1, errorSerial);
        table.setWidget(3, 0, owning);
        table.setWidget(3, 1, serial);
        table.setText(4, 0, elements.description());
        table.setWidget(5, 0, description);
        table.setColSpanAndRowStyle(5, 0, 2, "");
        table.setText(6, 0, elements.owning_buy_date());
        table.setText(6, 1, elements.owning_warrenty_date());
        table.setWidget(7, 0, errorPurchaseDate);
        table.setWidget(7, 1, errorWarrentyDate);
        table.setWidget(8, 0, purchaseDate);
        table.setWidget(8, 1, warrentyDate);
        table.setText(9, 0, elements.owning_purchase_price());
        table.setWidget(10, 0, errorPurchasePrice);
        table.setWidget(11, 0, purchasePrice);
        table.setText(12, 0, elements.owning_remaining(), "desc");
        table.setText(12, 1, elements.owning_month_deprecation(), "desc");
        table.setWidget(13, 0, remainingLabel);
        table.setWidget(13, 1, eachMonthLabel);
        table.setWidget(14, 0, remaining);
        table.setWidget(14, 1, eachMonth);
        table.setText(15, 0, elements.owning_account());
        table.setWidget(17, 0, errorOwningAccount);
        table.setWidget(18, 0, owningPanel);
        table.setColSpanAndRowStyle(18, 0, 2, "");
        table.setText(19, 0, elements.owning_deprecation());
        table.setWidget(20, 0, errorAccountDeprecation);
        table.setWidget(21, 0, deprecationPanel);
        table.setColSpanAndRowStyle(21, 0, 2, "");
        table.setText(22, 0, elements.responsible());

        addImage = ImageFactory.chooseImage("choose");
        addImage.addClickHandler(this);

        HorizontalPanel fp = new HorizontalPanel();
        responsibleLabel = new Label();
        fp.add(responsibleLabel);
        fp.add(addImage);
        table.setWidget(23, 0, fp);
        table.setColSpanAndRowStyle(23, 0, 2, "");

        posttypeCache = PosttypeCache.getInstance(constants, messages);

        posttypeCache.fillAllDeprecations(deprecationListbox.getListbox());
        posttypeCache.fillAllOwnings(owningListbox.getListbox());

        Util.syncListbox(deprecationListbox.getListbox(), accountDeprecation.getTextBox());
        Util.syncListbox(owningListbox.getListbox(), accountOwning.getTextBox());

        VerticalPanel vp = new VerticalPanel();
        vp.add(table);

        add(vp);

        HorizontalPanel buttonPanel = new HorizontalPanel();

        updateButton = new NamedButton("update_owning", elements.update());
        updateButton.addClickHandler(this);
        buttonPanel.add(updateButton);

        closeButton = new NamedButton("close", elements.close());
        closeButton.addClickHandler(this);
        buttonPanel.add(closeButton);

        vp.add(buttonPanel);

        loadData(id);
        center();
    }

    private JSONObject current;
    private String responsibleId;
    private PosttypeCache posttypeCache;

    private void loadData(int id) {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                current = responseObj.isObject();

                owning.setText(Util.str(current.get("belonging")));
                serial.setText(Util.str(current.get("serial")));
                description.setText(Util.strSkipNull(current.get("description")));
                purchaseDate.setText(Util.formatDate(current.get("purchase_date")));
                warrentyDate.setText(Util.formatDate(current.get("warrenty_date")));
                purchasePrice.setText(Util.money(Util.strSkipNull(current.get("purchase_price"))));
                remaining.setText(Util.money(Util.strSkipNull(current.get("current_price"))));
                eachMonth.setText(Util.money(Util.strSkipNull(current.get("deprecation_amount"))));
                accountDeprecation.setText(Util.strSkipNull(current.get("deprecation_account")));
                accountOwning.setText(Util.strSkipNull(current.get("owning_account")));

                Util.syncOnce(owningListbox.getListbox(), accountOwning.getTextBox());
                Util.syncOnce(deprecationListbox.getListbox(), accountDeprecation.getTextBox());
            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/belongings.php?action=get&id=" + id);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == closeButton) {
            hide();
            return;
        }

        if (event.getSource() == addImage) {
            PersonPickView view = PersonPickView.show(messages, constants, this, helpPanel, elements);
            view.center();
        }
    }

    public void pickPerson(String id, JSONObject personObj) {
        responsibleLabel.setText(Util.str(personObj.get("firstname")) + " " + Util.str(personObj.get("lastname")));
        current.put("person", new JSONString(id));
    }

}
