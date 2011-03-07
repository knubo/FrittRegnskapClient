package no.knubo.accounting.client.views.ownings;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegisterOwningsView extends Composite {

    private static RegisterOwningsView instance;
    private Constants constants;
    private I18NAccount messages;
    private Elements elements;
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

    public static RegisterOwningsView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new RegisterOwningsView(constants, messages, elements);
        }
        return instance;
    }

    public RegisterOwningsView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;

        table = new AccountTable("tableborder");
        table.setText(0, 0, elements.menuitem_owning_register());
        table.setColSpanAndRowStyle(0, 0, 2, "header");

        table.setText(1, 0, elements.owning());
        table.setText(2, 0, elements.identifier());
        table.setText(3, 0, elements.description());
        table.setText(4, 0, elements.owning_purchase_price());
        table.setText(5, 0, elements.owning_buy_date());
        table.setText(6, 0, elements.owning_year_to_deprecation());
        table.setText(7, 0, elements.owning_account());
        table.setText(8, 0, elements.owning_deprecation());

        owning = new TextBoxWithErrorText("owning");
        description = new NamedTextArea("description");
        serial = new TextBoxWithErrorText("serial");
        purchasePrice = new TextBoxWithErrorText("purchase_price");
        purchaseDate = new TextBoxWithErrorText("purchase_date");
        yearsDeprecation = new TextBoxWithErrorText("purchase_date");
        accountOwning = new TextBoxWithErrorText("owning_account");
        accountDeprecation = new TextBoxWithErrorText("owning_deprecation");
        owningListbox = new ListBoxWithErrorText("owning_listbox");
        deprecationListbox = new ListBoxWithErrorText("deprecation_listbox");
        
        table.setWidget(1, 1, owning);
        table.setWidget(2, 1, serial);
        table.setWidget(3, 1, description);
        table.setWidget(4, 1, purchasePrice);
        table.setWidget(5, 1, purchaseDate);
        table.setWidget(6, 1, yearsDeprecation);

        HorizontalPanel owningPanel = new HorizontalPanel();
        owningPanel.add(accountOwning);
        owningPanel.add(owningListbox);
        table.setWidget(7, 1, owningPanel);
        
        
        HorizontalPanel deprecationPanel = new HorizontalPanel();
        deprecationPanel.add(accountDeprecation);
        deprecationPanel.add(deprecationListbox);
        table.setWidget(8, 1, deprecationPanel);

        VerticalPanel vp = new VerticalPanel();
        vp.add(table);

        HorizontalPanel buttonPanel = new HorizontalPanel();
        NamedButton registerButton = new NamedButton("register_owning", elements.owning_register());
        buttonPanel.add(registerButton);

        PosttypeCache posttypeCache = PosttypeCache.getInstance(constants, messages);
        
        posttypeCache.fillAllDeprecations(deprecationListbox.getListbox());
        posttypeCache.fillAllOwnings(owningListbox.getListbox());

        Util.syncListbox(deprecationListbox.getListbox(), accountDeprecation.getTextBox());
        Util.syncListbox(owningListbox.getListbox(), accountOwning.getTextBox());
        
        vp.add(buttonPanel);

        initWidget(vp);
    }

    public void init() {

    }

}
