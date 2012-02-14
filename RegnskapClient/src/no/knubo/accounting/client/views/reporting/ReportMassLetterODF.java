package no.knubo.accounting.client.views.reporting;

import java.util.ArrayList;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.views.files.UploadDelegate;
import no.knubo.accounting.client.views.files.UploadDelegateCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;

public class ReportMassLetterODF extends Composite implements ClickHandler, UploadDelegateCallback {
    private static ReportMassLetterODF reportInstance;
    private final Constants constants;
    private final I18NAccount messages;
    private FlexTable table;
    private ArrayList<RadioButton> radiobuttons;
    private NamedButton joinButton;
    UploadDelegate uploadDelegate;
    private ListBox yearListBox;
    private NamedButton previewButton;

    public static ReportMassLetterODF getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (reportInstance == null) {
            reportInstance = new ReportMassLetterODF(constants, messages, elements);
        }
        return reportInstance;
    }

    public ReportMassLetterODF(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;

        table = new FlexTable();
        table.setStyleName("edittable");

        table.setText(0, 0, elements.letter_template());
        table.getRowFormatter().setStyleName(0, "header");
        radiobuttons = new ArrayList<RadioButton>();

        DockPanel dp = new DockPanel();
        dp.add(table, DockPanel.NORTH);

        joinButton = new NamedButton("join_letters", elements.join_letters());
        joinButton.addClickHandler(this);
        joinButton.addStyleName("nowrap");

        previewButton = new NamedButton("preview", elements.preview());
        previewButton.addClickHandler(this);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hp.add(new Label(elements.choose_year()));
        yearListBox = new ListBox();
        hp.add(yearListBox);

        hp.add(joinButton);

        hp.add(previewButton);
        dp.add(hp, DockPanel.NORTH);

        uploadDelegate = new UploadDelegate("files/files.php", this, constants, messages, elements);

        AccountTable at = new AccountTable("tableborder airTop");
        at.setText(0, 0, elements.upload_template());
        at.setHeaderColStyle(0);
        at.setWidget(1, 0, uploadDelegate.getForm());
        dp.add(at, DockPanel.NORTH);

        initWidget(dp);
    }

    public void init() {
        while (table.getRowCount() > 1) {
            table.removeRow(1);
        }

        radiobuttons.clear();

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue parse) {
                JSONObject data = parse.isObject();
                setYears(data.get("years").isArray());

                JSONArray array = data.get("files").isArray();

                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.get(i).isObject();

                    RadioButton rb = new RadioButton("letters");

                    String filename = Util.str(obj.get("name"));

                    if (!filename.endsWith(".odt")) {
                        continue;
                    }

                    rb.setText(filename);
                    radiobuttons.add(rb);
                    table.setWidget(1 + i, 0, rb);
                    table.getCellFormatter().setStyleName(i + 1, 0, "desc");
                }
            }
        };

        AuthResponder.get(constants, messages, callback, "files/files.php?action=list&years=1");

    }

    protected void setYears(JSONArray array) {
        yearListBox.clear();

        for (int i = 0; i < array.size(); i++) {
            yearListBox.addItem(Util.str(array.get(i)));
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (joinButton == event.getSource()) {
            for (RadioButton rb : radiobuttons) {

                if (rb.getValue()) {
                    doLetter(rb.getText(), 0);
                }
            }
        }
        if (previewButton == event.getSource()) {
            for (RadioButton rb : radiobuttons) {

                if (rb.getValue()) {
                    doLetter(rb.getText(), 1);
                }
            }
        }
    }

    private void doLetter(String template, int preview) {
        Window.open(this.constants.baseurl() + "reports/massletterodf.php?&file=" + template + "&preview=" + preview,
                "_blank", "");
    }

    @Override
    public void uploadComplete() {
        init();
    }

    @Override
    public boolean uploadBody(String body) {
        return false;
    }

    @Override
    public void preUpload() {
        /* Not needed */
    }
}
