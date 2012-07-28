package no.knubo.accounting.client.views.reporting.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.CKEditorFunctions;
import no.knubo.accounting.client.misc.CallbackComplete;
import no.knubo.accounting.client.misc.Logger;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.misc.ServerResponseString;
import no.knubo.accounting.client.misc.WidgetIds;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.NamedTextArea;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.views.ViewCallback;
import no.knubo.accounting.client.views.registers.EmailDefaultStyle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
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
    final Constants constants;
    final Elements elements;
    FlexTable table;
    ListBoxWithErrorText reciversListBox;
    TextBoxWithErrorText titleBox;
    NamedTextArea bodyBox;
    NamedTextArea richBodyBox;
    protected JSONArray receivers;
    private EmailSendStatus emailSendStatusView;
    I18NAccount messages;
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
    private FlexTable mainTable;
    private NamedButton saveTemplateButton;
    private NamedButton cancelTemplateButton;
    private final ViewCallback callback;
    private String callerID;

    public static ReportMail getInstance(Constants constants, I18NAccount messages, Elements elements,
            ViewCallback callback) {
        if (reportInstance == null) {
            reportInstance = new ReportMail(constants, messages, elements, callback);
        }
        return reportInstance;
    }

    public ReportMail(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.callback = callback;
        this.logger = new Logger(this.constants);

        mainTable = new FlexTable();
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
        addSaveTemplateButtons(mainTable, 9);

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
    protected void onLoad() {
        super.onLoad();

        if (!replacedHTMLWidget) {
            Util.log("Setting up rich editor");
            replacedHTMLWidget = true;
            setupRichEditor();
        }

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

    private void addSaveTemplateButtons(FlexTable mainTable, int row) {
        FlowPanel fp = new FlowPanel();

        saveTemplateButton = new NamedButton("save", elements.save());
        saveTemplateButton.addClickHandler(this);

        cancelTemplateButton = new NamedButton("cancel", elements.cancel());
        cancelTemplateButton.addClickHandler(this);

        fp.add(saveTemplateButton);
        fp.add(cancelTemplateButton);

        mainTable.setWidget(row, 1, fp);
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

            @Override
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

            @Override
            public void serverResponse(JSONValue parse) {
                receivers = parse.isArray();
                confirmSendEmail();
            }
        };

        StringBuffer sb = new StringBuffer("action=list");
        Util.addPostParam(sb, "query", reciversListBox.getText());
        Util.addPostParam(sb, "year", yearBox.getText());
        Util.addPostParam(sb, "emailSettings", buildEmailSettings().toString());

        AuthResponder.post(constants, messages, callback, sb, "reports/email.php");

    }

    protected void setDefaultValues(JSONObject object) {
        if (object == null) {
            setEmailText(PLAIN, "");
            return;
        }
        String footer = Util.str(object.get(EMAIL_FOOTER));
        String header = Util.str(object.get(EMAIL_HEADER));
        String format = Util.str(object.get(EMAIL_FORMAT));
        String title = Util.str(object.get(EMAIL_TITLE));

        if (title == null || title.length() == 0) {
            title = "";
        }

        setEmailText(format, "");

        titleBox.setText(title);
        Util.setIndexByValue(headerSelect.getListbox(), header);
        Util.setIndexByValue(footerSelect.getListbox(), footer);

    }

    private JSONObject buildEmailSettings() {
        JSONObject obj = new JSONObject();
        obj.put(EMAIL_HEADER, new JSONString(Util.getSelected(headerSelect)));
        obj.put(EMAIL_FOOTER, new JSONString(Util.getSelected(footerSelect)));
        obj.put(EMAIL_FORMAT, new JSONString(getFormat()));
        obj.put(EMAIL_TITLE, new JSONString(titleBox.getText()));

        return obj;
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
            emailSendStatusView = new EmailSendStatus(this);
        }

        emailSendStatusView.setAttachmends(getSelectedFiles());

        emailSendStatusView.show();
        emailSendStatusView.center();
        emailSendStatusView.sendEmails(reciversListBox.getText().equals("simulate"));
    }

    @Override
    public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();

        if (sender == sendButton) {
            fillReceivers();

        } else if (sender == previewButton) {
            preview();
        } else if (sender == attachButton) {
            chooseAttachments();
        } else if (sender == reSendButton) {
            resendFailedEmails();
        } else if (sender == radioFormatHTML) {
            if (bodyBox.isVisible()) {
                bodyBox.setVisible(false);

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
        } else if (sender == saveTemplateButton) {
            saveTemplate();
        } else if (sender == cancelTemplateButton) {
            cancelTemplate();
        }
    }

    private void cancelTemplate() {
        callback.openView(WidgetIds.INVOICE_SETTINGS, elements.menuitem_settings_invoice(), callerID);
    }

    private void saveTemplate() {
        JSONObject emailSettings = buildEmailSettings();
        emailSettings.put("id", new JSONString(callerID));
        emailSettings.put("body", new JSONString(radioFormatHTML.getValue() ? getHTML() : bodyBox.getText()));

        callback.openView(WidgetIds.INVOICE_SETTINGS, elements.menuitem_settings_invoice(), emailSettings);
    }

    public HorizontalPanel addSizeSelect(final ScrollPanel sp, final DialogBox popup) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();

        for (int i = 200; i <= 1200; i += 200) {
            RadioButton radioButton = new RadioButton("width_select", i + "px");
            final int setWidth = i;

            if (i == 600) {
                radioButton.setValue(true);
            }

            radioButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    sp.setWidth(setWidth + "px");
                }
            });

            horizontalPanel.add(radioButton);
        }

        RadioButton closeButton = new RadioButton("width_select", elements.close());
        closeButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                popup.hide();
            }

        });
        horizontalPanel.add(closeButton);

        return horizontalPanel;
    }

    private void preview() {
        StringBuffer mailRequest = new StringBuffer();

        mailRequest.append("action=preview");
        String emailText = getFixedEmailText();
        fillEmailText(mailRequest, emailText);

        ServerResponseString callback = new ServerResponseString() {

            @Override
            public void serverResponse(String response) {

                DialogBox popup = new DialogBox();
                popup.setText(elements.preview_actual());
                popup.setAutoHideEnabled(true);
                popup.setModal(true);

                VerticalPanel vp = new VerticalPanel();

                ScrollPanel sp = new ScrollPanel();
                vp.add(addSizeSelect(sp, popup));
                vp.add(sp);

                sp.setWidth("600px");
                sp.setHeight("40em");
                sp.add(new HTML(response));

                popup.add(vp);
                popup.center();
            }

            @Override
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
            pickAttachments = new PickAttachments(this);
        }
        pickAttachments.fillFiles(files, existingFiles);
        pickAttachments.show();
        pickAttachments.center();

    }

    public void initSendingEmail() {

        mainTable.getRowFormatter().setVisible(0, true);
        mainTable.getRowFormatter().setVisible(6, true);
        mainTable.getRowFormatter().setVisible(7, true);
        mainTable.getRowFormatter().setVisible(8, true);
        mainTable.getRowFormatter().setVisible(9, false);
        table.setVisible(true);

        fillStyle();

        CallbackComplete complete = new CallbackComplete() {

            @Override
            public void complete(final JSONObject obj) {
                new Timer() {

                    @Override
                    public void run() {
                        setDefaultValues(obj);
                    }

                }.schedule(500);
            }

        };
        fillFooterAndHeader(complete);
        setupTimerSaveDraft();
    }

    public void initEditEmailTemplate(final String id) {

        this.callerID = id;

        mainTable.getRowFormatter().setVisible(0, false);
        mainTable.getRowFormatter().setVisible(6, false);
        mainTable.getRowFormatter().setVisible(7, false);
        mainTable.getRowFormatter().setVisible(8, false);
        mainTable.getRowFormatter().setVisible(9, true);
        table.setVisible(false);
        doClearEmail();

        CallbackComplete complete = new CallbackComplete() {

            @Override
            public void complete(JSONObject obj) {

                loadEmailTemplate(id);
            }

        };

        fillStyle();
        fillFooterAndHeader(complete);

        setupTimerKeepalive();
    }

    private void loadEmailTemplate(String id) {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONObject obj = responseObj.isObject();

                titleBox.setText(Util.strSkipNull(obj.get("email_subject")));
                final String body = Util.strSkipNull(obj.get("email_body"));

                Util.setIndexByValue(headerSelect.getListbox(), Util.strSkipNull(obj.get("email_header")));
                Util.setIndexByValue(footerSelect.getListbox(), Util.strSkipNull(obj.get("email_footer")));
                String format = Util.strSkipNull(obj.get("email_format"));

                if (format == null || format.length() == 0) {
                    format = PLAIN;
                }

                final String finalFormat = format;

                new Timer() {

                    @Override
                    public void run() {
                        setEmailText(finalFormat, body);
                    }

                }.schedule(500);
            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=emailtemplate&id=" + id);
    }

    private void fillStyle() {
        ServerResponseString callback = new ServerResponseString() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                /* Unused */
            }

            @Override
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

    private void fillFooterAndHeader(final CallbackComplete complete) {
        ServerResponse callback = new ServerResponse() {
            @Override
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                fill(footerSelect, object.get("footers"));
                fill(headerSelect, object.get("headers"));

                if (complete != null) {
                    complete.complete(object.get("profile").isObject());
                }
            }

        };

        AuthResponder.get(constants, messages, callback, "registers/emailcontent.php?action=report_init");
    }

    private void setupTimerKeepalive() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer() {

            @Override
            public void run() {
                if (!reportInstance.isVisible()) {
                    timer.cancel();
                    return;
                }
                AuthResponder.get(constants, messages, AuthResponder.NULL_RESPONSE,
                        "accounting/invoice_ops.php?action=keepalive");
            }

        };
        timer.scheduleRepeating(60 * 1000);
    }

    private void setupTimerSaveDraft() {
        if (timer != null) {
            timer.cancel();
        }

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

            @Override
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

    void fillEmailText(StringBuffer mailRequest, String emailText) {
        Util.addPostParam(mailRequest, "subject", URL.encode(titleBox.getText()));
        Util.addPostParam(mailRequest, "body", emailText);
        Util.addPostParam(mailRequest, "format", getFormat());
        Util.addPostParam(mailRequest, "header", Util.getSelected(headerSelect));
        Util.addPostParam(mailRequest, "footer", Util.getSelected(footerSelect));
    }

    String getFixedEmailText() {
        if (radioFormatHTML.getValue()) {
            String html = getHTML();

            // html = html.replace("\n", "");
            // html = html.replace("<br", "\n<br");
            // html = html.replace("</p", "\n</p");

            return URL.encode(html);
        }
        return URL.encode(bodyBox.getText());
    }

    public void editEmail(JSONObject object) {
        doClearEmail();

        archiveId = Util.getInt(object.get("id"));
        titleBox.setText(Util.str(object.get("subject")));

        String format = Util.str(object.get("format"));
        String content = Util.str(object.get("body"));

        setEmailText(format, content);

        String header = Util.str(object.get("header"));
        String footer = Util.str(object.get("footer"));

        Util.setIndexByValue(headerSelect.getListbox(), header);
        Util.setIndexByValue(footerSelect.getListbox(), footer);
        setupTimerSaveDraft();
    }

    private void setEmailText(String format, String content) {
        if (format.equals(PLAIN)) {
            Util.log("Plain text email");
            richBodyBox.setVisible(false);
            bodyBox.setVisible(true);
            setRichEditorVisible(false);
            radioFormatHTML.setValue(false);
            radioFormatPlain.setValue(true);
            onClick(new ClickEvent() {
                @Override
                public Object getSource() {
                    return radioFormatPlain;
                }
            });
        } else if (format.equals(HTML)) {
            Util.log("HTML email ");
            richBodyBox.setVisible(false);
            bodyBox.setVisible(false);
            setRichEditorVisible(true);
            radioFormatPlain.setValue(false);
            radioFormatHTML.setValue(true);
            onClick(new ClickEvent() {
                @Override
                public Object getSource() {
                    return radioFormatHTML;
                }
            });

        } else if (format.equals(WIKI)) {
            Util.log("Wiki email");
            bodyBox.setVisible(true);
            setRichEditorVisible(false);
            radioFormatPlain.setValue(false);
            radioFormatWiki.setValue(true);
            onClick(new ClickEvent() {
                @Override
                public Object getSource() {
                    return radioFormatWiki;
                }
            });
        }

        if (radioFormatHTML.getValue()) {
            setHTML(content);
        } else {
            bodyBox.setText(content);
        }
    }

    public void setRichEditorVisible(boolean visible) {
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
        if (configuredStyle) {
            return;
        }
        configuredStyle = true;
        CKEditorFunctions.configStylesInt(styles, id);
    }

    public static native String getHTML()
    /*-{
    
        return $wnd['CKEDITOR'].instances.html_area.getData();
    }-*/;

    public static native void setHTML(String x)
    /*-{
       $wnd['CKEDITOR'].instances.html_area.setData(x);
    }-*/;

}
