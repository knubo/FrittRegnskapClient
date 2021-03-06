package no.knubo.accounting.client.invoice;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;

import com.google.gwt.core.client.GWT;

public class InvoiceStatus {

    public static final String INVOICE_SENT = "2";
    private static Elements elements;

    private static void setElements() {
        if (elements == null) {
            elements = (Elements) GWT.create(Elements.class);
        }
    }

    public static boolean invoiceNotSent(int val) {
        return val == 1;
    }
    
    public static boolean invoiceSent(int val) {
        return val == 2;
    }
    
    public static String invoiceStatus(int int1) {
        setElements();
        switch (int1) {
        case 1:
            return elements.invoice_status_not_sent();
        case 2:
            return elements.invoice_status_sent();
        case 3:
            return elements.invoice_status_deleted();
        case 4:
            return elements.invoice_status_paid();
        }
        return "???" + int1;
    }

    public static void fill(ListBoxWithErrorText box) {
        setElements();
        box.addItem(elements.invoice_status_not_sent(), "1");
        box.addItem(elements.invoice_status_sent(), INVOICE_SENT);
        box.addItem(elements.invoice_status_deleted(), "3");
        box.addItem(elements.invoice_status_paid(), "4");
    }

}
