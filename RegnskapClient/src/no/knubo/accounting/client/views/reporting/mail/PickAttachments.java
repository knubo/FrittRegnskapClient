package no.knubo.accounting.client.views.reporting.mail;

import java.util.ArrayList;

import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

class PickAttachments extends DialogBox implements ClickHandler {

    private final ReportMail reportMail;
    private FlexTable pickFilesTable;
    private NamedButton cancelButton;
    private NamedButton pickButton;

    PickAttachments(ReportMail reportMail) {
        this.reportMail = reportMail;
        VerticalPanel dp = new VerticalPanel();

        pickFilesTable = new FlexTable();
        pickFilesTable.setStyleName("tableborder");
        pickFilesTable.setTitle(this.reportMail.elements.choose_attachments());

        pickFilesTable.getRowFormatter().setStyleName(0, "header");
        pickFilesTable.setHTML(0, 0, this.reportMail.elements.files());
        pickFilesTable.setHTML(0, 1, this.reportMail.elements.choose_files());

        dp.add(pickFilesTable);

        HorizontalPanel hp = new HorizontalPanel();
        dp.add(hp);

        cancelButton = new NamedButton("abort", this.reportMail.elements.abort());
        cancelButton.addClickHandler(this);
        hp.add(cancelButton);

        pickButton = new NamedButton("choose_file", this.reportMail.elements.choose_files());
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

    @Override
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
        this.reportMail.setAttachedFiles(fileNames);
        hide();
    }

}