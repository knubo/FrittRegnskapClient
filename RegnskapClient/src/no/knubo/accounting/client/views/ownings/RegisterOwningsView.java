package no.knubo.accounting.client.views.ownings;

import java.util.Date;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.DayValidator;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegisterOwningsView extends Composite implements ClickHandler, KeyUpHandler {

    private static RegisterOwningsView instance;
    private Constants constants;
    private I18NAccount messages;
    private AccountTable table;
    private TextBoxWithErrorText owning;
    private NamedTextArea description;
    private TextBoxWithErrorText purchasePrice;
    private TextBoxWithErrorText purchaseDate;
    private TextBoxWithErrorText yearsDeprecation;
    private TextBoxWithErrorText accountOwning;
    private TextBoxWithErrorText accountDeprecation;
    private TextBoxWithErrorText serial;
    private ListBoxWithErrorText owningListbox;
    private ListBoxWithErrorText deprecationListbox;
    private TextBoxWithErrorText warrentyDate;
    private Label infoLabel;
    private PosttypeCache posttypeCache;

    private AccountTable addedBelongings = new AccountTable("tableborder");
    private double eachMonth;
    private double currentAmount;
    private RegisterStandards registerStandards;
    private TextBoxWithErrorText attachmentBox;
    private HTML attachmentLabel;
    private HTML dayLabel;
    private TextBoxWithErrorText dayBox;
    private final Elements elements;

    public static RegisterOwningsView getInstance(Constants constants, I18NAccount messages, Elements elements,
            ViewCallback caller) {
        if (instance == null) {
            instance = new RegisterOwningsView(constants, messages, elements, caller);
        }
        return instance;
    }

    public RegisterOwningsView(Constants constants, I18NAccount messages, Elements elements, ViewCallback caller) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;

        registerStandards = new RegisterStandards(constants, messages, elements, caller, true);
        table = new AccountTable("tableborder full");
        table.setText(0, 0, elements.menuitem_owning_register());
        table.setColSpanAndRowStyle(0, 0, 4, "header");

        HTML errorOwning = new HTML();
        owning = registerStandards.createDescriptionBox(errorOwning);

        description = new NamedTextArea("description");

        HTML errorSerial = new HTML();
        serial = new TextBoxWithErrorText("serial", errorSerial);

        HTML errorPurchaseDate = new HTML();
        purchaseDate = new TextBoxWithErrorText("purchase_date", errorPurchaseDate);
        purchaseDate.addDelayedKeyUpHandler(this);
        HTML errorWarrentyDate = new HTML();
        warrentyDate = new TextBoxWithErrorText("warrenty_date", errorWarrentyDate);

        HTML errorPurchasePrice = new HTML();
        purchasePrice = new TextBoxWithErrorText("purchase_price", errorPurchasePrice);
        purchasePrice.addDelayedKeyUpHandler(this);

        HTML errorYearDeprecation = new HTML();
        yearsDeprecation = new TextBoxWithErrorText("purchase_date", errorYearDeprecation);
        yearsDeprecation.addDelayedKeyUpHandler(this);

        HTML errorOwningAccount = new HTML();
        accountOwning = new TextBoxWithErrorText("owning_account", errorOwningAccount);
        accountOwning.setWidth("5em");

        HTML errorAccountDeprecation = new HTML();
        accountDeprecation = new TextBoxWithErrorText("owning_deprecation", errorAccountDeprecation);
        accountDeprecation.setWidth("5em");

        attachmentBox = registerStandards.getAttachmentBox();
        attachmentLabel = registerStandards.getAttachmentLabel();

        dayLabel = new HTML();
        dayBox = registerStandards.createDayBox(dayLabel, "day");

        owningListbox = new ListBoxWithErrorText("owning_listbox");
        deprecationListbox = new ListBoxWithErrorText("deprecation_listbox");

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
        table.setText(9, 1, elements.owning_year_to_deprecation());
        table.setWidget(10, 0, errorPurchasePrice);
        table.setWidget(10, 1, errorYearDeprecation);
        table.setWidget(11, 0, purchasePrice);
        table.setWidget(11, 1, yearsDeprecation);
        table.setText(12, 0, elements.owning_remaining(), "desc");
        table.setText(12, 1, elements.owning_month_deprecation(), "desc");
        table.setText(13, 0, "N/A");
        table.setText(13, 1, "N/A");
        table.setText(14, 0, elements.attachment());
        table.setText(14, 1, elements.day());
        table.setWidget(15, 0, attachmentLabel);
        table.setWidget(15, 1, dayLabel);
        table.setWidget(16, 0, attachmentBox);
        table.setWidget(16, 1, dayBox);

        table.setText(17, 0, elements.owning_account());
        table.setWidget(18, 0, errorOwningAccount);
        table.setWidget(19, 0, owningPanel);
        table.setColSpanAndRowStyle(19, 0, 2, "");
        table.setText(20, 0, elements.owning_deprecation());
        table.setWidget(21, 0, errorAccountDeprecation);
        table.setWidget(22, 0, deprecationPanel);
        table.setColSpanAndRowStyle(22, 0, 2, "");

        VerticalPanel vp = new VerticalPanel();
        vp.add(table);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        NamedButton registerButton = new NamedButton("register_owning", elements.owning_register());
        registerButton.addClickHandler(this);
        buttonPanel.add(registerButton);

        posttypeCache = PosttypeCache.getInstance(constants, messages);

        posttypeCache.fillAllDeprecations(deprecationListbox.getListbox());
        posttypeCache.fillAllOwnings(owningListbox.getListbox());

        Util.syncListbox(deprecationListbox.getListbox(), accountDeprecation.getTextBox());
        Util.syncListbox(owningListbox.getListbox(), accountOwning.getTextBox());

        infoLabel = new Label();
        vp.add(infoLabel);

        vp.add(buttonPanel);
        vp.add(addedBelongings);
        initWidget(vp);
    }

    public void init() {
        registerStandards.fetchInitalData(true);
        owning.setFocus(true);
    }

    public void onClick(ClickEvent event) {
        MasterValidator mv = new MasterValidator();

        mv.mandatory(messages.required_field(), owning, serial, purchaseDate, purchasePrice);
        mv.money(messages.field_money(), purchasePrice);
        mv.date(messages.date_format(), purchaseDate, warrentyDate);

        if (getDeprecationYears() > 0 || !accountDeprecation.getText().isEmpty() || !accountOwning.getText().isEmpty()) {

            mv.mandatory(messages.required_field(), yearsDeprecation, accountDeprecation, accountOwning, attachmentBox);
            mv.registry(messages.registry_invalid_key(), posttypeCache, accountDeprecation, accountOwning);
            if (!registerStandards.validateTop()) {
                return;
            }

        }

        if (!mv.validateStatus()) {
            return;
        }

        createBelonging();
    }

    private void createBelonging() {

        final String serialValue = serial.getText();
        final String owningValue = owning.getText();
        final String purchaseDateValue = purchaseDate.getText();
        final String warrentyDateValue = warrentyDate.getText();
        final String purchasePriceValue = purchasePrice.getText();
        final String yearsDeprecationValue = yearsDeprecation.getText();
        String accountDeprecationValue = accountDeprecation.getText();
        String accountOwningValue = accountOwning.getText();

        StringBuffer sb = new StringBuffer();
        sb.append("action=add");
        Util.addPostParam(sb, "owning", owningValue);
        Util.addPostParam(sb, "description", description.getText());
        Util.addPostParam(sb, "serial", serialValue);
        Util.addPostParam(sb, "eachMonth", String.valueOf(eachMonth));
        Util.addPostParam(sb, "purchaseDate", purchaseDateValue);
        Util.addPostParam(sb, "warrentyDate", warrentyDateValue);
        Util.addPostParam(sb, "purchasePrice", purchasePriceValue);
        Util.addPostParam(sb, "yearsDeprecation", yearsDeprecationValue);
        Util.addPostParam(sb, "accountDeprecation", accountDeprecationValue);
        Util.addPostParam(sb, "accountOwning", accountOwningValue);
        Util.addPostParam(sb, "currentAmount", String.valueOf(currentAmount));
        Util.addPostParam(sb, "year", String.valueOf(registerStandards.getCurrentYear()));
        Util.addPostParam(sb, "month", String.valueOf(registerStandards.getCurrentMonth()));
        Util.addPostParam(sb, "postnmb", registerStandards.getPostNmbBox().getText());
        Util.addPostParam(sb, "attachment", String.valueOf(attachmentBox.getText()));
        Util.addPostParam(sb, "deprecationTitle", elements.deprecation());

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                boolean ok = Util.getBoolean(responseObj.isObject().get("status"));

                if (addedBelongings.getRowCount() == 0) {
                    addedBelongings.setText(0, 0, elements.owning());
                    addedBelongings.setText(0, 1, elements.identifier());
                    addedBelongings.setText(0, 2, elements.owning_buy_date());
                    addedBelongings.setText(0, 3, elements.owning_warrenty_date());
                    addedBelongings.setText(0, 4, elements.owning_purchase_price());
                    addedBelongings.setText(0, 5, elements.owning_year_to_deprecation());
                    addedBelongings.setHeaderRowStyle(0);
                }

                if (ok) {
                    addedBelongings.insertRow(1);
                    addedBelongings.setText(1, 0, owningValue);
                    addedBelongings.setText(1, 1, serialValue);
                    addedBelongings.setText(1, 2, purchaseDateValue);
                    addedBelongings.setText(1, 3, warrentyDateValue);
                    addedBelongings.setText(1, 4, purchasePriceValue);
                    addedBelongings.setText(1, 5, yearsDeprecationValue);
                } else {
                    infoLabel.setText(messages.save_failed());
                }
                owning.setFocus(true);
            }
        };
        AuthResponder.post(constants, messages, callback, sb, "accounting/belongings.php");
    }

    public void onKeyUp(KeyUpEvent event) {
        double purchase = getPurchasePrice();
        int years = getDeprecationYears();
        Date pd = getPurchaseDate();

        if (purchase <= 0 || years <= 0 || pd == null) {
            table.setText(13, 0, "N/A");
            table.setText(13, 1, "N/A");
            infoLabel.setText(messages.deprecation_without_account());
            eachMonth = 0;
            currentAmount = 0;
            return;
        }

        int monthsUsed = getMonthSincePurchase(pd);

        int monthToDeprecate = years * 12;

        eachMonth = purchase / monthToDeprecate;

        currentAmount = purchase - (eachMonth * monthsUsed);

        if (currentAmount < 0) {
            table.setText(13, 0, Util.money(0d));
            table.setText(13, 1, "N/A");
            infoLabel.setText(messages.deprecation_nothing_left());
            return;
        }

        infoLabel.setText(messages.deprecation_with_account());

        table.setText(13, 0, Util.money(currentAmount));
        table.setText(13, 1, Util.money(eachMonth));
    }

    @SuppressWarnings("deprecation")
    private int getMonthSincePurchase(Date purchaseDate) {
        Date now = new Date();

        int month = now.getMonth();
        int year = now.getYear();

        int purchaseMonth = purchaseDate.getMonth();
        int purcahseYear = purchaseDate.getYear();

        return (year - purcahseYear) * 12 + (month - purchaseMonth);
    }

    private Date getPurchaseDate() {
        return DayValidator.getDate(purchaseDate.getText());
    }

    private int getDeprecationYears() {
        if (yearsDeprecation.getText().isEmpty()) {
            return 0;
        }

        try {
            return Integer.valueOf(yearsDeprecation.getText());
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    private double getPurchasePrice() {
        if (purchasePrice.getText().isEmpty()) {
            return 0;
        }

        try {
            return Double.valueOf(purchasePrice.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
