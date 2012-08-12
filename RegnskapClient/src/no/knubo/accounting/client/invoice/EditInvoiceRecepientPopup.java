package no.knubo.accounting.client.invoice;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

class EditInvoiceRecepientPopup extends DialogBox implements ClickHandler {

    private ListBoxWithErrorText status;
    private NamedButton saveButton;
    private NamedButton cancelButton;
    private Label label;
    private final Constants constants;
    private final I18NAccount messages;

    EditInvoiceRecepientPopup(I18NAccount messages, Constants constants, Elements elements) {
        this.messages = messages;
        this.constants = constants;
        setText(elements.change_status());

        AccountTable table = new AccountTable("edittable");

        table.setText(0, 0, elements.status());
        status = new ListBoxWithErrorText("status");
        table.setWidget(0, 1, status);

        InvoiceStatus.fill(status);

        HorizontalPanel hp = new HorizontalPanel();

        saveButton = new NamedButton(elements.save());
        saveButton.addClickHandler(this);
        cancelButton = new NamedButton(elements.cancel());
        cancelButton.addClickHandler(this);

        hp.add(saveButton);
        hp.add(cancelButton);

        label = new Label();
        table.setWidget(1, 1, label);
        table.setWidget(2, 1, hp);
    }

    public void setReceiverIdAndShow(ClickEvent event, String receiverId) {
        setPopupPosition(event.getClientX(), event.getClientY());

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                Util.setIndexByValue(status.getListbox(), Util.str(responseObj.isObject().get("status")));
                show();
            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=invoice&receiver_id=" + receiverId);

    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            hide();
        } else if(event.getSource() == saveButton) {
            save();
        }
    }

    private void save() {
        
    }
}
