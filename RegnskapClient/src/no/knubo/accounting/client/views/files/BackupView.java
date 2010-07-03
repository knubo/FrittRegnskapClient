package no.knubo.accounting.client.views.files;

import java.util.ArrayList;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class BackupView extends Composite implements ClickHandler {

    private static BackupView instance;
    private final I18NAccount messages;
    private final Constants constants;
    private FlexTable table;
    TextArea progress;
    private NamedButton startBackup;
    private NamedButton downloadLast;
    private List<BackupAction> actions;
    private final Elements elements;

    public BackupView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        table = new FlexTable();
        table.setStyleName("tableborder");

        table.setText(0, 0, elements.title_back());
        table.getFlexCellFormatter().setColSpan(00, 0, 3);
        table.setText(1, 0, elements.backup_last());
        table.getFlexCellFormatter().setStyleName(1, 0, "header desc");
        table.getRowFormatter().setStyleName(0, "header");

        startBackup = new NamedButton("start_backup", elements.backup_start());
        startBackup.addClickHandler(this);
        table.setWidget(2, 1, startBackup);

        downloadLast = new NamedButton("downloadLast", elements.backup_download_last());
        downloadLast.addClickHandler(this);
        table.setWidget(3, 1, downloadLast);

        table.setText(4, 0, elements.backup_progress());
        progress = new TextArea();
        progress.setWidth("40em");
        progress.setHeight("10em");
        progress.setReadOnly(true);
        table.setWidget(4, 1, progress);
        table.getFlexCellFormatter().setStyleName(2, 0, "header");
        table.getFlexCellFormatter().setStyleName(3, 0, "header");
        table.getFlexCellFormatter().setStyleName(4, 0, "header logline");

        DockPanel dp = new DockPanel();
        dp.add(table, DockPanel.NORTH);

        initWidget(dp);
    }

    public static BackupView getInstance(Constants constants, I18NAccount messages,
            Elements elements) {
        if (instance == null) {
            instance = new BackupView(messages, constants, elements);
        }
        return instance;
    }

    public void init() {
        progress.setText("");
        actions = new ArrayList<BackupAction>();

        getInfo();

    }

    private void getInfo() {
        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONObject info = value.isObject();

                downloadLast.setEnabled(info.containsKey("backup_file"));
                table.setText(1, 1, Util.str(info.get("last_backup")));
            }
        };

        AuthResponder.get(constants, messages, callback, "backup.php?action=info");
    }

    public void onClick(ClickEvent event) {
    	Widget sender = (Widget) event.getSource();
    	
    	if (sender == startBackup) {
            startBackup.setEnabled(false);
            startBackup();
        } else if (sender == downloadLast) {
            downloadBackupFile();
        }
    }

    private void startBackup() {
        actions.add(new BackupAction("init", elements.backup_init()) {
            @Override
            public void data(JSONValue value) { /* Empty */
            }
        });

        actions.add(new BackupAction("tables", elements.backup_table_count()) {
            @Override
            public void data(JSONValue value) {
                JSONArray tables = value.isArray();
                text = text + tables.size();

                addActionsForTables(tables);
            }

        });

        BackupAction elem = actions.remove(0);

        elem.execute();
    }

    protected void addActionsForTables(JSONArray tables) {
        for (int i = 0; i < tables.size(); i++) {
            JSONValue value = tables.get(i);

            String table = Util.str(value);

            String text = elements.backup_table() + " " + table + " (" + (tables.size() - i) + ")";

            actions.add(new BackupAction("backup&table=" + table, text) {
                @Override
                public void data(JSONValue value) {
                    isOKAbortOnError(value);
                }

            });
        }

        actions.add(new BackupAction("zip", elements.backup_zipping()) {
            @Override
            public void data(JSONValue value) {
                if (isOKAbortOnError(value)) {
                    clearActions();
                    downloadBackupFile();
                    getInfo();
                }
            }
        });
    }

    private void downloadBackupFile() {
        Window.open(this.constants.baseurl() + "backup.php?action=get", "_blank", "");
    }

    private void clearActions() {
        actions.clear();
        startBackup.setEnabled(true);
    }

    abstract class BackupAction {
        private final String urlAction;
        protected String text;

        public BackupAction(String urlAction, String text) {
            this.urlAction = urlAction;
            this.text = text;
        }

        public void execute() {
            Timer timer = new Timer() {

                @Override
                public void run() {
                    doBackup();
                }
            };
            timer.schedule(2 * 1000);
        }

        private void doBackup() {
            ServerResponse callback = new ServerResponse() {
                public void serverResponse(JSONValue value) {
                    data(value);
                    progress.setText(text + "\n" + progress.getText());

                    if (actions.size() > 0) {
                        BackupAction elem = actions.remove(0);

                        elem.execute();
                    }
                }
            };

            AuthResponder.get(constants, messages, callback, "backup.php?action=" + urlAction);
        }

        abstract public void data(JSONValue value);

        protected boolean isOKAbortOnError(JSONValue value) {
            JSONObject obj = value.isObject();

            if (!Util.getBoolean(obj.get("result"))) {
                text = elements.backup_error() + text;
                clearActions();
                return false;
            }

            return true;
        }

    }
}
