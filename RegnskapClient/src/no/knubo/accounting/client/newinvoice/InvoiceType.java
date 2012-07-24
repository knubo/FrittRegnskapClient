package no.knubo.accounting.client.newinvoice;

import com.google.gwt.core.client.GWT;

import no.knubo.accounting.client.Elements;

public enum InvoiceType {
    SEMESTER, YEAR, OTHER;

    public static InvoiceType invoiceType(int type) {
        switch (type) {
        case 1:
            return SEMESTER;
        case 2:
            return YEAR;
        case 3:
            return OTHER;
        }
        throw new RuntimeException("Unknown invoice type:" + type);
    }

    private static Elements elements;

    public String getDesc() {
        if (elements == null) {
            elements = (Elements) GWT.create(Elements.class);
        }

        switch (this) {
        case SEMESTER:
            return elements.invoice_type_semester();
        case YEAR:
            return elements.invoice_type_year();
        case OTHER:
            return elements.invoice_type_other();
        default:
            return "???";
        }

    }
}
