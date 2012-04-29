package no.knubo.accounting.client.invoice;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InvoiceSettings extends Composite implements ClickHandler {
    private static InvoiceSettings me;
    private NamedButton emailTemplate;
    private final ViewCallback callback;

    public InvoiceSettings(I18NAccount messages, Constants constants, Elements elements, ViewCallback callback) {
        this.callback = callback;
        VerticalPanel dp = new VerticalPanel();

        AccountTable table = new AccountTable("edittable");
        dp.add(table);

        emailTemplate = new NamedButton("invoice_edit_email_template", elements.invoice_edit_email_template());
        emailTemplate.addClickHandler(this);
        table.setWidget(0, 0, emailTemplate);

        initWidget(dp);
    }

    public static InvoiceSettings getInstance(I18NAccount messages, Constants constants, Elements elements,
            ViewCallback callback) {
        if (me == null) {
            me = new InvoiceSettings(messages, constants, elements, callback);
        }
        return me;
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == emailTemplate) {
            callback.editEmailTemplateInvoice();
        }
    }

    public void init() {
        // TODO Auto-generated method stub

    }

}
