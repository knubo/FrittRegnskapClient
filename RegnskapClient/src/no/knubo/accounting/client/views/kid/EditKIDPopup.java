package no.knubo.accounting.client.views.kid;

import java.util.HashMap;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedCheckBox;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
    private final JSONObject posts;
    private HashMap<String, NamedCheckBox> checkboxes = new HashMap<String, NamedCheckBox>();

    private final HashMap<String, String> postGiveBDG = new HashMap<String, String>();
    private double sum;
    private TextBoxWithErrorText amountBox;

    public EditKIDPopup(JSONObject kid, JSONObject prices, JSONObject posts,
            RegisterMembershipKIDView registerMembershipKIDView) {
        postGiveBDG.put("course", "BDG_COURSE_POST");
        postGiveBDG.put("train", "BDG_TRAIN_POST");
        postGiveBDG.put("year", "BDG_YEAR_POST");
        postGiveBDG.put("year_youth", "BDG_YEAR_POST");
        postGiveBDG.put("youth", "BDG_YOUTH_POST");

        this.kid = kid;
        this.prices = prices;
        this.posts = posts;
        this.caller = registerMembershipKIDView;

        this.elements = caller.elements;
        this.constants = caller.constants;
        this.messages = caller.messages;

        setModal(true);
        setText(caller.elements.kid_payement_edit());

        AccountTable topTable = new AccountTable("tableborder");

        topTable.setText(0, 0, elements.kid_register_membership());
        topTable.setColSpanAndRowStyle(0, 0, 4, "header");
        topTable.setWidget(1, 0, checkbox("year_membership", "year"));
        topTable.setText(1, 1, elements.year_membership());

        topTable.setWidget(2, 0, checkbox("year_membership_youth", "year_youth"));
        topTable.setText(2, 1, elements.year_membership_youth());

        topTable.setWidget(1, 2, checkbox("course_membership", "course"));
        topTable.setText(1, 3, elements.course_membership());

        topTable.setWidget(2, 2, checkbox("train_membership", "train"));
        topTable.setText(2, 3, elements.train_membership());

        topTable.setWidget(3, 2, checkbox("youth_membership", "youth"));
        topTable.setText(3, 3, elements.youth_membership());

        aTable = new AccountTable("tableborder");
        aTable.setText(0, 0, elements.kid_kredit_posts());
        aTable.setColSpanAndRowStyle(0, 0, 4, "header");
        aTable.setText(1, 0, elements.account());
        aTable.setText(1, 1, "");
        aTable.setText(1, 2, elements.amount());
        aTable.setText(1, 3, "");
        aTable.setHeaderRowStyle(1);

        aTable.setText(3, 0, elements.sum());
        aTable.getRowFormatter().addStyleName(3, "sumline");

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
        amountBox = caller.registerStandards.createAmountBox();
        aTable.setWidget(4, 2, amountBox);
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

    private Widget checkbox(String id, String field) {
        NamedCheckBox namedCheckBox = new NamedCheckBox(id);
        checkboxes.put(field, namedCheckBox);
        return namedCheckBox;
    }

    private void fillInitialData() {
        if (kid.containsKey("accounting")) {
            fillAccounting();
            return;
        }

        if (!kid.containsKey("payments")) {
            return;
        }

        JSONArray payments = kid.get("payments").isArray();

        sum = 0;
        for (int i = 0; i < payments.size(); i++) {
            String paymentKey = Util.str(payments.get(i));

            checkboxes.get(paymentKey).setValue(true);

            addPayment(paymentKey);
        }

        aTable.setText(aTable.getRowCount() - 3, 2, Util.money(sum), "right");
    }

    private void addPayment(String paymentKey) {
        JSONValue price = prices.get(paymentKey);
        String bdgKey = postGiveBDG.get(paymentKey);

        String post = Util.str(posts.get(bdgKey));

        aTable.insertRow(2);

        aTable.setText(2, 0, post);
        aTable.setText(2, 1, postTypeCache.getDescription(post));
        aTable.setText(2, 2, Util.money(price), "right");
        Image deleteImage = ImageFactory.deleteImage("del_post");
        deleteImage.addClickHandler(this);
        aTable.setWidget(2, 3, deleteImage);
        sum += Util.getDouble(price);
    }

    private void fillAccounting() {

    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == abortButton) {
            hide();
        }
        if (event.getSource() == addButton) {
            addLine();
        }
    }

    private void addLine() {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), amountBox, accountIdBox);
        mv.money(messages.field_money(), amountBox);
        mv.registry(messages.registry_invalid_key(), postTypeCache, accountIdBox);

        if (!mv.validateStatus()) {
            return;
        }

        int maxRow = aTable.getRowCount() - 3;
        for (int row = 2; row < maxRow; row++) {
            if(accountIdBox.getText().equals(aTable.getText(row, 0))) {
                mv.fail(accountIdBox, true, messages.kid_account_used());
            }
        }

    }
}
