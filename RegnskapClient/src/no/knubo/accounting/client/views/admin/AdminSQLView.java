package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdminSQLView extends Composite implements ClickHandler {

    private static AdminSQLView me;

    public static AdminSQLView show(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminSQLView(messages, constants, elements);
        }
        return me;
    }

    private I18NAccount messages;
    private Constants constants;
    private NamedTextArea insertSQLText;
    private NamedButton insertButton;
    private AccountTable sqlTable;
    private final Elements elements;

    public AdminSQLView(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        this.elements = elements;

        VerticalPanel vp = new VerticalPanel();

        vp.add(new Label(elements.admin_new_sql()));
        insertSQLText = new NamedTextArea("newsql");
        insertSQLText.setSize("40em", "3em");
        vp.add(insertSQLText);
        insertButton = new NamedButton("sql_insert", elements.add());
        insertButton.addClickHandler(this);
        vp.add(insertButton);

        sqlTable = new AccountTable("tableborder");
        sqlTable.setText(0, 0, "SQL");
        sqlTable.setText(0, 1, elements.admin_verified());
        sqlTable.setText(0, 2, elements.admin_runinbeta());
        sqlTable.setText(0, 3, elements.admin_runinmain());
        sqlTable.setText(0, 4, elements.admin_registered_date());
        sqlTable.setText(0, 5, elements.admin_run());
        sqlTable.setHeaderRowStyle(0);
        vp.add(sqlTable);

        initWidget(vp);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == insertButton) {
            doInsert();
        } else if (event.getSource() instanceof Image) {
            Image image = (Image) event.getSource();

            String actionfor = DOM.getElementAttribute(image.getElement(), "id");

            String id = actionfor.substring(9);

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(JSONValue responseObj) {
                    new SQLRunner(responseObj.isObject());
                }

            };
            AuthResponder.get(constants, messages, callback, "admin/admin_sql.php?action=get&id=" + id);
        }
    }

    private void doInsert() {
        StringBuffer params = new StringBuffer();
        params.append("action=add");
        Util.addPostParam(params, "sql", insertSQLText.getText());

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                insertSQLText.setText("");
                init();
            }
        };
        AuthResponder.post(constants, messages, callback, params, "admin/admin_sql.php");
    }

    public void init() {
        while (sqlTable.getRowCount() > 1) {
            sqlTable.removeRow(1);
        }

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject value = array.get(i).isObject();

                    sqlTable.setStr(i + 1, 0, value.get("sqltorun"));

                    boolean runinbeta = Util.getBoolean(value.get("runinbeta"));
                    boolean runinother = Util.getBoolean(value.get("runinother"));

                    sqlTable.setText(i + 1, 1, giveYesNo(value, "verified"), "center");
                    sqlTable.setText(i + 1, 2, giveYesNo(runinbeta), "center");
                    sqlTable.setText(i + 1, 3, giveYesNo(runinother), "center");
                    sqlTable.setStr(i + 1, 4, value.get("added"));

                    if (!runinbeta || !runinother) {
                        Image image = ImageFactory.gearsImage("actionfor" + Util.str(value.get("id")));
                        image.addClickHandler(me);
                        sqlTable.setWidget(i + 1, 5, image, "center");
                    } else {
                        sqlTable.setText(i + 1, 5, "");
                    }
                    sqlTable.alternateStyle(i + 1, 0);
                }
            }

        };
        AuthResponder.get(constants, messages, callback, "admin/admin_sql.php?action=list");
    }

    String giveYesNo(JSONObject value, String field) {
        boolean test = Util.getBoolean(value.get(field));
        return giveYesNo(test);
    }

    String giveYesNo(boolean test) {
        return test ? elements.admin_yes() : elements.admin_no();
    }

    class SQLRunner extends DialogBox implements ClickHandler {
        private Frame frame;
        private VerticalPanel vp;
        private NamedButton closeButton;
        private NamedButton betaButton;
        private NamedButton mainButton;
        private final int id;
        private AccountTable table;

        public SQLRunner(JSONObject value) {
            JSONObject sql = value.get("sql").isObject();
            JSONArray installs = value.get("installs").isArray();

            boolean runinbeta = Util.getBoolean(sql.get("runinbeta"));

            this.id = Util.getInt(sql.get("id"));
            vp = new VerticalPanel();

            vp.add(new Label(id+":"+Util.str(sql.get("sqltorun"))));

            FlowPanel buttonPanel = new FlowPanel();
            betaButton = new NamedButton("run_beta", elements.admin_do_runinbeta());
            betaButton.addClickHandler(this);
            buttonPanel.add(betaButton);
            mainButton = new NamedButton("run_main", elements.admin_do_runinmain());
            mainButton.addClickHandler(this);
            buttonPanel.add(mainButton);
            vp.add(buttonPanel);

            table = new AccountTable("tableborder");
            vp.add(table);

            table.setText(0, 0, elements.admin_dbprefix());
            table.setText(0, 1, elements.admin_database());
            table.setText(0, 2, "BETA");
            table.setText(0, 3, elements.status());
            table.setHeaderRowStyle(0);

            fillInstalls(installs);

            if (runinbeta) {
                mainButton.setEnabled(true);
                betaButton.setEnabled(false);
            } else {
                betaButton.setEnabled(true);
                mainButton.setEnabled(false);
            }

            closeButton = new NamedButton("close", elements.close());
            closeButton.addClickHandler(this);
            vp.add(closeButton);
            setWidget(vp);
            center();
        }

        private void fillInstalls(JSONArray installs) {
            for (int i = 0; i < installs.size(); i++) {
                JSONObject obj = installs.get(i).isObject();
                table.setText(i + 1, 0, Util.str(obj.get("dbprefix")));
                table.setText(i + 1, 1, Util.str(obj.get("db")));
                table.setText(i + 1, 2, giveYesNo(obj, "beta"), "center");
                table.setText(i + 1, 3, Util.strSkipNull(obj.get("sqlIdToRun")));
                table.alternateStyle(i + 1, 2);
            }
        }

        public void onClick(ClickEvent event) {
            if (event.getSource() == closeButton) {
                hide();
            } else if (event.getSource() == betaButton) {
            } else if (event.getSource() == mainButton) {
            }
        }
    }
}
