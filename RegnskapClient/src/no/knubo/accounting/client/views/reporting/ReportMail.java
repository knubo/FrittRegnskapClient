package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ListBoxWithErrorText;
import no.knubo.accounting.client.misc.NamedButton;
import no.knubo.accounting.client.misc.NamedTextArea;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.TextBoxWithErrorText;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class ReportMail extends Composite implements ClickListener {
    private static ReportMail reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private FlexTable table;
    ListBoxWithErrorText reciversListBox;
    TextBoxWithErrorText titleBox;
    NamedTextArea bodyBox;
    protected JSONArray receivers;
    private EmailSendStatus emailSendStatusView;

    public static ReportMail getInstance(Constants constants,
            I18NAccount messages) {
        if (reportInstance == null) {
            reportInstance = new ReportMail(constants, messages);
        }
        return reportInstance;
    }

    public ReportMail(Constants constants, I18NAccount messages) {
        this.constants = constants;
        this.messages = messages;

        FlexTable mainTable = new FlexTable();
        mainTable.setStyleName("edittable");
        mainTable.setText(0, 0, messages.mail_receivers());
        mainTable.setText(1, 0, messages.mail_title());
        mainTable.setText(2, 0, messages.mail_body());

        reciversListBox = new ListBoxWithErrorText("mail_receivers");
        reciversListBox.getListbox().addItem("", "");
        reciversListBox.getListbox().addItem(messages.mail_query_members(),
                "members");
        reciversListBox.getListbox().addItem(messages.mail_query_newsletter(),
                "newsletter");
        mainTable.setWidget(0, 1, reciversListBox);

        titleBox = new TextBoxWithErrorText("mail_title");
        titleBox.setMaxLength(200);
        titleBox.setVisibleLength(80);
        mainTable.setWidget(1, 1, titleBox);

        bodyBox = new NamedTextArea("mail_body");
        bodyBox.setCharacterWidth(90);
        bodyBox.setVisibleLines(30);
        mainTable.setWidget(2, 1, bodyBox);

        NamedButton sendButton = new NamedButton("mail_send", messages
                .mail_send());
        sendButton.addClickListener(this);
        mainTable.setWidget(3, 0, sendButton);

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, messages.name());
        table.setText(0, 1, messages.email());
        table.setText(0, 2, messages.status());
        table.getRowFormatter().setStyleName(0, "header");

        DockPanel dp = new DockPanel();
        dp.add(mainTable, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    public void fillReceivers() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
        String selectedList = reciversListBox.getText();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                constants.baseurl() + "reports/email.php?action=list&query="
                        + selectedList);

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(String responseText) {
                JSONValue parse = JSONParser.parse(responseText);

                receivers = parse.isArray();
                confirmSendEmail();
            }
        };

        try {
            builder.sendRequest("", new AuthResponder(constants, messages,
                    callback));
        } catch (RequestException e) {
            Window.alert("Failed to send the request: " + e.getMessage());
        }

    }

    protected void confirmSendEmail() {
        boolean ok = Window.confirm(messages.mail_confirm(String
                .valueOf(receivers.size())));

        if (ok) {
            sendEmails();
        }
    }

    private void sendEmails() {
        if (emailSendStatusView == null) {
            emailSendStatusView = new EmailSendStatus();
        }
        int left = bodyBox.getAbsoluteLeft() + 40;

        int top = bodyBox.getAbsoluteTop() + 40;
        emailSendStatusView.setPopupPosition(left, top);

        emailSendStatusView.show();
        emailSendStatusView.sendEmails();

    }

    public void onClick(Widget sender) {
        fillReceivers();
    }

    class EmailSendStatus extends DialogBox implements ClickListener {

        private int currentIndex;
        private boolean pause;
        private FlexTable infoTable;

        EmailSendStatus() {
            DockPanel dp = new DockPanel();

            infoTable = new FlexTable();
            infoTable.setStyleName("tableborder");
            infoTable.setText(0, 0, messages.mail_sending());
            infoTable.setText(1, 0, messages.name());
            infoTable.setText(2, 0, messages.email());
            infoTable.getRowFormatter().setStyleName(0, "header");
            infoTable.setWidget(0, 1, ImageFactory
                    .loadingImage("image_loading"));
            infoTable.getCellFormatter().setWidth(0, 1, "30px");
            infoTable.getColumnFormatter().setStyleName(1, "emailsendname");
            infoTable.getColumnFormatter().setStyleName(2, "emailsendemail");
            NamedButton cancelButton = new NamedButton("abort", messages
                    .abort());
            cancelButton.addClickListener(this);
            infoTable.setWidget(3, 0, cancelButton);

            dp.add(infoTable, DockPanel.NORTH);
            setWidget(dp);
        }

        public void onClick(Widget sender) {
            pause = true;

            boolean stopSending = Window.confirm(messages.mail_abort_confirm());

            if (!stopSending) {
                pause = false;
                sendOneEmail();
            } else {
                hide();
            }
        }

        public void sendEmails() {
            currentIndex = 0;
            infoTable.setText(1, 1, "");
            infoTable.setText(2, 1, "");
            infoTable.getFlexCellFormatter().setColSpan(1, 1, 2);
            infoTable.getFlexCellFormatter().setColSpan(2, 1, 2);

            sendOneEmail();
        }

        private void sendOneEmail() {
            if (pause) {
                return;
            }
            JSONObject user = receivers.get(currentIndex).isObject();

            final String name = Util.str(user.get("name"));
            infoTable.setText(1, 1, name);
            final String email = Util.str(user.get("email"));
            infoTable.setText(2, 1, email);
            infoTable.setText(0, 2, "(" + (receivers.size() - currentIndex)
                    + ")");

            StringBuffer mailRequest = new StringBuffer();

            mailRequest.append("action=email");
            Util.addPostParam(mailRequest, "subject", titleBox.getText());
            Util.addPostParam(mailRequest, "email", email);
            Util.addPostParam(mailRequest, "body", bodyBox.getText());

            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                    constants.baseurl() + "reports/email.php");

            ServerResponse callback = new ServerResponse() {

                public void serverResponse(String serverResponse) {
                    JSONValue value = JSONParser.parse(serverResponse);
                    JSONObject object = value.isObject();

                    currentIndex++;
                    table.insertRow(1);
                    table.setText(1, 0, name);
                    table.setText(1, 1, email);
                    
                    String style = (currentIndex % 2 == 0) ? "showlineposts2"
                            : "showlineposts1";
                    table.getRowFormatter().setStyleName(1, style);

                    
                    if (!("1".equals(Util.str(object.get("status"))))) {
                        table.setStyleName("error");
                        table.setText(1, 2, "error");
                    } else {
                        table.setText(1, 2, messages.ok());
                    }
                    if (receivers.size() > currentIndex) {
                        sendOneEmail();
                    } else {
                        hide();
                    }
                }

            };

            try {
                builder.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                builder.sendRequest(mailRequest.toString(),
                        new AuthResponder(constants, messages, callback));
            } catch (RequestException e) {
                Window.alert("Failed to send the request: " + e.getMessage());
            }

        }
    }

}
