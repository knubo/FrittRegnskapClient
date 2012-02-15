package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
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

    public AdminBackupView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        uploadDelegate = new UploadDelegate("admin/admin_backup_admin.php", this, constants, messages, elements);

        DockPanel dp = new DockPanel();

        analyzeTable = new AccountTable("tableborder");
        dp.add(uploadDelegate.getForm(), DockPanel.NORTH);
        dp.add(analyzeTable, DockPanel.NORTH);

        analyzeTable.setHeaders(0, "", "Table", "Drop", "Truncate", "Lock", "Insert", "Unlock");
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

        JSONValue jsonValue = JSONParser.parseStrict(body);
        JSONArray array = jsonValue.isArray();

        String[] fields = { "table", "dropNotFound", "truncateNotFound", "lockFound", "insertFound", "unlockFound" };

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.get(i).isObject();

            CheckBox box = new CheckBox();
            analyzeTable.setWidget(i + 1, 0, box);

            for (int j = 0; j < fields.length; j++) {

                if (j >= 1) {
                    boolean checked = Util.getBoolean(obj.get(fields[j]));
                    if (!checked) {
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
        while (analyzeTable.getRowCount() > 1) {
            analyzeTable.removeRow(1);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() instanceof Anchor) {
            showSqlContentPopup((Anchor) event.getSource());
        }
        if(event.getSource() == selectAll) {
            toggleSelectAll();
        }
    }

    private void toggleSelectAll() {
        
        for(int row = 1; row < analyzeTable.getRowCount(); row++) {
            Widget box = analyzeTable.getWidget(row, 0);
            
            if (box instanceof CheckBox) {
                CheckBox cb = (CheckBox) box;
                
                if(cb.isEnabled()) {
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
                HTML html = new HTML("<pre style=\"white-space:pre-wrap\">"+response+"</pre>");
                db.setWidget(html);
                db.center();
            }
        }, "admin/admin_backup_admin.php?action=view&viewFile=" + anchor.getName());
    }
}
