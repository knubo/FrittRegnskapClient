package no.knubo.accounting.client.views.kid;

import java.util.HashMap;
import java.util.Set;

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
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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

    private double sum;
    private TextBoxWithErrorText amountBox;
    private JSONObject kidPosts;
    private NamedButton okButton;
    private TextBoxWithErrorText descriptionBox;

    public EditKIDPopup(JSONObject kid, JSONObject prices, JSONObject posts,
            RegisterMembershipKIDView registerMembershipKIDView) {

        this.kid = kid;
        this.prices = prices;
        this.posts = posts;
        this.caller = registerMembershipKIDView;

        this.elements = caller.elements;
        this.constants = caller.constants;
        this.messages = caller.messages;

        setModal(true);
        String personName = Util.strSkipNull(kid.get("firstname")) + " " + Util.strSkipNull(kid.get("lastname"));

        setText(caller.elements.kid_payement_edit() + " " + personName + " " + Util.money(kid.get("amount")));

        AccountTable topTable = new AccountTable("tableborder");

        descriptionBox = registerMembershipKIDView.registerStandards.createDescriptionBox();

        if (kid.containsKey("description")) {
            descriptionBox.setText(Util.str(kid.get("description")));
        } else {
            descriptionBox.setText("M:" + personName);
        }

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

        createCheckboxTooltips();

        aTable = new AccountTable("tableborder");
        aTable.setText(0, 0, elements.kid_kredit_posts());
        aTable.setColSpanAndRowStyle(0, 0, 4, "header");
        aTable.setText(1, 0, elements.account());
        aTable.setText(1, 1, "");
        aTable.setText(1, 2, elements.amount());
        aTable.setText(1, 3, "");
        aTable.setHeaderRowStyle(1);

        aTable.setText(3, 1, elements.sum());
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

        if (!Util.isNull(kid.get("memberid"))) {
            vp.add(new Label(messages.kid_membership_already()));
        }
        if (!Util.isNull(kid.get("youth"))) {
            vp.add(new Label(messages.kid_youth_already()));
        }
        if (!Util.isNull(kid.get("course"))) {
            vp.add(new Label(messages.kid_course_already()));
        }
        if (!Util.isNull(kid.get("train"))) {
            vp.add(new Label(messages.kid_train_already()));
        }

        vp.add(aTable);

        vp.add(new Label(elements.description()));
        vp.add(descriptionBox);

        HorizontalPanel hp = new HorizontalPanel();

        abortButton = new NamedButton("abort", elements.abort());
        abortButton.addClickHandler(this);
        abortButton.addStyleName("buttonrow");

        okButton = new NamedButton("ok", elements.ok());
        okButton.addClickHandler(this);
        okButton.addStyleName("buttonrow");

        hp.add(okButton);
        hp.add(abortButton);

        vp.add(hp);

        add(vp);
        center();
        fillInitialData();
        lockPaidMemberships();
    }

    private void createCheckboxTooltips() {
        Set<String> priceKeys = prices.keySet();

        for (String key : priceKeys) {

            String elemKey = key.equals("yearyouth") ? "year_youth" : key;

            String costTitle = elements.getString(elemKey + "_membership");

            JSONValue price = prices.get(key);
            String tooltip = messages.kid_membership_cost(costTitle, Util.money(price));

            checkboxes.get(elemKey).setTitle(tooltip);
        }
    }

    private void lockPaidMemberships() {
        if (!Util.isNull(kid.get("memberid"))) {
            checkboxes.get("year").setEnabled(false);
            checkboxes.get("year_youth").setEnabled(false);
        }

        if (!Util.isNull(kid.get("youth")) || !Util.isNull(kid.get("train")) || !Util.isNull(kid.get("course"))) {
            checkboxes.get("course").setEnabled(false);
            checkboxes.get("train").setEnabled(false);
            checkboxes.get("youth").setEnabled(false);
        }
    }

    private Widget checkbox(String id, String field) {
        NamedCheckBox namedCheckBox = new NamedCheckBox(id);
        checkboxes.put(field, namedCheckBox);
        namedCheckBox.addClickHandler(this);
        return namedCheckBox;
    }

    private void fillInitialData() {
        if (kid.containsKey("accounting")) {
            kidPosts = kid.get("accounting").isObject();
            fillAccounting(kidPosts);
            return;
        }

        kidPosts = new JSONObject();

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

        setSum();
    }

    private void setSum() {
        int row = aTable.getRowCount() - 3;
        aTable.setText(row, 2, Util.money(sum), "right");

        if (Util.getDouble(kid.get("amount")) != sum) {
            aTable.getCellFormatter().addStyleName(row, 2, "error");
            okButton.setEnabled(false);
        } else {
            aTable.getCellFormatter().removeStyleName(row, 2, "error");
            okButton.setEnabled(true);
        }
    }

    private void addPayment(String paymentKey) {
        JSONValue price = prices.get(paymentKey);
        String bdgKey = RegisterMembershipKIDView.postGiveBDG.get(paymentKey);

        String post = Util.str(posts.get(bdgKey));

        aTable.insertRow(2);

        aTable.setText(2, 0, post);
        aTable.setText(2, 1, postTypeCache.getDescription(post));
        aTable.setText(2, 2, Util.money(price), "right");
        Image deleteImage = ImageFactory.deleteImage("del_post");
        deleteImage.addClickHandler(this);
        aTable.setWidget(2, 3, deleteImage);

        String elemKey = paymentKey.equals("yearyouth") ? "year_youth" : paymentKey;
        String elemText = elements.getString(elemKey + "_membership");
        String tooltip = messages.kid_membership_creation(elemText);
        deleteImage.setTitle(tooltip);

        double amount = Util.getDouble(price);
        sum += amount;

        kidPosts.put(post, new JSONNumber(amount));
        kidPosts.put(post + "_tip", new JSONString(tooltip));

    }

    private void fillAccounting(JSONObject sourceKidPosts) {
        kidPosts = new JSONObject();

        Set<String> posts = sourceKidPosts.keySet();

        for (String post : posts) {
            if (post.endsWith("_tip")) {
                continue;
            }

            JSONNumber amountJ = sourceKidPosts.get(post).isNumber();

            kidPosts.put(post, amountJ);

            aTable.insertRow(2);
            aTable.setText(2, 0, post);
            aTable.setText(2, 1, postTypeCache.getDescription(post));
            aTable.setText(2, 2, Util.money(amountJ), "right");
            Image deleteImage = ImageFactory.deleteImage("del_post" + post);
            deleteImage.addClickHandler(this);
            aTable.setWidget(2, 3, deleteImage);
            sum += amountJ.doubleValue();

            if (sourceKidPosts.containsKey(post + "_tip")) {
                deleteImage.setTitle(Util.str(sourceKidPosts.get(post + "_tip")));
            }

        }

        JSONArray payments = kid.get("payments").isArray();

        if (payments == null) {
            return;
        }

        for (int i = 0; i < payments.size(); i++) {
            String paymentKey = Util.str(payments.get(i));

            checkboxes.get(paymentKey).setValue(true);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == abortButton) {
            hide();
        }
        if (event.getSource() == addButton) {
            addLine();
        }

        if (event.getSource() instanceof Image) {
            delRowWithImage(event.getSource());
        }

        if (event.getSource() == okButton) {
            checkAndOK();
        }
        if (event.getSource() instanceof NamedCheckBox) {
            checkBoxClick((NamedCheckBox) event.getSource());
        }
    }

    private void checkBoxClick(NamedCheckBox namedCheckBox) {
        if (!namedCheckBox.getValue()) {
            return;
        }
        if (namedCheckBox == checkboxes.get("year")) {
            checkboxes.get("year_youth").setValue(false);
        } else if (namedCheckBox == checkboxes.get("year_youth")) {
            checkboxes.get("year").setValue(false);
        } else if (namedCheckBox == checkboxes.get("course")) {
            checkboxes.get("youth").setValue(false);
            checkboxes.get("train").setValue(false);
        } else if (namedCheckBox == checkboxes.get("train")) {
            checkboxes.get("youth").setValue(false);
            checkboxes.get("course").setValue(false);
        } else if (namedCheckBox == checkboxes.get("youth")) {
            checkboxes.get("train").setValue(false);
            checkboxes.get("course").setValue(false);
        }
    }

    private void checkAndOK() {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), descriptionBox);

        if (!mv.validateStatus()) {
            return;
        }

        String[] fields = { "year", "year_youth", "youth", "train", "course" };

        JSONArray payments = new JSONArray();

        for (String field : fields) {
            if (checkboxes.get(field).getValue()) {
                payments.set(payments.size(), new JSONString(field));
            }
        }

        if (payments.size() == 0) {
            if (!Window.confirm(messages.kid_no_membership())) {
                return;
            }
        }

        if (doesNotMatchPayments(payments)) {
            if (!Window.confirm(messages.kid_does_not_match())) {
                return;
            }
        }

        kid.put("payments", payments);
        kid.put("accounting", kidPosts);
        kid.put("description", new JSONString(descriptionBox.getText()));
        kid.put("fix", new JSONString("0"));
        kid.put("edited", new JSONString("1"));
        hide();

        caller.kidEdited(kid);
    }

    private boolean doesNotMatchPayments(JSONArray payments) {

        HashMap<String, Double> amounts = new HashMap<String, Double>();

        for (int i = 0; i < payments.size(); i++) {
            String paymentKey = Util.str(payments.get(i));
            JSONValue price = prices.get(paymentKey);

            String bdgKey = RegisterMembershipKIDView.postGiveBDG.get(paymentKey);
            String post = Util.str(posts.get(bdgKey));

            if (!amounts.containsKey(post)) {
                amounts.put(post, Util.getDouble(price));
            } else {
                amounts.put(post, Util.getDouble(price) + amounts.get("post"));
            }
        }

        Util.log("Amounts:" + amounts + " KidPosts:" + kidPosts);

        Set<String> requiredPosts = amounts.keySet();

        for (String post : requiredPosts) {
            Double val = amounts.get(post);

            JSONValue posted = kidPosts.get(post);

            if (posted == null) {
                return true;
            }

            double postedVal = posted.isNumber().doubleValue();

            if (postedVal != val) {
                return true;
            }
        }

        return false;
    }

    private void delRowWithImage(Object source) {
        int maxRow = aTable.getRowCount() - 4;
        for (int row = 2; row < maxRow; row++) {
            if (aTable.getWidget(row, 3) == source) {
                String post = aTable.getText(row, 0);

                JSONNumber val = kidPosts.get(post).isNumber();
                sum -= val.doubleValue();

                aTable.removeRow(row);
                kidPosts.put(post, null);
                setSum();
                Util.log("Deleted row:" + row);
                return;
            }

        }
        Util.log("Failed to find " + source);
    }

    private void addLine() {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), amountBox, accountIdBox);
        mv.money(messages.field_money(), amountBox);
        mv.registry(messages.registry_invalid_key(), postTypeCache, accountIdBox);

        int maxRow = aTable.getRowCount() - 4;
        for (int row = 2; row < maxRow; row++) {
            if (accountIdBox.getText().equals(aTable.getText(row, 0))) {
                mv.fail(accountIdBox, true, messages.kid_account_used());
            }
        }

        if (!mv.validateStatus()) {
            return;
        }

        aTable.insertRow(2);

        double amount = Double.parseDouble(amountBox.getText().replaceAll(",", ""));
        kidPosts.put(accountIdBox.getText(), new JSONNumber(amount));
        aTable.setText(2, 0, accountIdBox.getText());
        aTable.setText(2, 1, postTypeCache.getDescription(accountIdBox.getText()));
        aTable.setText(2, 2, Util.money(amountBox.getText()), "right");
        Image deleteImage = ImageFactory.deleteImage("del_post" + accountIdBox.getText());
        deleteImage.addClickHandler(this);
        aTable.setWidget(2, 3, deleteImage);
        sum += amount;

        setSum();

    }
}
