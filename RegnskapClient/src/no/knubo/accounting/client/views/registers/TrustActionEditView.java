package no.knubo.accounting.client.views.registers;


import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CacheCallback;
import no.knubo.accounting.client.cache.PosttypeCache;
import no.knubo.accounting.client.cache.TrustActionCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class TrustActionEditView extends Composite implements ClickListener, CacheCallback {

    private static TrustActionEditView me;

    private final Constants constants;
    private final HelpPanel helpPanel;
    private final Elements elements;
    private final I18NAccount messages;

    private FlexTable table;

    private IdHolder<String, Image> idHolder;

    private Button newButton;

    private TrustActionEditFields editFields;
    private TrustActionCache trustActionCache;
    private PosttypeCache posttypeCache;

    public TrustActionEditView(I18NAccount messages, Constants constants, HelpPanel helpPanel,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.trust());
        table.setText(0, 1, elements.description());
        table.setText(0, 2, elements.trust_default_desc());
        table.setText(0, 3, elements.trust_actionclub());
        table.setText(0, 4, elements.trust_actiontrust());
        table.setText(0, 5, elements.trust_debetpost());
        table.setText(0, 6, elements.trust_creditpost());
        table.setText(0, 7, "");
        table.getRowFormatter().setStyleName(0, "header");

        newButton = new NamedButton("new_trust", elements.new_trust());
        newButton.addClickListener(this);

        dp.add(newButton, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        idHolder = new IdHolder<String, Image>();
        initWidget(dp);
    }

    public static TrustActionEditView show(I18NAccount messages, Constants constants,
            HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new TrustActionEditView(messages, constants, helpPanel, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(Widget sender) {
        if (editFields == null) {
            editFields = new TrustActionEditFields();
        }

        int left = 0;
        if (sender == newButton) {
            left = sender.getAbsoluteLeft() + 10;
        } else {
            left = sender.getAbsoluteLeft() - 250;
        }

        int top = sender.getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        if (sender == newButton) {
            editFields.init();
        } else {
            editFields.init(idHolder.findId(sender));
        }
        editFields.show();
    }

    public void init() {
        trustActionCache = TrustActionCache.getInstance(constants, messages);
        posttypeCache = PosttypeCache.getInstance(constants, messages);

        idHolder.init();

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        int row = 1;
        for (JSONObject object : trustActionCache.getAll()) {

            addRow(object, row);
            row++;
        }

        helpPanel.resize(this);
    }

    private void addRow(JSONObject object, int row) {
        String desc = Util.str(object.get("description"));
        String id = Util.str(object.get("id"));
        String trust = Util.str(object.get("fond"));
        String defaultdesc = Util.str(object.get("defaultdesc"));
        String actionclub = Util.str(object.get("actionclub"));
        String actiontrust = Util.str(object.get("actionfond"));
        String debetpost = Util.strSkipNull(object.get("debetpost"));
        String creditpost = Util.strSkipNull(object.get("creditpost"));

        table.setText(row, 0, trustActionCache.trustGivesDesc(trust));
        table.setText(row, 1, desc);
        table.setText(row, 2, defaultdesc);
        table.setText(row, 3, Util.debkred(elements, actionclub));
        table.setText(row, 4, Util.debkred(elements, actiontrust));

        if (!("".equals(debetpost))) {
            table.setText(row, 5, posttypeCache.getDescriptionWithType(debetpost));
        }
        if (!("".equals(creditpost))) {
            table.setText(row, 6, posttypeCache.getDescriptionWithType(creditpost));
        }
        table.getCellFormatter().setStyleName(row, 0, "desc");
        table.getCellFormatter().setStyleName(row, 1, "desc");
        table.getCellFormatter().setStyleName(row, 2, "desc");
        table.getCellFormatter().setStyleName(row, 5, "desc");
        table.getCellFormatter().setStyleName(row, 6, "desc");

        Image editImage = ImageFactory.editImage("projectEditView_editImage");
        editImage.addClickListener(me);
        idHolder.add(id, editImage);

        table.setWidget(row, 7, editImage);

        String style = (((row + 1) % 6) < 3) ? "line2" : "line1";
        table.getRowFormatter().setStyleName(row, style);
    }

    class TrustActionEditFields extends DialogBox implements ClickListener {
        private TextBoxWithErrorText descBox;

        private Button saveButton;

        private Button cancelButton;

        private String currentId;

        private HTML mainErrorLabel;

        private ListBoxWithErrorText trustBox;

        private TextBoxWithErrorText defaultDescBox;

        private ListBox actionClubBox;

        private ListBox actionTrustBox;

        private TextBoxWithErrorText accountCredIdBox;

        private ListBox accountCredNameBox;

        private TextBoxWithErrorText accountDebIdBox;

        private ListBox accountDebNameBox;

        TrustActionEditFields() {
            setText(elements.project());
            FlexTable edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setText(0, 0, elements.trust());
            trustBox = new ListBoxWithErrorText("trust");
            trustActionCache.fillTrustList(trustBox.getListbox());
            edittable.setWidget(0, 1, trustBox);

            edittable.setText(1, 0, elements.description());
            descBox = new TextBoxWithErrorText("description");
            descBox.setMaxLength(40);
            descBox.setVisibleLength(40);
            edittable.setWidget(1, 1, descBox);

            edittable.setText(2, 0, elements.trust_default_desc());
            defaultDescBox = new TextBoxWithErrorText("trust_default_desc");
            defaultDescBox.setMaxLength(50);
            defaultDescBox.setVisibleLength(50);
            edittable.setWidget(2, 1, defaultDescBox);

            edittable.setText(3, 0, elements.trust_actionclub());
            actionClubBox = new ListBox();
            addDebetKredit(actionClubBox);
            edittable.setWidget(3, 1, actionClubBox);

            edittable.setText(4, 0, elements.trust_actiontrust());
            actionTrustBox = new ListBox();
            addDebetKredit(actionTrustBox);
            edittable.setWidget(4, 1, actionTrustBox);

            edittable.setText(5, 0, elements.trust_creditpost());
            HorizontalPanel hpcred = new HorizontalPanel();

            HTML errorAccountCredHtml = new HTML();
            accountCredIdBox = new TextBoxWithErrorText("account", errorAccountCredHtml);
            accountCredIdBox.setVisibleLength(6);
            accountCredNameBox = new ListBox();
            accountCredNameBox.setVisibleItemCount(1);

            hpcred.add(accountCredIdBox);
            hpcred.add(accountCredNameBox);
            hpcred.add(errorAccountCredHtml);

            PosttypeCache.getInstance(constants, messages).fillAllPosts(accountCredNameBox);
            Util.syncListbox(accountCredNameBox, accountCredIdBox.getTextBox());

            edittable.setWidget(5, 1, hpcred);

            edittable.setText(6, 0, elements.trust_debetpost());
            HorizontalPanel hpdeb = new HorizontalPanel();

            HTML errorAccountDebHtml = new HTML();
            accountDebIdBox = new TextBoxWithErrorText("account", errorAccountDebHtml);
            accountDebIdBox.setVisibleLength(6);
            accountDebNameBox = new ListBox();
            accountDebNameBox.setVisibleItemCount(1);

            hpdeb.add(accountDebIdBox);
            hpdeb.add(accountDebNameBox);
            hpdeb.add(errorAccountDebHtml);

            PosttypeCache.getInstance(constants, messages).fillAllPosts(accountDebNameBox);
            Util.syncListbox(accountDebNameBox, accountDebIdBox.getTextBox());

            edittable.setWidget(6, 1, hpdeb);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("projectEditView_saveButton", elements.save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton("projectEditView_cancelButton", elements.cancel());
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

        private void addDebetKredit(ListBox listBox) {
            listBox.setVisibleItemCount(1);
            listBox.addItem("", "0");
            listBox.addItem(elements.debet(), "1");
            listBox.addItem(elements.kredit(), "-1");
        }

        public void init(String id) {
            currentId = id;

            JSONObject object = trustActionCache.getTrustAction(id);

            String trust = Util.str(object.get("fond"));
            String desc = Util.str(object.get("description"));
            String defaultdesc = Util.str(object.get("defaultdesc"));
            String actionclub = Util.str(object.get("actionclub"));
            String actiontrust = Util.str(object.get("actionfond"));
            String debetpost = Util.strSkipNull(object.get("debetpost"));
            String creditpost = Util.strSkipNull(object.get("creditpost"));

            Util.setIndexByValue(trustBox.getListbox(), trust);
            descBox.setText(desc);
            defaultDescBox.setText(defaultdesc);
            accountCredIdBox.setText(creditpost);
            Util.setIndexByValue(accountCredNameBox, creditpost);
            accountDebIdBox.setText(debetpost);
            Util.setIndexByValue(accountDebNameBox, debetpost);
            Util.setIndexByValue(actionClubBox, actionclub);
            Util.setIndexByValue(actionTrustBox, actiontrust);
        }

        public void onClick(Widget sender) {
            if (sender == cancelButton) {
                hide();
            } else if (sender == saveButton && validateFields()) {
                doSave();
            }
        }

        private void doSave() {
            StringBuffer sb = new StringBuffer();
            sb.append("action=save");

            Util.addPostParam(sb, "id", currentId);
            Util.addPostParam(sb, "trust", trustBox.getText());
            Util.addPostParam(sb, "description", descBox.getText());
            Util.addPostParam(sb, "defaultdesc", defaultDescBox.getText());
            Util.addPostParam(sb, "clubaction", Util.getSelected(actionClubBox));
            Util.addPostParam(sb, "trustaction", Util.getSelected(actionTrustBox));
            Util.addPostParam(sb, "debetpost", accountDebIdBox.getText());
            Util.addPostParam(sb, "creditpost", accountCredIdBox.getText());

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue value) {
                    JSONObject obj = value.isObject();

                    String serverResponse = Util.str(obj.get("result"));

                    if ("0".equals(serverResponse)) {
                        mainErrorLabel.setText(messages.save_failed());
                        Util.timedMessage(mainErrorLabel, "", 5);
                    } else {
                        /* Could probably be more effective, but why bother? */
                        TrustActionCache.getInstance(constants, messages).flush(me);

                        hide();
                    }
                }
            };

            AuthResponder.post(constants, messages, callback, sb, "registers/trustaction.php");

        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            Widget[] widgets = new Widget[] { descBox, defaultDescBox };

            Widget[] idWidgets = new Widget[] { accountDebIdBox, accountCredIdBox };

            if (accountDebIdBox.getText().length() > 0 || accountCredIdBox.getText().length() > 0) {
                mv.mandatory(messages.required_field(), idWidgets);
            }

            mv.registry(messages.registry_invalid_key(), PosttypeCache.getInstance(constants,
                    messages), idWidgets);

            mv.mandatory(messages.required_field(), widgets);
            return mv.validateStatus();
        }

        public void init() {
            currentId = null;
            trustBox.setSelectedIndex(0);
            descBox.setText("");
            defaultDescBox.setText("");
            accountCredIdBox.setText("");
            accountCredNameBox.setSelectedIndex(0);
            accountDebIdBox.setText("");
            accountDebNameBox.setSelectedIndex(0);
            actionClubBox.setSelectedIndex(0);
            actionTrustBox.setSelectedIndex(0);
        }
    }

    public void flushCompleted() {
        me.init();
    }
}
