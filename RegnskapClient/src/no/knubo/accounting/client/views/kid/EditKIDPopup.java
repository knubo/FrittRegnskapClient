package no.knubo.accounting.client.views.kid;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedCheckBox;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EditKIDPopup extends DialogBox implements ClickHandler {

    private final JSONObject kid;
    private final JSONObject prices;
    private final RegisterMembershipKIDView caller;
    private NamedButton abortButton;
    private Elements elements;
    private AccountTable aTable;
    private TextBoxWithErrorText accountIdBox;
    private Constants constants;
    private I18NAccount messages;
    private NamedButton addButton;
    private PosttypeCache postTypeCache;

    public EditKIDPopup(JSONObject kid, JSONObject prices, RegisterMembershipKIDView registerMembershipKIDView) {
        this.kid = kid;
        this.prices = prices;
        this.caller = registerMembershipKIDView;

        this.elements = caller.elements;
        this.constants = caller.constants;
        this.messages = caller.messages;
        
        setModal(true);
        setText(caller.elements.kid_payement_edit());

        AccountTable topTable = new AccountTable("tableborder");

        topTable.setText(0, 0, elements.kid_register_membership());
        topTable.setColSpanAndRowStyle(0, 0, 4, "header");
        topTable.setWidget(1, 0, new NamedCheckBox("year_membership"));
        topTable.setText(1, 1, elements.year_membership());

        topTable.setWidget(2, 0, new NamedCheckBox("year_membership_youth"));
        topTable.setText(2, 1, elements.year_membership_youth());

        topTable.setWidget(1, 2, new NamedCheckBox("course_membership"));
        topTable.setText(1, 3, elements.course_membership());

        topTable.setWidget(2, 2, new NamedCheckBox("train_membership"));
        topTable.setText(2, 3, elements.train_membership());

        topTable.setWidget(3, 2, new NamedCheckBox("youth_membership"));
        topTable.setText(3, 3, elements.youth_membership());

        aTable = new AccountTable("tableborder");
        aTable.setText(0, 0, elements.kid_kredit_posts());
        aTable.setColSpanAndRowStyle(0, 0, 4, "header");
        aTable.setText(1, 0, elements.account());
        aTable.setText(1, 1, "");
        aTable.setText(1, 2, elements.amount());
        aTable.setText(1, 3, "");
        aTable.setHeaderRowStyle(1);

        aTable.setText(3, 0, elements.sum(), "sumline");

        HTML errorAccountHtml = new HTML();
        accountIdBox = new TextBoxWithErrorText("account", errorAccountHtml);
        accountIdBox.setVisibleLength(6);
        aTable.setWidget(4, 0, accountIdBox);
        aTable.setWidget(5, 0, errorAccountHtml);
        aTable.setColSpanAndRowStyle(5, 0, 2, "");

        ListBox accountNameBox = new ListBox();
        accountNameBox.setVisibleItemCount(1);
        aTable.setWidget(4, 0, accountIdBox);

        postTypeCache = PosttypeCache.getInstance(constants, messages);
        postTypeCache.fillAllEarnings(accountNameBox);
        Util.syncListbox(accountNameBox, accountIdBox.getTextBox());
        aTable.setWidget(4, 1, accountNameBox);
        aTable.setWidget(4, 2, caller.registerStandards.createAmountBox());

        addButton = new NamedButton("add", elements.add());
        addButton.addClickHandler(this);
        aTable.setWidget(5, 1, addButton);
        
        VerticalPanel vp = new VerticalPanel();

        vp.add(topTable);
        vp.add(aTable);

        HorizontalPanel hp = new HorizontalPanel();

        
        abortButton = new NamedButton("abort", elements.abort());
        abortButton.addClickHandler(this);
        abortButton.addStyleName("buttonrow");
        hp.add(abortButton);

        vp.add(hp);

        add(vp);
        center();
        fillInitialData();
    }

    private void fillInitialData() {
        if(kid.containsKey("accounting")) {
            fillAccounting();
            return;
        }
        
        if(!kid.containsKey("payment")) {
            return;
        }
        
        JSONArray payments = kid.get("payments").isArray();

        for(int i=0; i < payments.size(); i++) {
            String paymentKey = Util.str(payments.get(i));
        }
        
    }

    private void fillAccounting() {
        // TODO Auto-generated method stub
        
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == abortButton) {
            hide();
        }
    }

}
