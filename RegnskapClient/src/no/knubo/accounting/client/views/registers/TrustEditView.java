package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.cache.CacheCallback;
import no.knubo.accounting.client.cache.TrustActionCache;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.json.client.JSONArray;
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
import com.google.gwt.user.client.ui.Widget;

public class TrustEditView extends Composite implements ClickListener, CacheCallback {

    private static TrustEditView me;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    private IdHolder idHolder;

    private Button newButton;

    private TrustEditFields editFields;

    private TrustActionCache trustCache;

    private final HelpPanel helpPanel;

    private final Elements elements;

    public TrustEditView(I18NAccount messages, Constants constants, HelpPanel helpPanel,
            Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.trust());
        table.setHTML(0, 1, elements.description());
        table.getRowFormatter().setStyleName(0, "header");
        table.getFlexCellFormatter().setColSpan(0, 1, 2);

        newButton = new NamedButton("trustEditView_newButton", elements.trustEditView_newButton());
        newButton.addClickListener(this);

        dp.add(newButton, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        idHolder = new IdHolder();
        initWidget(dp);
    }

    public static TrustEditView show(I18NAccount messages, Constants constants,
            HelpPanel helpPanel, Elements elements) {
        if (me == null) {
            me = new TrustEditView(messages, constants, helpPanel, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(Widget sender) {
        if (editFields == null) {
            editFields = new TrustEditFields();
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
        trustCache = TrustActionCache.getInstance(constants, messages);
        idHolder.init();

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        JSONArray trusts = trustCache.getAllTrust();
        for (int pos = 0; pos < trusts.size(); pos++) {
            JSONObject object = trusts.get(pos).isObject();

            String trust = Util.str(object.get("description"));
            String id = Util.str(object.get("fond"));

            addRow(pos + 1, trust, id);
        }
        helpPanel.resize(this);
    }

    private void addRow(int row, String trust, String id) {
        table.setHTML(row, 0, id);
        table.setHTML(row, 1, trust);
        table.getCellFormatter().setStyleName(row, 0, "desc");
        table.getCellFormatter().setStyleName(row, 1, "desc");

        Image editImage = ImageFactory.editImage("trustEditView_editImage");
        editImage.addClickListener(me);
        idHolder.add(id, editImage);

        table.setWidget(row, 2, editImage);

        String style = (((row + 1) % 6) < 3) ? "line2" : "line1";
        table.getRowFormatter().setStyleName(row, style);
    }

    class TrustEditFields extends DialogBox implements ClickListener {
        private TextBoxWithErrorText trustBox;

        private Button saveButton;

        private Button cancelButton;

        private HTML mainErrorLabel;

        private TextBoxWithErrorText descBox;

        TrustEditFields() {
            setText(elements.trust());
            FlexTable edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, elements.trust());
            trustBox = new TextBoxWithErrorText("trust");
            trustBox.setMaxLength(3);
            trustBox.setVisibleLength(3);
            edittable.setWidget(0, 1, trustBox);

            edittable.setHTML(1, 0, elements.description());
            descBox = new TextBoxWithErrorText("description");
            descBox.setMaxLength(50);
            descBox.setVisibleLength(50);
            edittable.setWidget(1, 1, descBox);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("trustEditView_saveButton", elements.save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton("trustEditView_cancelButton", elements.cancel());
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

        public void init(String id) {
            String trust = TrustActionCache.getInstance(constants, messages).trustGivesDesc(id);

            trustBox.setText(id);
            descBox.setText(trust);
            trustBox.setEnabled(false);
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
            final String sendId = trustBox.getText();
            final String description = descBox.getText();

            Util.addPostParam(sb, "fond", sendId);
            Util.addPostParam(sb, "description", description);

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue parse) {
                    JSONObject resobj = parse.isObject();
                    String result = Util.str(resobj.get("result"));

                    if (result.equals("1")) {
                        /* Could probably be more effective but why bother? */
                        TrustActionCache.getInstance(constants, messages).flush(me);
                    } else {
                        mainErrorLabel.setHTML(messages.save_failed());
                        Util.timedMessage(mainErrorLabel, "", 5);
                    }
                    hide();
                }
            };

            AuthResponder.post(constants, messages, callback, sb, "registers/trust.php");

        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            Widget[] widgets = new Widget[] { trustBox, descBox };
            mv.mandatory(messages.required_field(), widgets);
            return mv.validateStatus();
        }

        public void init() {
            trustBox.setText("");
            trustBox.setEnabled(true);
        }
    }

    public void flushCompleted() {
        me.init();
    }
}
