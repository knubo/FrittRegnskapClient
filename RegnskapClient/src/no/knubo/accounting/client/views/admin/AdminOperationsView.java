package no.knubo.accounting.client.views.admin;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.HTMLStreamWindow;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.richtexttoolbar.RichTextToolbar;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdminOperationsView extends Composite implements ClickHandler {
    private static AdminOperationsView me;
    private final I18NAccount messages;
    private final Constants constants;
    private FlexTable table;
    private final Elements elements;
    private RichTextArea richBodyBox;
    private VerticalPanel richEditorWithToolbar;
    private NamedButton openButton;
    private NamedButton closeButton;
    private NamedButton saveButton;
    private Label statusLabel;
    private NamedButton distributeButton;

    public static AdminOperationsView getInstance(I18NAccount messages, Constants constants, Elements elements) {
        if (me == null) {
            me = new AdminOperationsView(messages, constants, elements);
        }
        return me;
    }

    public AdminOperationsView(I18NAccount messages, Constants constants, Elements elements) {

        this.messages = messages;
        this.constants = constants;
        this.elements = elements;
        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setHTML(0, 0, elements.menuitem_admin_operations());
        table.getFlexCellFormatter().setColSpan(0, 0, 2);
        table.getRowFormatter().setStyleName(0, "header");

        table.setText(2, 0, elements.message());
        table.setText(1, 0, elements.status());

        richBodyBox = new RichTextArea();
        richBodyBox.setWidth("50em");
        richBodyBox.setHeight("20em");

        richEditorWithToolbar = new VerticalPanel();
        RichTextToolbar toolbar = new RichTextToolbar(richBodyBox);

        richEditorWithToolbar.add(toolbar);
        richEditorWithToolbar.add(richBodyBox);

        table.setWidget(2, 1, richEditorWithToolbar);

        FlowPanel flowPanel = new FlowPanel();
        saveButton = new NamedButton("save_close_text", elements.save());
        saveButton.addClickHandler(this);
        flowPanel.add(saveButton);

        closeButton = new NamedButton("close_site", elements.admin_close_site());
        closeButton.addClickHandler(this);
        flowPanel.add(closeButton);

        openButton = new NamedButton("open_site", elements.admin_open_site());
        openButton.addClickHandler(this);
        flowPanel.add(openButton);

        statusLabel = new Label();
        flowPanel.add(statusLabel);

        table.setWidget(3, 1, flowPanel);

        distributeButton = new NamedButton("distribute_beta", elements.admin_copy_to_main());
        distributeButton.addClickHandler(this);
        table.setWidget(4, 1, distributeButton);

        initWidget(table);
    }

    public void init() {
        openButton.setEnabled(false);
        closeButton.setEnabled(false);
        distributeButton.setEnabled(false);

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();

                String status = Util.str(object.get("status"));

                if ("open".equals(status)) {
                    table.setText(1, 1, elements.admin_open());
                    openButton.setEnabled(false);
                    closeButton.setEnabled(true);
                    distributeButton.setEnabled(false);
                } else {
                    table.setText(1, 1, elements.admin_closed());
                    openButton.setEnabled(true);
                    closeButton.setEnabled(false);
                    distributeButton.setEnabled(true);
                }
                richBodyBox.setHTML(Util.str(object.get("content")));
            }
        };
        AuthResponder.get(constants, messages, callback, "admin/siteadmin.php?action=init");
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == saveButton) {
            doAction("save");

        } else if (event.getSource() == closeButton) {
            doAction("close");
            delayedInit();
        } else if (event.getSource() == openButton) {
            doAction("open");
            delayedInit();
        } else if (event.getSource() == distributeButton) {
            HTMLStreamWindow.open(constants.baseurl() + "admin/siteadmin.php?action=distribute", elements);
        }
    }

    private void delayedInit() {
        Timer timer = new Timer() {

            @Override
            public void run() {
                init();
            }
        };
        timer.schedule(500);
    }

    private void doAction(String action) {
        StringBuffer sb = new StringBuffer();

        sb.append("action=" + action);
        Util.addPostParam(sb, "content", richBodyBox.getHTML());
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                statusLabel.setText(messages.save_ok());
                Util.timedMessage(statusLabel, "", 8);
            }
        };
        AuthResponder.post(constants, messages, callback, sb, "admin/siteadmin.php");
    }
}
