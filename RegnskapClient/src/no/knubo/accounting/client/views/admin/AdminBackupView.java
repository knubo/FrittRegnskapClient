package no.knubo.accounting.client.views.admin;

import java.util.Set;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.FileUploadWithErrorText;
import no.knubo.accounting.client.views.files.UploadDelegate;
import no.knubo.accounting.client.views.files.UploadDelegateCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class AdminBackupView extends Composite implements UploadDelegateCallback, ClickHandler {

    private static AdminBackupView me;
    private final Elements elements;
    private final I18NAccount messages;
    private final Constants constants;
    private FileUploadWithErrorText upload;
    private UploadDelegate uploadDelegate;
    private AccountTable analyzeTable;
    private CheckBox selectAll;
    private Hidden dbSelect;
    private ListBox dbListbox;
    private Button clearButton;
    private Button installButton;

    public AdminBackupView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        dbSelect = new Hidden("dbSelect");

        uploadDelegate = new UploadDelegate("admin/admin_backup_admin.php", this, constants, messages, elements,
                dbSelect);

        DockPanel dp = new DockPanel();

        analyzeTable = new AccountTable("tableborder");
        dp.add(uploadDelegate.getForm(), DockPanel.NORTH);

        dbListbox = new ListBox();
        dp.add(new Label(elements.select_database()), DockPanel.NORTH);

        HorizontalPanel hp = new HorizontalPanel();

        clearButton = new Button(elements.clear());
        clearButton.addClickHandler(this);

        installButton = new Button(elements.backup_install());
        installButton.addClickHandler(this);
        installButton.setEnabled(false);
        
        hp.add(dbListbox);
        hp.add(clearButton);
        hp.add(installButton);
        dp.add(hp, DockPanel.NORTH);

        dp.add(analyzeTable, DockPanel.NORTH);

        analyzeTable.setHeaders(0, "", "Table", "Drop", "Truncate", "Lock", "Insert", "Unlock", "Table exist",
                "Backup exist");

        selectAll = new CheckBox(elements.select_all());
        selectAll.addClickHandler(this);
        analyzeTable.setWidget(0, 0, selectAll);

        initWidget(dp);
    }

    public static AdminBackupView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminBackupView(messages, constants, elements);
        }
        return me;
    }

    public void init() {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject obj = responseObj.isObject();
                JSONObject allDB = obj.get("db").isObject();

                dbListbox.clear();

                Set<String> keys = allDB.keySet();

                for (String k : keys) {
                    JSONArray db = allDB.get(k).isArray();
                    dbListbox.addItem(Util.str(db.get(3)), k);
                }
            }
        };
        AuthResponder.get(constants, messages, callback, "admin/admin_backup_admin.php?action=init");
    }

    @Override
    public void uploadComplete() {
        /* Not needed */
    }

    @Override
    public boolean uploadBody(String body) {

        if (body.length() == 0) {
            Util.log("No message upload admin");
            return true;
        }
        
        installButton.setEnabled(true);

        JSONValue jsonValue = JSONParser.parseStrict(body);
        JSONArray array = jsonValue.isArray();

        String[] fields = { "table", "dropNotFound", "truncateNotFound", "lockFound", "insertFound", "unlockFound",
                "tableExist", "backupExist" };

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();

            CheckBox box = new CheckBox();
            analyzeTable.setWidget(i + 1, 0, box);

            for (int j = 0; j < fields.length; j++) {

                if (j >= 1) {
                    boolean checked = Util.getBoolean(obj.get(fields[j]));

                    if (!checked && !fields[j].equals("tableExist") && !fields[j].equals("backupExist")) {
                        box.setEnabled(false);
                    }
                    analyzeTable.setText(i + 1, j + 1, checked ? elements.ok() : elements.x());
                } else {
                    String name = Util.str(obj.get(fields[j]));
                    Anchor anchor = new Anchor(name);
                    anchor.setName(name);
                    anchor.addClickHandler(this);
                    analyzeTable.setWidget(i + 1, j + 1, anchor);
                }
            }
        }
        return true;
    }

    @Override
    public void preUpload() {
        clear();

        dbSelect.setValue(Util.getSelected(dbListbox));
        dbListbox.setEnabled(false);
    }

    private void clear() {
        dbListbox.setEnabled(true);
        while (analyzeTable.getRowCount() > 1) {
            analyzeTable.removeRow(1);
        }
        selectAll.setValue(false);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() instanceof Anchor) {
            showSqlContentPopup((Anchor) event.getSource());
        } else if (event.getSource() == selectAll) {
            toggleSelectAll();
        } else if(event.getSource() == clearButton) {
            clear();
        }
    }

    private void toggleSelectAll() {

        for (int row = 1; row < analyzeTable.getRowCount(); row++) {
            Widget box = analyzeTable.getWidget(row, 0);

            if (box instanceof CheckBox) {
                CheckBox cb = (CheckBox) box;

                if (cb.isEnabled()) {
                    cb.setValue(selectAll.getValue());
                }
            }

        }
    }

    private void showSqlContentPopup(Anchor anchor) {
        AuthResponder.get(constants, messages, new ServerResponseString() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                /* Not used */
            }

            @Override
            public void serverResponse(String response) {
                DialogBox db = new DialogBox();
                db.setAutoHideEnabled(true);
                HTML html = new HTML("<pre style=\"white-space:pre-wrap\">" + response + "</pre>");
                db.setWidget(html);
                db.center();
            }
        }, "admin/admin_backup_admin.php?action=view&viewFile=" + anchor.getName());
    }
}
