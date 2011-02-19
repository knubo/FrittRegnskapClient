package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AdminInstallsView extends Composite implements ClickHandler {
    private static AdminInstallsView me;
    private final I18NAccount messages;
    private final Constants constants;
    private FlexTable table;
    private final Elements elements;
    private AdminInstallEditFields editFields;
    private NamedButton newButton;

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
        table.getFlexCellFormatter().setColSpan(0, 0, 13);

        table.setHTML(1, 0, elements.admin_hostprefix());
        table.setHTML(1, 1, elements.admin_dbprefix());
        table.setHTML(1, 2, elements.admin_database());
        table.setHTML(1, 3, elements.description());
        table.setHTML(1, 4, elements.admin_wikilogin());
        table.setHTML(1, 5, elements.admin_diskqvota());
        table.setHTML(1, 6, "Beta");
        table.setHTML(1, 7, elements.status());
        table.setHTML(1, 8, elements.portal_title());
        table.setHTML(1, 9, elements.admin_archive_limit());
        table.setHTML(1, 10, elements.admin_reduced_mode());
        table.setHTML(1, 11, elements.admin_parentdbprefix());
        table.setHTML(1, 12, "");
        table.getRowFormatter().setStyleName(0, "header");
        table.getRowFormatter().setStyleName(1, "header");

        VerticalPanel vp = new VerticalPanel();
        newButton = new NamedButton("new_install", elements.new_install());
        newButton.addClickHandler(this);
        vp.add(newButton);
        vp.add(table);
        initWidget(vp);
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

            String hostprefix = Util.str(obj.get("hostprefix"));
            table.setWidget(i + 2, 0, new Anchor(hostprefix, "http://" + hostprefix
                    + ".frittregnskap.no/prg/AccountingGWT.html", "_blank"));
            table.setText(i + 2, 1, Util.str(obj.get("dbprefix")));
            table.setText(i + 2, 2, Util.str(obj.get("db")));
            table.setText(i + 2, 3, Util.str(obj.get("description")));
            table.setText(i + 2, 4, Util.str(obj.get("wikilogin")));
            table.setText(i + 2, 5, Util.str(obj.get("diskquota")) + " MB");
            table.setText(i + 2, 6, "" + Util.getBoolean(obj.get("beta")));
            table.setText(i + 2, 7, statusAsString(Util.getInt(obj.get("portal_status"))));
            table.setText(i + 2, 8, Util.str(obj.get("portal_title")));
            table.setText(i + 2, 9, Util.strSkipNull(obj.get("archive_limit")));
            table.setText(i + 2, 10, Util.strSkipNull(obj.get("reduced_mode")));
            table.setText(i + 2, 11, Util.strSkipNull(obj.get("parentdbprefix")));
            Image image = ImageFactory.editImage("edit" + Util.str(obj.get("id")));
            image.addClickHandler(this);
            table.setWidget(i + 2, 12, image);

            table.getCellFormatter().addStyleName(i + 2, 5, "right");

            String style = (((i + 2) % 6) < 3) ? "line2" : "line1";
            table.getRowFormatter().setStyleName(i + 2, style + " desc");
        }
    }

    private String statusAsString(int status) {
        switch (status) {
        case 0:
            return elements.portal_status_inactive_0();
        case 1:
            return elements.portal_status_active_1();
        case 2:
            return elements.portal_status_blocked_2();
        case 3:
            return elements.portal_status_closed_3();
        case 4:
            return elements.portal_status_pending_4();
        }
        return "???";
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == newButton) {
            new NewInstallPopup(this, messages, elements, constants);
            return;
        }

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

    private void doTheLogin(String secret, String domain) {
        Util.log("Doing login...");
        String url = "http://" + domain + "/RegnskapServer/services/authenticate.php?action=adminlogin&secret="
                + secret;

        Window.Location.assign(url);
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

        private ListBoxWithErrorText statusListbox;

        private TextBoxWithErrorText portalTitle;

        private NamedButton sendWelcomeLetter;

        private TextBoxWithErrorText archiveLimit;

        private FocusWidget sendPortalActivationLetter;

        private TextBoxWithErrorText parentdbprefix;

        private TextBoxWithErrorText parenthostprefix;

        private TextBoxWithErrorText reducedMode;

        private NamedButton sulogin;

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
            edittable.setHTML(7, 0, elements.status());
            edittable.setHTML(8, 0, elements.portal_title());
            edittable.setHTML(9, 0, elements.admin_archive_limit());
            edittable.setHTML(10, 0, elements.admin_reduced_mode());
            edittable.setHTML(11, 0, elements.admin_parentdbprefix());
            edittable.setHTML(12, 0, elements.admin_parentdomainprefix());

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

            statusListbox = new ListBoxWithErrorText("portal_status");
            statusListbox.addItem(elements.portal_status_inactive_0(), "0");
            statusListbox.addItem(elements.portal_status_active_1(), "1");
            statusListbox.addItem(elements.portal_status_closed_3(), "3");
            statusListbox.addItem(elements.portal_status_pending_4(), "4");
            statusListbox.addItem(elements.portal_status_blocked_2(), "2");

            portalTitle = new TextBoxWithErrorText("portal_title");
            archiveLimit = new TextBoxWithErrorText("archive_limit");

            reducedMode = new TextBoxWithErrorText("reducedmode");
            parentdbprefix = new TextBoxWithErrorText("parentdbprefix");
            parenthostprefix = new TextBoxWithErrorText("parentdomain");
            
            edittable.setWidget(0, 1, hostprefixBox);
            edittable.setWidget(3, 1, descriptionBox);
            edittable.setWidget(4, 1, wikiLogin);
            edittable.setWidget(5, 1, diskQvotaBox);
            edittable.setWidget(6, 1, betaBox);
            edittable.setWidget(7, 1, statusListbox);
            edittable.setWidget(8, 1, portalTitle);
            edittable.setWidget(9, 1, archiveLimit);
            edittable.setWidget(10, 1, reducedMode);
            edittable.setWidget(11, 1, parentdbprefix);
            edittable.setWidget(12, 1, parenthostprefix);

            DockPanel dp = new DockPanel();
            dp.add(edittable, DockPanel.NORTH);

            saveButton = new NamedButton("adminInstall_saveButton", elements.save());
            saveButton.addClickHandler(this);
            cancelButton = new NamedButton("adminInstall_cancelButton", elements.cancel());
            cancelButton.addClickHandler(this);

            deleteButton = new NamedButton("adminInstall_deleteButton", elements.admin_delete_accounting());
            deleteButton.addClickHandler(this);

            sendWelcomeLetter = new NamedButton("admin_send_welcome_letter", elements.admin_send_welcome_letter());
            sendWelcomeLetter.addClickHandler(this);

            sendPortalActivationLetter = new NamedButton("admin_send_portal_activation_letter", elements
                    .admin_send_portal_letter());
            sendPortalActivationLetter.addClickHandler(this);

            sulogin = new NamedButton("admin_login", elements.login());
            sulogin.addClickHandler(this);

            mainErrorLabel = new HTML();
            mainErrorLabel.setStyleName("error");

            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(sulogin);
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(sendWelcomeLetter);
            buttonPanel.add(sendPortalActivationLetter);
            dp.add(buttonPanel, DockPanel.NORTH);
            dp.add(mainErrorLabel, DockPanel.NORTH);
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

            Util.setIndexByValue(statusListbox.getListbox(), Util.str(obj.get("portal_status")));
            portalTitle.setText(Util.str(obj.get("portal_title")));
            archiveLimit.setText(Util.strSkipNull(obj.get("archive_limit")));

            reducedMode.setText(Util.strSkipNull(obj.get("reduced_mode")));
            parentdbprefix.setText(Util.strSkipNull(obj.get("parentdbprefix")));
            parenthostprefix.setText(Util.strSkipNull(obj.get("parenthostprefix")));
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
            } else if (sender == sendWelcomeLetter) {
                doSendWelcomeLetter();
            } else if (sender == sendPortalActivationLetter) {
                doSendPortalLetter();
            } else if (sender == sulogin) {
                doSuLogin();
            }
        }

        private void doSendPortalLetter() {
            boolean choice = Window.confirm(messages.confirm_send_portal_letter());

            if (choice) {
                ServerResponse callback = new ServerResponse() {

                    public void serverResponse(JSONValue responseObj) {
                        mainErrorLabel.setText(messages.sendt_portal_letter());
                    }
                };
                AuthResponder.get(constants, messages, callback, "admin/installs.php?action=sendPortalLetter&id="
                        + this.currentId);

            }
        }

        private void doSendWelcomeLetter() {
            boolean choice = Window.confirm(messages.confirm_send_welcome_letter());

            if (choice) {
                ServerResponse callback = new ServerResponse() {

                    public void serverResponse(JSONValue responseObj) {
                        mainErrorLabel.setText(messages.sendt_welcome_letter());
                    }
                };
                AuthResponder.get(constants, messages, callback, "admin/installs.php?action=sendWelcomeLetter&id="
                        + this.currentId);

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

        public void doSuLogin() {
            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue responseObj) {
                    JSONObject object = responseObj.isObject();
                    String secret = Util.str(object.get("secret"));
                    String domain = Util.str(object.get("domain"));

                    doTheLogin(secret, domain);
                }
            };
            AuthResponder.get(constants, messages, callback, "admin/installs.php?action=adminlogin&id="
                    + this.currentId);
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
            Util.addPostParam(sb, "portal_status", statusListbox.getText());
            Util.addPostParam(sb, "portal_title", portalTitle.getText());
            Util.addPostParam(sb, "archive_limit", archiveLimit.getText());
            Util.addPostParam(sb, "parentdbprefix", parentdbprefix.getText());
            Util.addPostParam(sb, "reduced_mode", reducedMode.getText());
            Util.addPostParam(sb, "parenthostprefix", parenthostprefix.getText());

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
            mv.range(messages.field_positive(), 0, Integer.MAX_VALUE, diskQvotaBox, archiveLimit);
            mv.mandatory(messages.required_field(), diskQvotaBox, archiveLimit);
            return mv.validateStatus();
        }

    }

}
