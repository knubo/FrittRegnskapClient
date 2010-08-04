package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedCheckBox;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class AdminInstallsView extends Composite implements ClickHandler {
    private static AdminInstallsView me;
    private final I18NAccount messages;
    private final Constants constants;
    private FlexTable table;
    private final Elements elements;
    private AdminInstallEditFields editFields;

    public static AdminInstallsView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminInstallsView(messages, constants, elements);
        }
        return me;
    }

    public AdminInstallsView(I18NAccount messages, Constants constants, Elements elements) {

        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.admin_installs());
        table.getFlexCellFormatter().setColSpan(0, 0, 8);

        table.setHTML(1, 0, elements.admin_hostprefix());
        table.setHTML(1, 1, elements.admin_dbprefix());
        table.setHTML(1, 2, elements.admin_database());
        table.setHTML(1, 3, elements.description());
        table.setHTML(1, 4, elements.admin_wikilogin());
        table.setHTML(1, 5, elements.admin_diskqvota());
        table.setHTML(1, 6, "Beta");
        table.setHTML(1, 7, "");
        table.getRowFormatter().setStyleName(0, "header");
        table.getRowFormatter().setStyleName(1, "header");
        initWidget(table);
    }

    public void init() {
        while (table.getRowCount() > 2) {
            table.removeRow(2);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                fillInstalls(responseObj.isArray());
            }
        };
        AuthResponder.get(constants, messages, callback, "admin/installs.php?action=list");

    }

    protected void fillInstalls(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();
            table.setText(i + 2, 0, Util.str(obj.get("hostprefix")));
            table.setText(i + 2, 1, Util.str(obj.get("dbprefix")));
            table.setText(i + 2, 2, Util.str(obj.get("db")));
            table.setText(i + 2, 3, Util.str(obj.get("description")));
            table.setText(i + 2, 4, Util.str(obj.get("wikilogin")));
            table.setText(i + 2, 5, Util.str(obj.get("diskquota")) + " MB");
            table.setText(i + 2, 6, "" + Util.getBoolean(obj.get("beta")));
            Image image = ImageFactory.editImage("edit" + Util.str(obj.get("id")));
            image.addClickHandler(this);
            table.setWidget(i + 2, 7, image);

            table.getCellFormatter().addStyleName(i + 2, 5, "right");

            String style = (((i + 2) % 6) < 3) ? "line2" : "line1";
            table.getRowFormatter().setStyleName(i + 2, style + " desc");
        }
    }

    public void onClick(ClickEvent event) {
        Image image = (Image) event.getSource();

        String idWithEdit = DOM.getElementAttribute(image.getElement(), "id");

        if (editFields == null) {
            editFields = new AdminInstallEditFields();
        }

        int left = event.getRelativeElement().getAbsoluteLeft() - 250;

        int top = event.getRelativeElement().getAbsoluteTop() + 10;
        editFields.setPopupPosition(left, top);

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                editFields.setEditData(responseObj.isObject());
                editFields.show();
            }
        };
        AuthResponder.get(constants, messages, callback, "admin/installs.php?action=get&id=" + idWithEdit.substring(4));

    }

    class AdminInstallEditFields extends DialogBox implements ClickHandler {
        private TextBoxWithErrorText descriptionBox;

        private Button saveButton;

        private Button cancelButton;

        private String currentId;

        private HTML mainErrorLabel;

        private TextBoxWithErrorText hostprefixBox;

        private TextBoxWithErrorText diskQvotaBox;

        private NamedCheckBox betaBox;

        private FlexTable edittable;

        private NamedButton deleteButton;

        private TextBoxWithErrorText wikiLogin;

        AdminInstallEditFields() {
            setText(elements.project());
            edittable = new FlexTable();
            edittable.setStyleName("edittable");

            edittable.setHTML(0, 0, elements.admin_hostprefix());
            edittable.setHTML(1, 0, elements.admin_dbprefix());
            edittable.setHTML(2, 0, elements.admin_database());
            edittable.setHTML(3, 0, elements.description());
            edittable.setHTML(4, 0, elements.admin_wikilogin());
            edittable.setHTML(5, 0, elements.admin_diskqvota());
            edittable.setHTML(6, 0, "Beta");

            hostprefixBox = new TextBoxWithErrorText("hostprefix");
            hostprefixBox.setMaxLength(40);
            hostprefixBox.setVisibleLength(40);
            descriptionBox = new TextBoxWithErrorText("description");
            descriptionBox.setMaxLength(80);
            descriptionBox.setVisibleLength(80);
            diskQvotaBox = new TextBoxWithErrorText("qvota");
            diskQvotaBox.setMaxLength(4);
            diskQvotaBox.setVisibleLength(4);
            wikiLogin = new TextBoxWithErrorText("wikilogin");

            betaBox = new NamedCheckBox("beta");

            edittable.setWidget(0, 1, hostprefixBox);
            edittable.setWidget(3, 1, descriptionBox);
            edittable.setWidget(4, 1, wikiLogin);
            edittable.setWidget(5, 1, diskQvotaBox);
            edittable.setWidget(6, 1, betaBox);
            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("adminInstall_saveButton", elements.save());
            saveButton.addClickHandler(this);
            cancelButton = new NamedButton("adminInstall_cancelButton", elements.cancel());
            cancelButton.addClickHandler(this);

            deleteButton = new NamedButton("adminInstall_deleteButton", elements.admin_delete_accounting());
            deleteButton.addClickHandler(this);
            mainErrorLabel = new HTML();
            mainErrorLabel.setStyleName("error");

            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(mainErrorLabel);
            dp.add(buttonPanel, DockPanel.NORTH);
            setWidget(dp);
        }

        public void setEditData(JSONObject obj) {
            hostprefixBox.setText(Util.str(obj.get("hostprefix")));
            edittable.setText(1, 1, Util.str(obj.get("dbprefix")));
            edittable.setText(2, 1, Util.str(obj.get("db")));
            descriptionBox.setText(Util.str(obj.get("description")));
            wikiLogin.setText(Util.str(obj.get("wikilogin")));
            diskQvotaBox.setText(Util.str(obj.get("diskquota")));
            betaBox.setValue(Util.getBoolean(obj.get("beta")));

            currentId = Util.str(obj.get("id"));

        }

        public void onClick(ClickEvent event) {
            Widget sender = (Widget) event.getSource();
            if (sender == cancelButton) {
                hide();
            } else if (sender == saveButton && validateFields()) {
                doSave();
            } else if (sender == deleteButton) {
                doDelete();
            }
        }

        private void doDelete() {
            boolean choice = Window.confirm(messages.confirm_delete());

            if (choice) {
                ServerResponse callback = new ServerResponse() {

                    public void serverResponse(JSONValue responseObj) {
                        hide();
                    }
                };
                AuthResponder.get(constants, messages, callback, "admin/installs.php?action=deleterequest&id="
                        + this.currentId);
            }

        }

        private void doSave() {
            StringBuffer sb = new StringBuffer();
            sb.append("action=save");
            final String description = descriptionBox.getText();

            Util.addPostParam(sb, "id", currentId);
            Util.addPostParam(sb, "description", description);
            Util.addPostParam(sb, "quota", diskQvotaBox.getText());
            Util.addPostParam(sb, "beta", betaBox.getValue() ? "1" : "0");
            Util.addPostParam(sb, "hostprefix", hostprefixBox.getText());
            Util.addPostParam(sb, "wikilogin", wikiLogin.getText());

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue value) {
                    JSONObject object = value.isObject();

                    if ("0".equals(object.get("result"))) {
                        mainErrorLabel.setHTML(messages.save_failed());
                        Util.timedMessage(mainErrorLabel, "", 5);
                    } else {
                        hide();
                        init();
                    }
                }
            };

            AuthResponder.post(constants, messages, callback, sb, "admin/installs.php");
        }

        private boolean validateFields() {
            MasterValidator mv = new MasterValidator();
            mv.mandatory(messages.required_field(), descriptionBox, hostprefixBox, diskQvotaBox, wikiLogin);
            mv.range(messages.field_positive(), 0, Integer.MAX_VALUE, diskQvotaBox);
            return mv.validateStatus();
        }

    }

}
