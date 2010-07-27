package no.knubo.accounting.client.views.reporting;

import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.user.client.ui.DialogBox;

public class StructuralTableEditDialogBox extends DialogBox {

    public StructuralTableEditDialogBox() {
        setText("Table");

        AccountTable table = new AccountTable("tableborder");
        table.setText(0, 0, "Font st¿rrelse");
        table.setText(1, 0, "Vis overskrift");
        table.setText(2, 0, "Vis linjer");
        table.setText(3, 0, "X-posisjon");
        table.setText(4, 0, "X-orientering");
        table.setText(5, 0, "Skygger");
    }
}
