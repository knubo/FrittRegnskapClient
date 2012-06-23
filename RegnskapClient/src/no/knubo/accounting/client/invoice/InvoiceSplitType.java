package no.knubo.accounting.client.invoice;

import com.google.gwt.core.client.GWT;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;

public enum InvoiceSplitType {

    NONE, MONTH, QUARTER, HALF_YEAR;

    static InvoiceSplitType invoiceSplitType(int invoiceSplitType) {
        switch (invoiceSplitType) {
        case 0:
            return NONE;
        case 1:
            return MONTH;
        case 2:
            return QUARTER;
        case 3:
            return HALF_YEAR;
        }

        throw new RuntimeException("Unknown split type:" + invoiceSplitType);
    }

    private static Elements elements;

    public String getDesc() {
        setElements();

        switch (this) {
        case NONE:
            return "";
        case MONTH:
            return elements.invoice_split_type_monthly();
        case QUARTER:
            return elements.invoice_split_type_quarterly();
        case HALF_YEAR:
            return elements.invoice_split_type_half_year();
        }
        return "???";

    }

    private static void setElements() {
        if (elements == null) {
            elements = (Elements) GWT.create(Elements.class);
        }
    }

    public static void addSplitTypeItems(ListBoxWithErrorText splitType) {
        setElements();

        splitType.addItem("", "0");
        splitType.addItem(elements.invoice_split_type_monthly(), "1");
        splitType.addItem(elements.invoice_split_type_quarterly(), "2");
        splitType.addItem(elements.invoice_split_type_half_year(), "3");
    }

}
