package no.knubo.accounting.client.views.reporting.mail;

import java.util.ArrayList;

import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.misc.ServerResponseWithErrorFeedback;
import no.knubo.accounting.client.ui.NamedButton;

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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;

class EmailSendStatus extends DialogBox implements ClickHandler {

    private final ReportMail reportMail;
    private int currentIndex;
    private boolean pause;
    private FlexTable infoTable;
    private String attachmentsAsJSONString;
    private boolean simulate;
    private String emailText;

    EmailSendStatus(ReportMail reportMail) {
        this.reportMail = reportMail;
        DockPanel dp = new DockPanel();

        infoTable = new FlexTable();
        infoTable.setStyleName("tableborder");
        infoTable.setText(0, 0, this.reportMail.elements.mail_sending());
        infoTable.setText(1, 0, this.reportMail.elements.name());
        infoTable.setText(2, 0, this.reportMail.elements.email());
        infoTable.getRowFormatter().setStyleName(0, "header");
        infoTable.setWidget(0, 1, ImageFactory.loadingImage("image_loading"));
        infoTable.getCellFormatter().setWidth(0, 1, "30px");
        infoTable.getColumnFormatter().setStyleName(1, "emailsendname");
        infoTable.getColumnFormatter().setStyleName(2, "emailsendemail");
        NamedButton cancelButton = new NamedButton("abort", this.reportMail.elements.abort());
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

    @Override
    public void onClick(ClickEvent event) {
        pause = true;

        boolean stopSending = Window.confirm(this.reportMail.messages.mail_abort_confirm());

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

        emailText = this.reportMail.getFixedEmailText();

        sendOneEmail();
    }

    private void sendOneEmail() {
        if (pause) {
            return;
        }
        JSONObject user = this.reportMail.receivers.get(currentIndex).isObject();

        final String name = Util.str(user.get("name"));
        final String personId = Util.str(user.get("id"));
        final String email = Util.str(user.get("email"));

        infoTable.setText(1, 1, name);
        infoTable.setText(2, 1, email);
        infoTable.setText(0, 2, "(" + (this.reportMail.receivers.size() - currentIndex) + ")");

        StringBuffer mailRequest = new StringBuffer();

        mailRequest.append("action=" + (simulate ? "simulatemail" : "email"));
        Util.addPostParam(mailRequest, "personid", personId);
        Util.addPostParam(mailRequest, "email", email);
        this.reportMail.fillEmailText(mailRequest, emailText);
        Util.addPostParam(mailRequest, "attachments", attachmentsAsJSONString);

        ServerResponseWithErrorFeedback callback = new ServerResponseWithErrorFeedback() {

            @Override
            public void serverResponse(JSONValue value) {
                JSONObject object = value.isObject();

                fillSentLine(name, email, personId);

                if (!("1".equals(Util.str(object.get("status")))) || (simulate && Random.nextBoolean())) {
                    EmailSendStatus.this.reportMail.table.setStyleName("error");
                    EmailSendStatus.this.reportMail.table.setText(1, 2,
                            EmailSendStatus.this.reportMail.elements.failed());
                } else {
                    EmailSendStatus.this.reportMail.table.setText(1, 2, EmailSendStatus.this.reportMail.elements.ok());
                }
                if (EmailSendStatus.this.reportMail.receivers.size() > currentIndex) {
                    sleepThenSendOneEmail();
                } else {
                    EmailSendStatus.this.reportMail.checkForErrors();
                    hide();
                }
            }

            @Override
            public void onError() {
                fillSentLine(name, email, personId);
                EmailSendStatus.this.reportMail.table.setStyleName("error");
                EmailSendStatus.this.reportMail.table.setText(1, 2, EmailSendStatus.this.reportMail.elements.failed());

                if (EmailSendStatus.this.reportMail.receivers.size() > currentIndex) {
                    sleepThenSendOneEmail();
                } else {
                    EmailSendStatus.this.reportMail.checkForErrors();
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
                EmailSendStatus.this.reportMail.table.insertRow(1);
                EmailSendStatus.this.reportMail.table.setText(1, 0, name);
                EmailSendStatus.this.reportMail.table.setText(1, 1, email);
                EmailSendStatus.this.reportMail.table.setText(1, 3, id);
                EmailSendStatus.this.reportMail.table.getCellFormatter().setStyleName(1, 3, "hidden");

                String style = (currentIndex % 2 == 0) ? "showlineposts2" : "showlineposts1";
                EmailSendStatus.this.reportMail.table.getRowFormatter().setStyleName(1, style);
            }

        };

        AuthResponder.post(this.reportMail.constants, this.reportMail.messages, callback, mailRequest,
                "reports/email.php");
    }

}