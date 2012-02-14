package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.TrustActionCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class TrustStatusView extends Composite implements ClickHandler {

    private static TrustStatusView trustStatusInstance;

    private final Constants constants;

    private final I18NAccount messages;

    private AccountTable table;

    private TrustEditFields editFields;

    private final HelpPanel helpPanel;

    private IdHolder<String, Image> idHolder;

    private Button newTrustButton;

    private final ViewCallback callback;

    private final Elements elements;

    public static TrustStatusView getInstance(Constants constants, I18NAccount messages,
            HelpPanel helpPanel, ViewCallback callback, Elements elements) {
        if (trustStatusInstance == null) {
            trustStatusInstance = new TrustStatusView(constants, messages, helpPanel, callback,
                    elements);
        }
        return trustStatusInstance;
    }

    public TrustStatusView(Constants constants, I18NAccount messages, HelpPanel helpPanel,
            ViewCallback callback, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.helpPanel = helpPanel;
        this.callback = callback;
        this.elements = elements;

        idHolder = new IdHolder<String, Image>();

        DockPanel dp = new DockPanel();

        newTrustButton = new NamedButton("trustStatusView_newTrustButton", elements
                .trustStatusView_newTrustButton());
        newTrustButton.addClickHandler(this);
        dp.add(newTrustButton, DockPanel.NORTH);

        table = new AccountTable("tableborder");
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public void init() {
        idHolder.init();
        while (table.getRowCount() > 0) {
            table.removeRow(0);
        }

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();
                renderResult(object);
            }
        };

        AuthResponder.get(constants, messages, callback, "accounting/edittrust.php");
    }

    protected void renderResult(JSONObject object) {
        JSONValue value = object.get("types");
        JSONArray array = value.isArray();
        JSONValue dataVal = object.get("data");
        JSONObject dataObj = dataVal.isObject();

        JSONValue sumFondVal = object.get("sumfond");
        JSONValue sumClubVal = object.get("sumclub");
        JSONObject sumFondObj = sumFondVal.isObject();
        JSONObject sumClubObj = sumClubVal.isObject();

        for (int i = 0; i < array.size(); i++) {
            JSONValue fondVal = array.get(i);
            JSONObject fondObj = fondVal.isObject();

            String fond = Util.str(fondObj.get("fond"));
            String description = Util.str(fondObj.get("description"));
            JSONValue fondLines = dataObj.get(fond);
            JSONValue sumFond = sumFondObj.get(fond);
            JSONValue sumClub = sumClubObj.get(fond);

            renderFond(description, fondLines.isArray(), sumFond, sumClub);
        }
        helpPanel.resize(this);
    }

    private void renderFond(String description, JSONArray fondlines, JSONValue sumFond,
            JSONValue sumClub) {

        int row = table.getRowCount();
        table.setHTML(row, 0, description);
        table.setHeaderRowStyle(row);
        table.getFlexCellFormatter().setColSpan(row, 0, 5);
        row++;

        table.setHTML(row, 0, elements.description());
        table.setHTML(row, 1, elements.date());
        table.setHTML(row, 2, elements.trust_account());
        table.setHTML(row, 3, elements.club_account());
        table.setHTML(row, 4, "");
        table.setHeaderRowStyle(row);
        row++;

        for (int i = 0; i < fondlines.size(); i++) {
            JSONValue lineVal = fondlines.get(i);
            JSONObject lineObj = lineVal.isObject();

            table.setHTML(row, 0, Util.str(lineObj.get("Description")));
            table.getCellFormatter().setStyleName(row, 0, "desc");
            table.setHTML(row, 1, Util.formatDate(lineObj.get("Occured")));
            table.getCellFormatter().setStyleName(row, 1, "desc");

            table.setMoney(row, 2, lineObj.get("Fond_account"), "right colspace");
            table.setMoney(row, 3, lineObj.get("Club_account"), "right colspace");

            int accountline = Util.getInt(lineObj.get("AccountLine"));
            if (accountline > 0) {
                Image viewPostImage = ImageFactory.editImage("TrustStatusView.viewPost");
                viewPostImage.addClickHandler(this);
                table.setWidget(row, 4, viewPostImage);
                idHolder.add(String.valueOf(accountline), viewPostImage);
            } else {
                table.setText(row, 4, "");
            }
            table.alternateStyle(row, (i % 2 == 0));
            row++;
        }
        table.setHTML(row, 0, elements.sum());
        table.setMoney(row, 2, sumFond);
        table.setMoney(row, 3, sumClub);
        table.getRowFormatter().setStyleName(row, "sumline");
        row++;
    }

    @Override
    public void onClick(ClickEvent event) {
    	Widget sender = (Widget) event.getSource();
    	
    	if (sender == newTrustButton) {
            if (editFields == null) {
                editFields = new TrustEditFields();
            }

            int left = 0;
            left = sender.getAbsoluteLeft() + 10;

            int top = sender.getAbsoluteTop() + 10;
            editFields.setPopupPosition(left, top);

            editFields.init();
            editFields.show();
            helpPanel.addEventHandler();
            return;
        }

        String id = idHolder.findId(sender);
        callback.openDetails(id);

    }

    class TrustEditFields extends DialogBox implements ClickHandler, ChangeHandler {

        private HTML errorLabelForDate;
        private HTML mainErrorLabel;

        private ListBoxWithErrorText actionListBox;
        private ListBoxWithErrorText trustListBox;
        private TextBoxWithErrorText dayBox;
        private TextBoxWithErrorText monthBox;
        private TextBoxWithErrorText yearBox;
        private TextBoxWithErrorText descBox;
        private TextBoxWithErrorText amountBox;
        private TextBoxWithErrorText attachmentBox;
        private TextBoxWithErrorText postNmbBox;

        private Button saveButton;
        private Button cancelButton;
        private RegisterStandards registerStandards;

        TrustEditFields() {
            registerStandards = new RegisterStandards(constants, messages, elements, callback);

            setText(elements.new_trust());
            FlexTable edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, elements.trust());
            edittable.setHTML(1, 0, elements.action());
            edittable.setHTML(3, 0, elements.date());
            edittable.setHTML(4, 0, elements.postnmb());
            edittable.setHTML(5, 0, elements.attachment());
            edittable.setHTML(6, 0, elements.description());
            edittable.setHTML(7, 0, elements.amount());

            actionListBox = new ListBoxWithErrorText("TrustStatusView.actionList");
            actionListBox.getListbox().addClickHandler(this);

            trustListBox = new ListBoxWithErrorText("TrustStatusView.trustList");
            trustListBox.getListbox().setVisibleItemCount(1);
            trustListBox.getListbox().addChangeHandler(this);

            TrustActionCache trustActionCache = TrustActionCache.getInstance(constants, messages);
            trustActionCache.fillTrustList(trustListBox.getListbox());

            edittable.setWidget(0, 1, trustListBox);
            edittable.setWidget(1, 1, actionListBox);

            errorLabelForDate = new HTML();

            dayBox = registerStandards.createDayBox(errorLabelForDate, "day_single");
            monthBox = registerStandards.createMonthBox(errorLabelForDate);
            yearBox = registerStandards.createYearBox(errorLabelForDate);

            HorizontalPanel hp = new HorizontalPanel();
            hp.add(dayBox);
            hp.add(monthBox);
            hp.add(yearBox);
            hp.add(errorLabelForDate);

            edittable.setWidget(3, 1, hp);

            postNmbBox = registerStandards.getPostNmbBox();
            edittable.setWidget(4, 1, postNmbBox);

            attachmentBox = registerStandards.getAttachmentBox();
            edittable.setWidget(5, 1, attachmentBox);

            descBox = registerStandards.createDescriptionBox();
            edittable.setWidget(6, 1, descBox);

            amountBox = registerStandards.createAmountBox();
            edittable.setWidget(7, 1, amountBox);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("TrustStatusView.saveButton", elements.save());
            saveButton.addClickHandler(this);
            cancelButton = new NamedButton("trustStatusView.cancelButton", elements
                    .trustStatusView_cancelButton());
            cancelButton.addClickHandler(this);

            mainErrorLabel = new HTML();
            mainErrorLabel.setStyleName("error");

            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(mainErrorLabel);
            dp.add(buttonPanel, DockPanel.NORTH);

            setWidget(dp);
        }

        public void init() {
            dayBox.setText("");
            descBox.setText("");
            amountBox.setText("");
            actionListBox.getListbox().clear();
            if (trustListBox.getListbox().getItemCount() > 0) {
                trustListBox.setSelectedIndex(0);
            }
            registerStandards.fetchInitalData(false);
        }

        @Override
        public void onClick(ClickEvent event) {
        	Widget sender = (Widget) event.getSource();
        	if (sender == cancelButton) {
                hide();
            } else if (sender == saveButton && validateFields()) {
                doSave();
            }
        }

        private void doSave() {
            StringBuffer sb = new StringBuffer();
            sb.append("action=add");

            Util.addPostParam(sb, "day", dayBox.getText());
            Util.addPostParam(sb, "month", monthBox.getText());
            Util.addPostParam(sb, "year", yearBox.getText());
            Util.addPostParam(sb, "desc", descBox.getText());
            Util.addPostParam(sb, "attachment", attachmentBox.getText());
            Util.addPostParam(sb, "postnmb", postNmbBox.getText());
            final String money = Util.fixMoney(amountBox.getText());
            Util.addPostParam(sb, "amount", money);
            Util.addPostParam(sb, "actionid", Util.getSelected(actionListBox.getListbox()));

            ServerResponse callback = new ServerResponse() {

                @Override
                public void serverResponse(JSONValue value) {

                    JSONObject object = value.isObject();

                    JSONValue result = object.get("result");

                    if ("1".equals(Util.str(result))) {
                        hide();
                        trustStatusInstance.init();
                    } else {
                        // TODO fix to error message.
                        Window.alert("Failed to add data");
                    }
                }
            };

            AuthResponder.post(constants, messages, callback, sb, "accounting/edittrust.php");
        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();

            if (attachmentBox.isEnabled()) {
                /*
                 * This one does some of the below + some other nice controls
                 * valid for postnmb./attachmentnmb.
                 */
                registerStandards.validateTop();
            }
            mv.mandatory(messages.required_field(), new Widget[] { dayBox, monthBox, yearBox,
                    actionListBox, trustListBox, descBox, amountBox });

            mv.day(messages.illegal_day(), Util.getInt(yearBox.getText()), Util.getInt(monthBox
                    .getText()), new Widget[] { dayBox });

            mv.money(messages.field_money(), new Widget[] { amountBox });

            return mv.validateStatus();
        }

        @Override
        public void onChange(ChangeEvent event) {
            TrustActionCache trustActionCache = TrustActionCache.getInstance(constants, messages);

            Widget sender = (Widget) event.getSource();
            if (sender == this.trustListBox.getListbox()) {
                ListBox listBox = (ListBox) sender;

                String selected = Util.getSelected(listBox);
                trustActionCache.fillActionList(actionListBox.getListbox(), selected);
            }

            if (sender == this.actionListBox.getListbox()) {
                String selected = Util.getSelected(actionListBox.getListbox());

                if ("".equals(selected)) {
                    return;
                }

                trustActionCache.fillDefaultDesc(descBox, selected);

                boolean addsAccountLine = trustActionCache.addsAccountLineUponSave(selected);

                if (addsAccountLine) {
                    monthBox.setText(String.valueOf(registerStandards.getCurrentMonth()));
                    yearBox.setText(String.valueOf(registerStandards.getCurrentYear()));
                    registerStandards.fetchInitalData(true);
                } else {
                    postNmbBox.setText("");
                    attachmentBox.setText("");
                }

                attachmentBox.setEnabled(addsAccountLine);
                postNmbBox.setEnabled(addsAccountLine);
                monthBox.setEnabled(!addsAccountLine);
                yearBox.setEnabled(!addsAccountLine);
            }
        }

    }
}
