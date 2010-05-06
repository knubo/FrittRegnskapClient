package no.knubo.accounting.client.views.reporting;

import java.util.ArrayList;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.Logger;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseWithErrorFeedback;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportMail extends Composite implements ClickHandler {
    private static ReportMail reportInstance;
    private final Constants constants;
    private final Elements elements;
    private FlexTable table;
    ListBoxWithErrorText reciversListBox;
    TextBoxWithErrorText titleBox;
    NamedTextArea bodyBox;
    protected JSONArray receivers;
    private EmailSendStatus emailSendStatusView;
    private I18NAccount messages;
    private NamedButton sendButton;
    private NamedButton attachButton;
    private PickAttachments pickAttachments;
    private FlexTable attachedFiles;
    private Logger logger;
    private TextBox yearBox;
    private NamedButton reSendButton;
    private ListBoxWithErrorText headerSelect;
    private ListBoxWithErrorText footerSelect;
    private RadioButton radioFormatPlain;
    private RadioButton radioFormatWiki;
    private RadioButton radioFormatHTML;

    public static ReportMail getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportMail(constants, messages, elements);
        }
        return reportInstance;
    }

    public ReportMail(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.logger = new Logger(this.constants);

        FlexTable mainTable = new FlexTable();
        mainTable.setStyleName("edittable");
        mainTable.setText(0, 0, elements.mail_receivers());
        mainTable.setText(1, 0, elements.mail_title());
        mainTable.setText(2, 0, elements.email_format());
        mainTable.setText(3, 0, elements.mail_body());
        mainTable.setText(4, 0, elements.email_header());
        mainTable.setText(5, 0, elements.email_footer());
        mainTable.setText(6, 0, elements.files());

        mainTable.setWidget(0, 1, createReceiverRow(elements));

        titleBox = new TextBoxWithErrorText("mail_title");
        titleBox.setMaxLength(200);
        titleBox.setVisibleLength(80);
        mainTable.setWidget(1, 1, titleBox);

        addEmailFormat(mainTable, 2);

        bodyBox = new NamedTextArea("mail_body");
        bodyBox.setCharacterWidth(90);
        bodyBox.setVisibleLines(30);
        mainTable.setWidget(3, 1, bodyBox);

        attachButton = new NamedButton("attach_files", elements.attach_files());
        attachButton.addClickHandler(this);

        addHeaderFooterSelects(mainTable, 4);

        attachedFiles = new FlexTable();
        attachedFiles.setStyleName("insidetable");
        mainTable.setWidget(6, 1, attachedFiles);
        mainTable.setWidget(7, 1, attachButton);

        addSendButtons(mainTable, 8);

        table = new FlexTable();
        table.setStyleName("tableborder");
        table.setText(0, 0, elements.name());
        table.setText(0, 1, elements.email());
        table.setText(0, 2, elements.status());
        table.getRowFormatter().setStyleName(0, "header");

        DockPanel dp = new DockPanel();
        dp.add(mainTable, DockPanel.NORTH);
        dp.add(table, DockPanel.NORTH);
        initWidget(dp);
    }

    private void addEmailFormat(FlexTable mainTable, int row) {
        radioFormatPlain = new RadioButton("format", elements.email_format_plain());
        radioFormatWiki = new RadioButton("format", elements.email_format_wiki());
        radioFormatHTML = new RadioButton("format", elements.email_format_html());

        radioFormatPlain.setValue(true);

        FlowPanel fp = new FlowPanel();
        fp.add(radioFormatPlain);
        fp.add(radioFormatWiki);
        fp.add(radioFormatHTML);

        mainTable.setWidget(row, 1, fp);
    }

    private void addHeaderFooterSelects(FlexTable mainTable, int row) {
        headerSelect = new ListBoxWithErrorText("header");
        footerSelect = new ListBoxWithErrorText("footer");

        mainTable.setWidget(row, 1, headerSelect);
        mainTable.setWidget(row + 1, 1, footerSelect);

    }

    private void addSendButtons(FlexTable mainTable, int row) {
        sendButton = new NamedButton("mail_send", elements.mail_send());
        sendButton.addClickHandler(this);

        reSendButton = new NamedButton("mail_send_again", elements.mail_send_again());
        reSendButton.addClickHandler(this);
        reSendButton.setEnabled(false);

        FlowPanel fp = new FlowPanel();

        fp.add(sendButton);
        fp.add(reSendButton);

        mainTable.setWidget(row, 1, fp);
    }

    private HorizontalPanel createReceiverRow(Elements elements) {
        HorizontalPanel hpReceivers = new HorizontalPanel();
        yearBox = new TextBox();
        yearBox.setText("" + Util.currentYear());

        reciversListBox = new ListBoxWithErrorText("mail_receivers");
        reciversListBox.getListbox().addItem("", "");
        reciversListBox.getListbox().addItem(elements.mail_query_members(), "members");
        reciversListBox.getListbox().addItem(elements.mail_query_newsletter(), "newsletter");
        reciversListBox.getListbox().addItem(elements.mail_test(), "test");
        reciversListBox.getListbox().addItem(elements.mail_simulate(), "simulate");

        hpReceivers.add(reciversListBox);
        hpReceivers.add(new Label(elements.year()));
        hpReceivers.add(yearBox);
        return hpReceivers;
    }

    protected void setAttachedFiles(List<String> fileNames) {
        while (attachedFiles.getRowCount() > 0) {
            attachedFiles.removeRow(0);
        }

        int row = 0;
        for (String fileName : fileNames) {

            attachedFiles.setText(row++, 0, fileName);
        }
    }

    private void chooseAttachments() {
        final ArrayList<String> existingFiles = getSelectedFiles();

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue parse) {
                JSONArray files = parse.isObject().get("files").isArray();

                openSelectFilesForAttachment(files, existingFiles);
            }
        };

        AuthResponder.get(constants, messages, callback, "files/files.php?action=list");
    }

    private ArrayList<String> getSelectedFiles() {
        final ArrayList<String> existingFiles = new ArrayList<String>();

        for (int i = 0; i < attachedFiles.getRowCount(); i++) {
            existingFiles.add(attachedFiles.getText(i, 0));
        }
        return existingFiles;
    }

    public void fillReceivers() {
        if (reciversListBox.getText().length() == 0) {
            Window.alert(messages.mail_choose_recivers());
            return;
        }

        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue parse) {
                receivers = parse.isArray();
                confirmSendEmail();
            }
        };

        AuthResponder.get(constants, messages, callback, "reports/email.php?action=list&query="
                + reciversListBox.getText() + "&year=" + yearBox.getText());

    }

    protected void confirmSendEmail() {
        boolean ok = Window.confirm(messages.mail_confirm(String.valueOf(receivers.size())));

        if (ok) {
            clearSendToTable();

            sendEmails();
        }

    }

    private void clearSendToTable() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }
    }

    private void sendEmails() {
        String message = "Email sending: " + reciversListBox.getText() + " " + receivers.size();
        logger.info("email", message);

        if (emailSendStatusView == null) {
            emailSendStatusView = new EmailSendStatus();
        }

        emailSendStatusView.setAttachmends(getSelectedFiles());

        emailSendStatusView.show();
        emailSendStatusView.center();
        emailSendStatusView.sendEmails(reciversListBox.getText().equals("simulate"));
    }

    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();

        if (sender == sendButton) {
            fillReceivers();
        } else if (sender == attachButton) {
            chooseAttachments();
        } else if (sender == reSendButton) {
            resendFailedEmails();
        }
    }

    private void resendFailedEmails() {
        receivers = new JSONArray();
        int pos = 0;

        for (int row = 1; row < table.getRowCount(); row++) {
            if (table.getText(row, 2).equals(elements.failed())) {

                JSONObject failedOne = new JSONObject();
                failedOne.put("name", new JSONString(table.getText(row, 0)));
                failedOne.put("email", new JSONString(table.getText(row, 1)));

                receivers.set(pos++, failedOne);
            }
        }
        confirmSendEmail();
    }

    class EmailSendStatus extends DialogBox implements ClickHandler {

        private int currentIndex;
        private boolean pause;
        private FlexTable infoTable;
        private String attachmentsAsJSONString;
        private boolean simulate;

        EmailSendStatus() {
            DockPanel dp = new DockPanel();

            infoTable = new FlexTable();
            infoTable.setStyleName("tableborder");
            infoTable.setText(0, 0, elements.mail_sending());
            infoTable.setText(1, 0, elements.name());
            infoTable.setText(2, 0, elements.email());
            infoTable.getRowFormatter().setStyleName(0, "header");
            infoTable.setWidget(0, 1, ImageFactory.loadingImage("image_loading"));
            infoTable.getCellFormatter().setWidth(0, 1, "30px");
            infoTable.getColumnFormatter().setStyleName(1, "emailsendname");
            infoTable.getColumnFormatter().setStyleName(2, "emailsendemail");
            NamedButton cancelButton = new NamedButton("abort", elements.abort());
            cancelButton.addClickHandler(this);
            infoTable.setWidget(3, 0, cancelButton);

            dp.add(infoTable, DockPanel.NORTH);
            setWidget(dp);
        }

        public void setAttachmends(ArrayList<String> selectedFiles) {
            JSONArray attachments = new JSONArray();
            int pos = 0;
            for (String fileName : selectedFiles) {
                attachments.set(pos++, new JSONString(fileName));
            }

            attachmentsAsJSONString = URL.encode(attachments.toString());
        }

        public void onClick(ClickEvent event) {
            pause = true;

            boolean stopSending = Window.confirm(messages.mail_abort_confirm());

            if (!stopSending) {
                pause = false;
                sendOneEmail();
            } else {
                hide();
            }
        }

        public void sendEmails(boolean simulate) {
            this.simulate = simulate;
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
            infoTable.setText(0, 2, "(" + (receivers.size() - currentIndex) + ")");

            StringBuffer mailRequest = new StringBuffer();

            mailRequest.append("action=" + (simulate ? "simulatemail" : "email"));
            Util.addPostParam(mailRequest, "subject", URL.encode(titleBox.getText()));
            Util.addPostParam(mailRequest, "email", email);
            Util.addPostParam(mailRequest, "body", URL.encode(bodyBox.getText()));
            Util.addPostParam(mailRequest, "attachments", attachmentsAsJSONString);
            Util.addPostParam(mailRequest, "format", getFormat());
            Util.addPostParam(mailRequest, "header", Util.getSelected(headerSelect));
            Util.addPostParam(mailRequest, "footer", Util.getSelected(footerSelect));
            
            ServerResponseWithErrorFeedback callback = new ServerResponseWithErrorFeedback() {

                public void serverResponse(JSONValue value) {
                    JSONObject object = value.isObject();

                    fillSentLine(name, email);

                    if (!("1".equals(Util.str(object.get("status")))) || (simulate && Random.nextBoolean())) {
                        table.setStyleName("error");
                        table.setText(1, 2, elements.failed());
                    } else {
                        table.setText(1, 2, elements.ok());
                    }
                    if (receivers.size() > currentIndex) {
                        sleepThenSendOneEmail();
                    } else {
                        checkForErrors();
                        hide();
                    }
                }

                public void onError() {
                    fillSentLine(name, email);
                    table.setStyleName("error");
                    table.setText(1, 2, elements.failed());

                    if (receivers.size() > currentIndex) {
                        sleepThenSendOneEmail();
                    } else {
                        checkForErrors();
                        hide();
                    }
                }

                protected void sleepThenSendOneEmail() {
                    Timer t = new Timer() {

                        @Override
                        public void run() {
                            sendOneEmail();
                        }
                    };
                    t.schedule(1000);
                }

                private void fillSentLine(final String name, final String email) {
                    currentIndex++;
                    table.insertRow(1);
                    table.setText(1, 0, name);
                    table.setText(1, 1, email);

                    String style = (currentIndex % 2 == 0) ? "showlineposts2" : "showlineposts1";
                    table.getRowFormatter().setStyleName(1, style);
                }

            };

            AuthResponder.post(constants, messages, callback, mailRequest, "reports/email.php");
        }

    }

    protected void checkForErrors() {

        reSendButton.setEnabled(false);

        for (int row = 1; row < table.getRowCount(); row++) {
            if (table.getText(row, 2).equals(elements.failed())) {
                reSendButton.setEnabled(true);
                break;
            }
        }
    }

    public String getFormat() {
        if(radioFormatPlain.getValue()) {
            return "PLAIN";
        }
        if(radioFormatHTML.getValue()) {
            return "HTML";
        }
        return "WIKI";
    }

    protected void openSelectFilesForAttachment(JSONArray files, ArrayList<String> existingFiles) {
        if (pickAttachments == null) {
            pickAttachments = new PickAttachments();
        }
        pickAttachments.fillFiles(files, existingFiles);
        pickAttachments.show();
        pickAttachments.center();

    }

    class PickAttachments extends DialogBox implements ClickHandler {

        private FlexTable pickFilesTable;
        private NamedButton cancelButton;
        private NamedButton pickButton;

        PickAttachments() {
            VerticalPanel dp = new VerticalPanel();

            pickFilesTable = new FlexTable();
            pickFilesTable.setStyleName("tableborder");
            pickFilesTable.setTitle(elements.choose_attachments());

            pickFilesTable.getRowFormatter().setStyleName(0, "header");
            pickFilesTable.setHTML(0, 0, elements.files());
            pickFilesTable.setHTML(0, 1, elements.choose_files());

            dp.add(pickFilesTable);

            HorizontalPanel hp = new HorizontalPanel();
            dp.add(hp);

            cancelButton = new NamedButton("abort", elements.abort());
            cancelButton.addClickHandler(this);
            hp.add(cancelButton);

            pickButton = new NamedButton("choose_file", elements.choose_files());
            pickButton.addClickHandler(this);
            hp.add(pickButton);

            setWidget(dp);

        }

        public void fillFiles(JSONArray files, ArrayList<String> existingFiles) {
            while (pickFilesTable.getRowCount() > 1) {
                pickFilesTable.removeRow(1);
            }

            for (int i = 0; i < files.size(); i++) {

                String fileName = Util.str(files.get(i).isObject().get("name"));
                pickFilesTable.setText(i + 1, 0, fileName);

                CheckBox filePick = new CheckBox();

                filePick.setValue(existingFiles.contains(fileName));

                pickFilesTable.setWidget(i + 1, 1, filePick);
                pickFilesTable.getCellFormatter().setStyleName(i + 1, 1, "center");
            }
        }

        public void onClick(ClickEvent event) {
            Widget sender = (Widget) event.getSource();
            if (sender == cancelButton) {
                hide();
            } else if (sender == pickButton) {
                pickFiles();
            }
        }

        private void pickFiles() {
            ArrayList<String> fileNames = new ArrayList<String>();
            for (int row = 1; row < pickFilesTable.getRowCount(); row++) {
                CheckBox checkbox = (CheckBox) pickFilesTable.getWidget(row, 1);

                if (checkbox.getValue()) {
                    fileNames.add(pickFilesTable.getText(row, 0));
                }
            }
            setAttachedFiles(fileNames);
            hide();
        }

    }

    public void init() {
        fillHeaderFooterSelects();
    }

    private void fillHeaderFooterSelects() {
        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                fill(footerSelect, object.get("footers"));
                fill(headerSelect, object.get("headers"));
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/emailcontent.php?action=report_init");

    }

    protected void fill(ListBoxWithErrorText listbox, JSONValue content) {
        listbox.clear();
        listbox.addItem(elements.email_choice_none(), "0");

        JSONArray array = content.isArray();
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject one = array.get(i).isObject();
            listbox.addItem(one.get("name"), one.get("id"));
        }
    }

}
