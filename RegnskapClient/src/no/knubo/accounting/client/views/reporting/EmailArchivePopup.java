package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EmailArchivePopup extends DialogBox implements ClickHandler {

    private AccountTable table;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;
    private NamedButton archiveEditButton;
    private NamedButton archiveDeleteButton;
    private NamedButton closeButton;
    private final ReportMail caller;

    public EmailArchivePopup(ReportMail reportMail, Elements elements, Constants constants, I18NAccount messages) {
        this.caller = reportMail;
        this.elements = elements;
        this.constants = constants;
        this.messages = messages;
        setModal(true);
        setText(elements.email_archive());

        table = new AccountTable("tableborder");

        table.setHeader(0, 1, elements.mail_title());
        table.setHeader(0, 2, elements.mail_sendt());
        table.setHeader(0, 3, elements.user());
        table.setHeader(0, 4, elements.date());

        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setHeight("30em");
        scrollPanel.add(table);

        VerticalPanel vp = new VerticalPanel();
        vp.add(scrollPanel);

        HorizontalPanel hp = new HorizontalPanel();
        hp.addStyleName("buttonpanel");

        archiveEditButton = new NamedButton("archive_edit", elements.email_archive_edit());
        archiveEditButton.addClickHandler(this);
        hp.add(archiveEditButton);

        archiveDeleteButton = new NamedButton("archive_delete", elements.email_archive_delete());
        archiveDeleteButton.addClickHandler(this);
        hp.add(archiveDeleteButton);

        closeButton = new NamedButton("archive_close", elements.email_archive_close());
        closeButton.addClickHandler(this);
        hp.add(closeButton);

        vp.add(hp);
        add(vp);

    }

    public void init() {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                JSONArray array = responseObj.isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject archiveElement = array.get(i).isObject();

                    table.setWidget(i + 1, 0, new RadioButton("archives"));
                    table.setText(i + 1, 1, Util.str(archiveElement.get("subject")));

                    boolean sent = Util.getBoolean(archiveElement.get("sent"));
                    table.setText(i + 1, 2, sent ? elements.admin_yes() : elements.admin_no(), "center");
                    table.setText(i + 1, 3, Util.str(archiveElement.get("username")));
                    table.setText(i + 1, 4, Util.str(archiveElement.get("edit_time")));
                    table.setText(i + 1, 5, Util.str(archiveElement.get("id")), "hidden");
                }
                center();
            }
        };
        AuthResponder.get(constants, messages, callback, "reports/email.php?action=archive_list");
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == closeButton) {
            hide();
        } else if (event.getSource() == archiveEditButton) {
            archiveEdit();
        } else if (event.getSource() == archiveDeleteButton) {
            archiveDelete();
        }
    }

    private void archiveDelete() {
       
        String id = findSelectedId();

        if (id == null) {
            Window.alert(elements.email_select());
            return;
        }
        
        boolean deleteOk = Window.confirm(messages.email_delete_confirm());

        if (!deleteOk) {
            return;
        }


        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                deleteSelected();
            }
        };
        AuthResponder.get(constants, messages, callback, "reports/email.php?action=archive_delete&id=" + id);
    }

    private void archiveEdit() {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {

                boolean doEdit = Window.confirm(messages.email_edit_confirm());

                if (doEdit) {
                    hide();
                    caller.editEmail(responseObj.isObject());
                }
            }
        };
        String id = findSelectedId();

        if (id == null) {
            Window.alert("Velg en knapp");
            return;
        }

        AuthResponder.get(constants, messages, callback, "reports/email.php?action=archive_get&id=" + id);

    }

    private void deleteSelected() {
        for (int row = 1; row < table.getRowCount(); row++) {
            RadioButton button = (RadioButton) table.getWidget(row, 0);

            if (button.getValue()) {
                table.removeRow(row);
                return;
            }
        }
    }
    
    private String findSelectedId() {
        for (int row = 1; row < table.getRowCount(); row++) {
            RadioButton button = (RadioButton) table.getWidget(row, 0);

            if (button.getValue()) {
                return table.getText(row, 5);
            }
        }
        return null;
    }

}
