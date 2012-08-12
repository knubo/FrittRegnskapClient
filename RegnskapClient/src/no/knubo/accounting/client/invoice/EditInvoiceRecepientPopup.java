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
    private String receiverId;
    private final InvoiceSearchView invoiceSearchView;

    EditInvoiceRecepientPopup(I18NAccount messages, Constants constants, Elements elements,
            InvoiceSearchView invoiceSearchView) {
        this.messages = messages;
        this.constants = constants;
        this.invoiceSearchView = invoiceSearchView;
        setText(elements.change_status());

        AccountTable table = new AccountTable("edittable");

        table.setText(0, 0, elements.status());
        status = new ListBoxWithErrorText("status");
        table.setWidget(0, 1, status);

        InvoiceStatus.fill(status);

        HorizontalPanel hp = new HorizontalPanel();

        saveButton = new NamedButton("save", elements.save());
        saveButton.addClickHandler(this);
        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);

        hp.add(saveButton);
        hp.add(cancelButton);

        label = new Label();
        table.setWidget(1, 1, label);
        table.setWidget(2, 1, hp);

        setWidget(table);
    }

    public void setReceiverIdAndShow(ClickEvent event, String receiverId) {
        this.receiverId = receiverId;
        setPopupPosition(event.getClientX(), event.getClientY());

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                Util.setIndexByValue(status.getListbox(), Util.str(responseObj.isObject().get("invoice_status")));
                show();
            }
        };
        AuthResponder.get(constants, messages, callback, "accounting/invoice_ops.php?action=invoice&receiver_id="
                + receiverId);

    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == cancelButton) {
            hide();
        } else if (event.getSource() == saveButton) {
            save();
        }
    }

    private void save() {
        final String newStatus = Util.getSelected(status);

        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                if ("1".equals(Util.str(responseObj.isObject().get("status")))) {
                    hide();
                    invoiceSearchView.updateInvoiceStatus(receiverId, Integer.parseInt(newStatus));
                } else {
                    label.setText(messages.save_failed());
                    Util.timedMessage(label, "", 30);
                }
            }
        };

        StringBuffer sb = new StringBuffer();
        sb.append("action=change_invoice_status");

        Util.addPostParam(sb, "receiver_id", receiverId);
        Util.addPostParam(sb, "status", newStatus);

        AuthResponder.post(constants, messages, callback, sb, "accounting/invoice_ops.php");
    }
}
