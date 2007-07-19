package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.help.HelpPanel;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.IdHolder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.ServerResponse;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PostTypeEditView extends Composite implements ClickListener {

    private static PostTypeEditView me;
    private final I18NAccount messages;
    private final Constants constants;
    private final HelpPanel helpPanel;
    private FlexTable inUseTable;
    private FlexTable notInUseTable;
    private IdHolder idHolderInUse;
    private IdHolder idHolderNotInUse;
    private IdHolder idHolderEdit;
    private NamedButton button;

    public static PostTypeEditView show(I18NAccount messages,
            Constants constants, HelpPanel helpPanel) {
        if (me == null) {
            me = new PostTypeEditView(messages, constants, helpPanel);
        }
        me.setVisible(true);
        return me;
    }

    public PostTypeEditView(I18NAccount messages, Constants constants,
            HelpPanel helpPanel) {
        this.messages = messages;
        this.constants = constants;
        this.helpPanel = helpPanel;

        DockPanel dp = new DockPanel();

        button = new NamedButton("new_account", messages.new_account());
        button.addClickListener(this);
        dp.add(button, DockPanel.NORTH);

        idHolderInUse = new IdHolder();
        idHolderNotInUse = new IdHolder();
        idHolderEdit = new IdHolder();
        inUseTable = createTable(messages.title_posttype_edit_in_use());
        notInUseTable = createTable(messages.title_posttype_edit_not_in_use());

        dp.add(inUseTable, DockPanel.NORTH);
        dp.add(notInUseTable, DockPanel.NORTH);

        initWidget(dp);
    }

    public FlexTable createTable(String title) {
        FlexTable table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, title);
        table.getRowFormatter().setStyleName(0, "header");
        table.getFlexCellFormatter().setColSpan(0, 0, 6);

        table.setText(1, 0, messages.account());
        table.setText(1, 1, messages.description());
        table.setText(1, 2, messages.account_collection_month());
        table.setText(1, 3, messages.account_collection_accountplan());
        table.setText(1, 4, "");
        table.setText(1, 5, "");

        table.getRowFormatter().setStyleName(1, "header");

        return table;
    }

    public void init() {
        idHolderInUse.init();
        idHolderNotInUse.init();
        idHolderEdit.init();

        while (inUseTable.getRowCount() > 2) {
            inUseTable.removeRow(2);
        }

        while (notInUseTable.getRowCount() > 2) {
            notInUseTable.removeRow(2);
        }

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl()
                        + "registers/posttypes.php?action=all&disableFilter=1");

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue parse = JSONParser.parse(responseText);
                JSONArray array = parse.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONValue value = array.get(i);
                    JSONObject object = value.isObject();
                    String id = Util.str(object.get("PostType"));
                    String description = Util.str(object.get("Description"));
                    String inUse = Util.str(object.get("InUse"));
                    String colAccMonth = Util.str(object.get("CollPost"));
                    String colAccAccountPlan = Util.str(object
                            .get("DetailPost"));

                    if ("1".equals(inUse)) {
                        addRow(inUseTable, idHolderInUse, id, description,
                                inUse, colAccMonth, colAccAccountPlan);
                    } else {
                        addRow(notInUseTable, idHolderNotInUse, id,
                                description, inUse, colAccMonth,
                                colAccAccountPlan);
                    }

                }
                helpPanel.resize(me);
            }

            private void addRow(FlexTable table, IdHolder idHolder, String id,
                    String description, String inUse, String colAccMonth,
                    String colAccAccountPlan) {
                int row = table.getRowCount();

                table.setText(row, 0, id);
                table.setText(row, 1, description);
                table.setText(row, 2, colAccMonth);
                table.setText(row, 3, colAccAccountPlan);

                table.getCellFormatter().setStyleName(row, 1, "desc");

                Image actionImage = null;
                if ("1".equals(inUse)) {
                    actionImage = ImageFactory
                            .removeImage("postTypeEditView.removeImage");
                } else {
                    actionImage = ImageFactory
                            .chooseImage("postTypeEditView.chooseImage");
                }
                actionImage.addClickListener(me);
                idHolder.add(id, actionImage);

                Image editImage = ImageFactory
                        .editImage("postTypeEditView.editImage");
                editImage.addClickListener(me);
                idHolderEdit.add(id, editImage);

                table.setWidget(row, 4, editImage);
                table.setWidget(row, 5, actionImage);

                String style = (((row + 1) % 6) < 3) ? "line2" : "line1";
                table.getRowFormatter().setStyleName(row, style);

            }

        };
        try {
            builder.sendRequest("", new AuthResponder(constants, messages,
                    callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    public void onClick(Widget sender) {
        // TODO Auto-generated method stub

    }
}