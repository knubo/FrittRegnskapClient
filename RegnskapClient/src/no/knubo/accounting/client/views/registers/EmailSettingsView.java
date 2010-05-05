package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class EmailSettingsView extends Composite implements ClickHandler {
    private static EmailSettingsView me;
    private I18NAccount messages;
    private Constants constants;
    private FlexTable tableHeader;
    private NamedButton newHeaderButton;
    private IdHolder<Integer, Image> idHolder;
    private final Elements elements;
    private EmailSettingsView.HeaderFooterEditFields editFields;
    private FlexTable tableFooter;
    private NamedButton newFooterButton;

    public EmailSettingsView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        DockPanel dp = new DockPanel();

        tableHeader = new FlexTable();
        tableHeader.setStyleName("tableborder");
        tableHeader.setText(0, 0, elements.email_header());
        tableHeader.getFlexCellFormatter().setColSpan(0, 0, 2);
        tableHeader.getRowFormatter().setStyleName(0, "header");
        tableHeader.setText(1, 0, elements.name());
        tableHeader.getRowFormatter().setStyleName(1, "header");
        tableHeader.setText(1, 1, "");

        newHeaderButton = new NamedButton("emailsettings_new_header", elements.new_email_header());
        newHeaderButton.addClickHandler(this);

        dp.add(newHeaderButton, DockPanel.NORTH);
        dp.add(tableHeader, DockPanel.NORTH);

        tableFooter = new FlexTable();
        tableFooter.setStyleName("tableborder");
        tableFooter.setText(0, 0, elements.email_footer());
        tableFooter.getFlexCellFormatter().setColSpan(0, 0, 2);
        tableFooter.getRowFormatter().setStyleName(0, "header");
        tableFooter.setText(1, 0, elements.name());
        tableFooter.getRowFormatter().setStyleName(1, "header");
        tableFooter.setText(1, 1, "");

        newFooterButton = new NamedButton("emailsettings_new_footer", elements.new_email_footer());
        newFooterButton.addClickHandler(this);

        dp.add(newFooterButton, DockPanel.NORTH);
        dp.add(tableFooter, DockPanel.NORTH);
        idHolder = new IdHolder<Integer, Image>();
        initWidget(dp);
    }

    public static EmailSettingsView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new EmailSettingsView(messages, constants, elements);
        }
        me.setVisible(true);
        return me;
    }

    public void init() {
        idHolder.init();
        while (tableHeader.getRowCount() > 1) {
            tableHeader.removeRow(1);
        }
        while (tableFooter.getRowCount() > 1) {
            tableFooter.removeRow(1);
        }

        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                fill(tableFooter, object.get("footers"));
                fill(tableHeader, object.get("headers"));
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/emailcontent.php?action=setup_init");
    }

    protected void fill(FlexTable table, JSONValue jsonValue) {
        if (jsonValue == null) {
            return;
        }
        JSONArray array = jsonValue.isArray();

        if (array == null) {
            return;
        }

        for (int i = 0; i < array.size(); i++) {
            JSONObject one = array.get(i).isObject();

            Integer id = Util.getInt(one.get("id"));
            String name = Util.str(one.get("name"));

            int row = table.getRowCount();
            table.setText(row, 0, name);
            table.getCellFormatter().setStyleName(row, 0, "desc");

            String style = (((row + 2) % 6) < 3) ? "line2" : "line1";
            table.getRowFormatter().setStyleName(row, style);

            Image editImage = ImageFactory.editImage("edit" + id);
            editImage.addClickHandler(this);
            table.setWidget(row, 1, editImage);
            idHolder.add(id, editImage);
        }
    }

    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();

        if (editFields == null) {
            editFields = new HeaderFooterEditFields();
        }

        int left = sender.getAbsoluteLeft() + 10;
        int top = sender.getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        if (sender == newHeaderButton || sender == newFooterButton) {
            editFields.init(sender == newHeaderButton);
        } else {
            int id = idHolder.findId(sender);

            editFields.init(id);
        }
        editFields.show();
    }

    class HeaderFooterEditFields extends DialogBox implements ClickHandler {
        private TextBoxWithErrorText nameBox;

        private Button saveButton;
        private Button cancelButton;
        private HTML mainErrorLabel;
        private FlexTable edittable;
        private NamedTextArea textBox;
        private boolean isHeader;

        private int id;

        HeaderFooterEditFields() {
            setText(elements.edit_text());
            edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, elements.name());
            edittable.setHTML(1, 0, elements.text());

            nameBox = new TextBoxWithErrorText("email_name");
            nameBox.setMaxLength(40);
            nameBox.setVisibleLength(40);

            textBox = new NamedTextArea("email_text");
            textBox.setCharacterWidth(80);
            textBox.setHeight("15em");

            edittable.setWidget(0, 1, nameBox);
            edittable.setWidget(1, 1, textBox);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("emailSettingsView_saveButton", elements.save());
            saveButton.addClickHandler(this);
            cancelButton = new NamedButton("emailSettingsView_cancelButton", elements.cancel());
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
            sb.append("action=setup_save");

            Util.addPostParam(sb, "id", String.valueOf(id));
            Util.addPostParam(sb, "name", nameBox.getText());
            Util.addPostParam(sb, "text", textBox.getText());
            Util.addPostParam(sb, "header", isHeader ? "1" : "0");

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue value) {
                    JSONObject object = value.isObject();

                    if ("0".equals(Util.str(object.get("result")))) {
                        mainErrorLabel.setText(messages.save_failed());
                        Util.timedMessage(mainErrorLabel, "", 10);
                    } else {
                        me.init();
                        hide();
                    }
                }
            };

            AuthResponder.post(constants, messages, callback, sb, "registers/emailcontent.php?action=setup_save");
        }

        private void init(boolean isHeader) {
            this.isHeader = isHeader;
            this.id = 0;
            textBox.setText("");
            nameBox.setText("");
            mainErrorLabel.setText("");
        }

        private void init(final int id) {
            this.id = id;
            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue responseObj) {
                    JSONObject one = responseObj.isObject();

                    textBox.setText(Util.str(one.get("content")));
                    nameBox.setText(Util.str(one.get("name")));
                }
            };
            AuthResponder.get(constants, messages, callback, "registers/emailcontent.php?action=setup_get&id=" + id);
            mainErrorLabel.setText("");
        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            Widget[] widgets = new Widget[] { nameBox, textBox };
            mv.mandatory(messages.required_field(), widgets);
            return mv.validateStatus();
        }

    }

}
