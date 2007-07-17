package no.knubo.accounting.client.views;

import java.util.HashMap;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
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

public class UsersEditView extends Composite implements ClickListener {

    private static UsersEditView me;

    private final Constants constants;

    private final I18NAccount messages;

    private FlexTable table;

    private IdHolder idHolder;

    private Button newButton;

    private UserEditFields editFields;

    private HashMap objectPerUsername;

    public UsersEditView(I18NAccount messages, Constants constants) {
        this.messages = messages;
        this.constants = constants;

        DockPanel dp = new DockPanel();

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, messages.title_user_adm());
        table.getRowFormatter().setStyleName(0, "header");
        table.getFlexCellFormatter().setColSpan(0, 0, 3);

        table.setHTML(1, 0, messages.user());
        table.setHTML(1, 1, messages.name());
        table.setHTML(1, 2, "");
        table.getRowFormatter().setStyleName(1, "header");

        newButton = new NamedButton("userEditView.newButton", messages
                .userEditView_newButton());
        newButton.addClickListener(this);

        dp.add(newButton, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);

        idHolder = new IdHolder();
        initWidget(dp);
    }

    public static UsersEditView show(I18NAccount messages, Constants constants) {
        if (me == null) {
            me = new UsersEditView(messages, constants);
        }
        me.setVisible(true);
        return me;
    }

    public void onClick(Widget sender) {
        if (editFields == null) {
            editFields = new UserEditFields();
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

        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        idHolder.init();
        objectPerUsername = new HashMap();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "registers/users.php?action=all");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue value = JSONParser.parse(responseText);

                JSONArray array = value.isArray();
                int row = 2;
                for (int i = 0; i < array.size(); i++) {

                    JSONObject object = array.get(i).isObject();

                    String username = Util.str(object.get("username"));
                    String name = Util.str(object.get("name"));

                    objectPerUsername.put(username, object);

                    addRow(row++, username, name);
                }

            }
        };

        try {
            builder.sendRequest("", new AuthResponder(constants, callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }
    }

    private void addRow(int row, String username, String name) {
        table.setHTML(row, 0, username);
        table.setHTML(row, 1, name);
        Image editImage = ImageFactory.editImage("userEditView_editImage");
        editImage.addClickListener(me);
        idHolder.add(username, editImage);

        table.setWidget(row, 2, editImage);

        String style = (row % 2 == 0) ? "showlineposts2" : "showlineposts1";
        table.getRowFormatter().setStyleName(row, style);
    }

    class UserEditFields extends DialogBox implements ClickListener {
        private TextBoxWithErrorText userBox;

        private Button saveButton;

        private Button cancelButton;

        private HTML mainErrorLabel;

        private TextBoxWithErrorText passwordBox;

        private FlexTable edittable;

        private String personId;

        private TextBoxWithErrorText personBox;

        UserEditFields() {
            setText(messages.project());
            edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, messages.title_edit_new_user());
            userBox = new TextBoxWithErrorText("user");
            userBox.setMaxLength(12);
            userBox.setWidth("12em");

            passwordBox = new TextBoxWithErrorText("password");
            passwordBox.setWidth("12em");

            personBox = new TextBoxWithErrorText("name");
            personBox.setVisibleLength(40);
            personBox.setEnabled(false);

            edittable.setText(1, 0, messages.user());
            edittable.setWidget(1, 1, userBox);
            edittable.setText(2, 0, messages.password());
            edittable.setWidget(2, 1, passwordBox);
            edittable.setText(3, 0, messages.name());
            edittable.setWidget(3, 1, personBox);
            Image searchImage = ImageFactory.searchImage("search_person");
            searchImage.addClickListener(this);
            edittable.setWidget(3, 2, searchImage);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("userEditView_saveButton", messages
                    .save());
            saveButton.addClickListener(this);
            cancelButton = new NamedButton("usertEditView_cancelButton",
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

        public void init(String username) {
            JSONObject object = (JSONObject) objectPerUsername.get(username);
            personId = Util.str(object.get("person"));

            userBox.setText(username);
            personBox.setText(Util.str(object.get("name")));
            passwordBox.setText("");
            userBox.setEnabled(false);
        }

        public void init() {
            personId = null;
            passwordBox.setText("");
            userBox.setText("");
            userBox.setEnabled(true);
            personBox.setText("");
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

            Util.addPostParam(sb, "username", userBox.getText());
            Util.addPostParam(sb, "password", passwordBox.getText());
            Util.addPostParam(sb, "person", personId);

            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                    constants.baseurl() + "registers/users.php");

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(String responseText) {
                    // Util.timedMessage(mainErrorLabel, "", 5);
                    // addRow(row, description, sendId);
                    hide();
                    // TODO Auto-generated method stub
                }
            };

            try {
                builder.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                builder.sendRequest(sb.toString(), new AuthResponder(constants,
                        callback));
            } catch (RequestException e) {
                Window.alert("Failed to send the request: " + e.getMessage());
            }

        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            Widget[] widgets = new Widget[] { userBox, personBox };
            mv.mandatory(messages.required_field(), widgets);

            return mv.validateStatus();
        }
    }
}