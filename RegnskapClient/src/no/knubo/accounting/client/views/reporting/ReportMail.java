package no.knubo.accounting.client.views.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.Logger;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.misc.ServerResponseWithErrorFeedback;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.views.registers.EmailDefaultStyle;

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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportMail extends Composite implements ClickHandler {
    private static final String WIKI = "WIKI";
    private static final String HTML = "HTML";
    private static final String PLAIN = "PLAIN";
    private static final String EMAIL_TITLE = "email_title";
    private static final String EMAIL_FORMAT = "email_format";
    private static final String EMAIL_FOOTER = "email_footer";
    private static final String EMAIL_HEADER = "email_header";
    private static ReportMail reportInstance;
    private static boolean htmlVisible;
    private static boolean configuredStyle;
    private final Constants constants;
    private final Elements elements;
    private FlexTable table;
    ListBoxWithErrorText reciversListBox;
    TextBoxWithErrorText titleBox;
    NamedTextArea bodyBox;
    NamedTextArea richBodyBox;
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
    private NamedButton archiveButton;
    private Timer timer;
    private int savedHash;
    protected Integer archiveId;
    private NamedButton clearButton;
    private Label infoLabel;
    private boolean emailSent;
    private boolean replacedHTMLWidget;
    private NamedButton previewButton;

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
        bodyBox.setWidth("50em");
        bodyBox.setHeight("20em");
        bodyBox.setVisibleLines(30);

        richBodyBox = new NamedTextArea("html_area");
        richBodyBox.setWidth("80em");
        richBodyBox.setHeight("20em");

        richBodyBox.setVisible(false);

        FlowPanel fp = new FlowPanel();
        fp.add(bodyBox);
        fp.add(richBodyBox);
        mainTable.setWidget(3, 1, fp);
        mainTable.getFlexCellFormatter().setColSpan(3, 1, 2);

        attachButton = new NamedButton("attach_files", elements.attach_files());
        attachButton.addClickHandler(this);

        addHeaderFooterSelects(mainTable, 4);
        infoLabel = new Label();
        infoLabel.addStyleName("nowrap");
        mainTable.setWidget(4, 2, infoLabel);

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

    @Override
    public void setVisible(boolean visible) {
        reciversListBox.setFocus(true);
        super.setVisible(visible);
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

        radioFormatWiki.addClickHandler(this);
        radioFormatPlain.addClickHandler(this);
        radioFormatHTML.addClickHandler(this);

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

        archiveButton = new NamedButton("mail_archive", elements.email_archive());
        archiveButton.addClickHandler(this);

        clearButton = new NamedButton("mail_clear", elements.clear());
        clearButton.addClickHandler(this);

        previewButton = new NamedButton("preview", elements.preview());
        previewButton.addClickHandler(this);
        
        FlowPanel fp = new FlowPanel();

        fp.add(previewButton);
        fp.add(sendButton);
        fp.add(reSendButton);
        fp.add(archiveButton);
        fp.add(clearButton);

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

        StringBuffer sb = new StringBuffer("action=list");
        Util.addPostParam(sb, "query", reciversListBox.getText());
        Util.addPostParam(sb, "year", yearBox.getText());
        Util.addPostParam(sb, "emailSettings", buildEmailSettings());

        AuthResponder.post(constants, messages, callback, sb, "reports/email.php");

    }

    protected void setDefaultValues(JSONObject object) {
        if (object == null) {
            return;
        }
        String footer = Util.str(object.get(EMAIL_FOOTER));
        String header = Util.str(object.get(EMAIL_HEADER));
        String format = Util.str(object.get(EMAIL_FORMAT));
        String title = Util.str(object.get(EMAIL_TITLE));

        if (title == null || title.length() == 0) {
            return;
        }

        titleBox.setText(title);
        Util.setIndexByValue(headerSelect.getListbox(), header);
        Util.setIndexByValue(footerSelect.getListbox(), footer);

        if (PLAIN.equals(format)) {
            radioFormatPlain.setValue(true);
            onClick(new ClickEvent() {
                @Override
                public Object getSource() {
                    return radioFormatPlain;
                }
            });
        } else if (HTML.equals(format)) {
            radioFormatHTML.setValue(true);
            onClick(new ClickEvent() {
                @Override
                public Object getSource() {
                    return radioFormatHTML;
                }
            });

        } else if (WIKI.equals(format)) {
            radioFormatWiki.setValue(true);
            onClick(new ClickEvent() {
                @Override
                public Object getSource() {
                    return radioFormatWiki;
                }
            });

        }
    }

    private String buildEmailSettings() {
        JSONObject obj = new JSONObject();
        obj.put(EMAIL_HEADER, new JSONString(Util.getSelected(headerSelect)));
        obj.put(EMAIL_FOOTER, new JSONString(Util.getSelected(footerSelect)));
        obj.put(EMAIL_FORMAT, new JSONString(getFormat()));
        obj.put(EMAIL_TITLE, new JSONString(titleBox.getText()));

        return obj.toString();
    }

    protected void confirmSendEmail() {
        boolean ok = Window.confirm(messages.mail_confirm(String.valueOf(receivers.size())));

        if (ok) {
            clearSendToTable();
            emailSent = true;
            saveDraftIfNeeded();

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
            
        } else if(sender == previewButton) {
            preview();
        } else if (sender == attachButton) {
            chooseAttachments();
        } else if (sender == reSendButton) {
            resendFailedEmails();
        } else if (sender == radioFormatHTML) {
            if (bodyBox.isVisible()) {
                bodyBox.setVisible(false);

                if (!replacedHTMLWidget) {
                    replacedHTMLWidget = true;
                    setupRichEditor();
                }
                setRichEditorVisible(true);

            }
        } else if (sender == radioFormatPlain || sender == radioFormatWiki) {
            if (htmlVisible) {
                bodyBox.setVisible(true);
                setRichEditorVisible(false);
            }
        } else if (sender == archiveButton) {
            openArchiveDialog();
        } else if (sender == clearButton) {
            clearEmail();
        }
    }

    private void preview() {
        StringBuffer mailRequest = new StringBuffer();

        mailRequest.append("action=preview");
        String emailText = getFixedEmailText();
        fillEmailText(mailRequest, emailText);
        
        ServerResponseString callback = new ServerResponseString() {

            public void serverResponse(String response) {
                DialogBox popup = new DialogBox();
                popup.setText(elements.preview_actual());
                popup.setAutoHideEnabled(true);
                popup.setModal(true);
                popup.add(new HTML(response));
                popup.center();
            }

            public void serverResponse(JSONValue responseObj) {
                /* unused */
            }
            
        };
        AuthResponder.post(constants, messages, callback, mailRequest, "reports/email.php");

    }

    private void clearEmail() {

        boolean ok = Window.confirm(messages.confirm_clear());

        if (!ok) {
            return;
        }

        doClearEmail();
    }

    private void doClearEmail() {
        emailSent = false;
        archiveId = null;
        bodyBox.setText("");
        richBodyBox.setText("");
        titleBox.setText("");
        infoLabel.setText("");
        savedHash = 0;
    }

    private void openArchiveDialog() {
        timer.cancel();
        EmailArchivePopup archivePopup = new EmailArchivePopup(this, elements, constants, messages);
        archivePopup.init();
    }

    private void resendFailedEmails() {
        receivers = new JSONArray();
        int pos = 0;

        for (int row = 1; row < table.getRowCount(); row++) {
            if (table.getText(row, 2).equals(elements.failed())) {

                JSONObject failedOne = new JSONObject();
                failedOne.put("name", new JSONString(table.getText(row, 0)));
                failedOne.put("email", new JSONString(table.getText(row, 1)));
                failedOne.put("id", new JSONString(table.getText(row, 3)));

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
        private String emailText;

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

            emailText = getFixedEmailText();

            sendOneEmail();
        }

        private void sendOneEmail() {
            if (pause) {
                return;
            }
            JSONObject user = receivers.get(currentIndex).isObject();

            final String name = Util.str(user.get("name"));
            final String personId = Util.str(user.get("id"));
            final String email = Util.str(user.get("email"));

            infoTable.setText(1, 1, name);
            infoTable.setText(2, 1, email);
            infoTable.setText(0, 2, "(" + (receivers.size() - currentIndex) + ")");

            StringBuffer mailRequest = new StringBuffer();

            mailRequest.append("action=" + (simulate ? "simulatemail" : "email"));
            Util.addPostParam(mailRequest, "personid", personId);
            Util.addPostParam(mailRequest, "email", email);
            fillEmailText(mailRequest, emailText);
            Util.addPostParam(mailRequest, "attachments", attachmentsAsJSONString);

            ServerResponseWithErrorFeedback callback = new ServerResponseWithErrorFeedback() {

                public void serverResponse(JSONValue value) {
                    JSONObject object = value.isObject();

                    fillSentLine(name, email, personId);

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
                    fillSentLine(name, email, personId);
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

                private void fillSentLine(final String name, final String email, String id) {
                    currentIndex++;
                    table.insertRow(1);
                    table.setText(1, 0, name);
                    table.setText(1, 1, email);
                    table.setText(1, 3, id);
                    table.getCellFormatter().setStyleName(1, 3, "hidden");

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
        if (radioFormatPlain.getValue()) {
            return PLAIN;
        }
        if (radioFormatHTML.getValue()) {
            return HTML;
        }
        return WIKI;
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
        fillFooterAndHeader();
        fillStyle();

        setupTimer();

    }

    private void fillStyle() {
        ServerResponseString callback = new ServerResponseString() {

            public void serverResponse(JSONValue responseObj) {
                /* Unused */
            }

            public void serverResponse(String response) {
                if (response.trim().length() == 0) {
                    configStyles(EmailDefaultStyle.DEFAULT, "my_style");
                } else {
                    configStyles(response, "my_style");
                }
                
                setHTML("");
            }
        };
        AuthResponder.get(constants, messages, callback, "files/files.php?action=gettext&file=style.js");
    }

    private void fillFooterAndHeader() {
        ServerResponse callback = new ServerResponse() {
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                fill(footerSelect, object.get("footers"));
                fill(headerSelect, object.get("headers"));
                setDefaultValues(object.get("profile").isObject());
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/emailcontent.php?action=report_init");
    }

    private void setupTimer() {
        timer = new Timer() {

            @Override
            public void run() {
                if (!reportInstance.isVisible()) {
                    timer.cancel();
                    return;
                }
                try {
                    saveDraftIfNeeded();
                } catch (Exception e) {
                    Util.log(e.toString());
                }
            }

        };
        timer.scheduleRepeating(60 * 1000);
    }

    protected void saveDraftIfNeeded() {
        String stringToHash = radioFormatHTML.getValue() ? getHTML() : bodyBox.getText();

        if (stringToHash.trim().length() == 0) {
            return;
        }
        int newHash = stringToHash.hashCode();

        Util.log("Hash is:" + newHash);
        if (savedHash == newHash) {
            return;
        }

        savedHash = newHash;

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();
                archiveId = Util.getInt(object.get("insert_id"));

                infoLabel.setText(messages.draft_saved(new Date().toString()));

            }
        };

        StringBuffer params = new StringBuffer();
        params.append("action=archive_save");

        fillEmailText(params, getFixedEmailText());
        Util.addPostParam(params, "sent", emailSent ? "1" : "0");

        if (archiveId != null) {
            Util.addPostParam(params, "id", String.valueOf(archiveId));
        }

        AuthResponder.post(constants, messages, callback, params, "reports/email.php");

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

    private void fillEmailText(StringBuffer mailRequest, String emailText) {
        Util.addPostParam(mailRequest, "subject", URL.encode(titleBox.getText()));
        Util.addPostParam(mailRequest, "body", emailText);
        Util.addPostParam(mailRequest, "format", getFormat());
        Util.addPostParam(mailRequest, "header", Util.getSelected(headerSelect));
        Util.addPostParam(mailRequest, "footer", Util.getSelected(footerSelect));
    }

    private String getFixedEmailText() {
        if (radioFormatHTML.getValue()) {
            String html = getHTML();

            html = html.replace("\n", "");
            html = html.replace("<br", "\n<br");
            html = html.replace("</p", "\n</p");

            return URL.encode(html);
        }
        return URL.encode(bodyBox.getText());
    }

    public void editEmail(JSONObject object) {
        doClearEmail();

        archiveId = Util.getInt(object.get("id"));
        titleBox.setText(Util.str(object.get("subject")));

        String format = Util.str(object.get("format"));

        if (format.equals(PLAIN)) {
            bodyBox.setVisible(true);
            setRichEditorVisible(false);
            radioFormatPlain.setValue(true);
        } else if (format.equals(HTML)) {
            bodyBox.setVisible(false);
            setRichEditorVisible(true);
            radioFormatHTML.setValue(true);
        } else if (format.equals(WIKI)) {
            radioFormatWiki.setValue(true);
            setRichEditorVisible(false);
            radioFormatPlain.setValue(true);
        }

        if (radioFormatHTML.getValue()) {
            setHTML(Util.str(object.get("body")));
        } else {
            bodyBox.setText(Util.str(object.get("body")));
        }

        String header = Util.str(object.get("header"));
        String footer = Util.str(object.get("footer"));

        Util.setIndexByValue(headerSelect.getListbox(), header);
        Util.setIndexByValue(footerSelect.getListbox(), footer);
        setupTimer();
    }

    private static native String getHTML()
    /*-{

        return $wnd['CKEDITOR'].instances.html_area.getData();
    }-*/;

    public static native void setHTML(String x)
    /*-{
       $wnd['CKEDITOR'].instances.html_area.setData(x);
    }-*/;

    public static void setRichEditorVisible(boolean visible) {
        htmlVisible = visible;
        setRichEditorVisibleNative(visible);
    }

    public static native void setRichEditorVisibleNative(boolean visible)
    /*-{
       if($doc.getElementById('cke_html_area')) 
          $doc.getElementById('cke_html_area').style.display = visible ? '' : 'none';
       }-*/;

    static native void setupRichEditor()
    /*-{
       $wnd['CKEDITOR'].replace( 'html_area' );
     
    }-*/;

    static void configStyles(String styles, String id) {
        if(configuredStyle) {
            return;
        }
        configuredStyle = true;
        configStylesInt(styles, id);
    }
    static native void configStylesInt(String styles, String id)
    /*-{
       $wnd['CKEDITOR'].stylesSet.add( id, eval("["+styles+"]"));
       $wnd['CKEDITOR'].config.stylesSet = id;
    }-*/;

}
