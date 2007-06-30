package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.TrustActionCache;
import no.knubo.accounting.client.misc.ListBoxWithErrorText;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;
import no.knubo.accounting.client.views.modules.RegisterStandards;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class TrustStatusView extends Composite implements ClickListener {

    private static TrustStatusView instance;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    private TrustEditFields editFields;

    public static TrustStatusView getInstance(Constants constants,
            I18NAccount messages) {
        if (instance == null) {
            instance = new TrustStatusView(constants, messages);
        }
        return instance;
    }

    public TrustStatusView(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;

        DockPanel dp = new DockPanel();

        Button button = new NamedButton("TrustStatusView.newTrustButton",
                messages.new_trust());
        button.addClickListener(this);
        dp.add(button, DockPanel.NORTH);

        table = new FlexTable();
        table.setStyleName("tableborder");
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public void init() {
        table.clear();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                constants.baseurl() + "accounting/edittrust.php");

        RequestCallback callback = new RequestCallback() {
            public void onError(Request request, Throwable exception) {
                Window.alert(exception.getMessage());
            }

            public void onResponseReceived(Request request, Response response) {
                JSONValue value = JSONParser.parse(response.getText());
                JSONObject object = value.isObject();
                renderResult(object);
            }
        };

        try {
            builder.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            builder.sendRequest("action=status", callback);
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
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
            String sumFond = Util.money(sumFondObj.get(fond));
            String sumClub = Util.money(sumClubObj.get(fond));

            renderFond(description, fondLines.isArray(), sumFond, sumClub);
        }
    }

    private void renderFond(String description, JSONArray fondlines,
            String sumFond, String sumClub) {

        int row = table.getRowCount();
        table.setHTML(row, 0, description);
        table.getRowFormatter().setStyleName(row, "header");
        table.getFlexCellFormatter().setColSpan(row, 0, 4);
        row++;

        table.setHTML(row, 0, messages.description());
        table.setHTML(row, 1, messages.date());
        table.setHTML(row, 2, messages.trust_account());
        table.setHTML(row, 3, messages.club_account());
        table.getRowFormatter().setStyleName(row, "header");
        row++;

        for (int i = 0; i < fondlines.size(); i++) {
            JSONValue lineVal = fondlines.get(i);
            JSONObject lineObj = lineVal.isObject();

            table.setHTML(row, 0, Util.str(lineObj.get("Description")));
            table.getCellFormatter().setStyleName(row, 0, "desc");
            table.setHTML(row, 1, Util.formatDate(lineObj.get("Occured")));
            table.getCellFormatter().setStyleName(row, 1, "desc");

            table.setHTML(row, 2, Util.money(lineObj.get("Fond_account")));
            table.getCellFormatter().setStyleName(row, 2, "right colspace");

            table.setHTML(row, 3, Util.money(lineObj.get("Club_account")));
            table.getCellFormatter().setStyleName(row, 3, "right colspace");

            String style = (i % 2 == 0) ? "showlineposts2" : "showlineposts1";
            table.getRowFormatter().setStyleName(row, style);
            row++;
        }
        table.setHTML(row, 0, messages.sum());
        table.setText(row, 2, sumFond);
        table.getCellFormatter().setStyleName(row, 2, "right");
        table.setText(row, 3, sumClub);
        table.getCellFormatter().setStyleName(row, 3, "right");
        table.getRowFormatter().setStyleName(row, "sumline");
        row++;
    }

    public void onClick(Widget sender) {
        if (editFields == null) {
            editFields = new TrustEditFields();
        }

        int left = 0;
        left = sender.getAbsoluteLeft() + 10;

        int top = sender.getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        editFields.init();
        editFields.show();
    }

    class TrustEditFields extends DialogBox implements ClickListener,
            ChangeListener {

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
            registerStandards = new RegisterStandards(constants, messages);

            setText(messages.new_trust());
            FlexTable edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, messages.trust());
            edittable.setHTML(1, 0, messages.action());
            edittable.setHTML(3, 0, messages.date());
            edittable.setHTML(4, 0, messages.postnmb());
            edittable.setHTML(5, 0, messages.attachment());
            edittable.setHTML(6, 0, messages.description());
            edittable.setHTML(7, 0, messages.amount());

            actionListBox = new ListBoxWithErrorText(
                    "TrustStatusView.actionList");
            actionListBox.getListbox().addChangeListener(this);

            trustListBox = new ListBoxWithErrorText("TrustStatuView.trustList");
            trustListBox.getListbox().setVisibleItemCount(1);
            trustListBox.getListbox().addChangeListener(this);

            TrustActionCache trustActionCache = TrustActionCache
                    .getInstance(constants);
            trustActionCache.fillTrustList(trustListBox.getListbox());

            edittable.setWidget(0, 1, trustListBox);
            edittable.setWidget(1, 1, actionListBox);

            errorLabelForDate = new HTML();

            dayBox = registerStandards.createDayBox(errorLabelForDate);

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

            saveButton = new NamedButton("TrustStatusView.saveButton", messages
                    .save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton("TrustStatusView.cancelButton",
                    messages.cancel());
            cancelButton.addClickListener(this);

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

        public void onClick(Widget sender) {
            if (sender == cancelButton) {
                hide();
            } else if (sender == saveButton && validateFields()) {
                doSave();
            }
        }

        private void doSave() {

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
            mv.mandatory(messages.required_field(), new Widget[] { dayBox,
                    monthBox, yearBox, actionListBox, trustListBox, descBox,
                    amountBox });

            mv.day(messages.illegal_day(), Util.getInt(yearBox.getText()), Util
                    .getInt(monthBox.getText()), new Widget[] { dayBox });

            mv.money(messages.field_money(), new Widget[] { amountBox });

            return mv.validateStatus();
        }

        public void onChange(Widget sender) {
            TrustActionCache trustActionCache = TrustActionCache
                    .getInstance(constants);

            if (sender == this.trustListBox.getListbox()) {
                ListBox listBox = (ListBox) sender;

                String selected = Util.getSelected(listBox);
                trustActionCache.fillActionList(actionListBox.getListbox(),
                        selected);
            }

            if (sender == this.actionListBox.getListbox()) {
                String selected = Util.getSelected(actionListBox.getListbox());

                if ("".equals(selected)) {
                    return;
                }

                trustActionCache.fillDefaultDesc(descBox, selected);

                boolean addsAccountLine = trustActionCache
                        .addsAccountLineUponSave(selected);

                if (addsAccountLine) {
                    monthBox.setText(registerStandards.getCurrentMonth());
                    yearBox.setText(registerStandards.getCurrentYear());
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
