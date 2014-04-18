package no.knubo.accounting.client.newinvoice;

import com.google.gwt.core.client.GWT;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;

public enum InvoiceType {
	SEMESTER, YEAR, SEMESTER_YOUTH, YEAR_YOUTH, TRAIN, OTHER;

	public static InvoiceType invoiceType(int type) {
		switch (type) {
		case 1:
			return SEMESTER;
		case 2:
			return SEMESTER_YOUTH;
		case 3:
			return YEAR;
		case 4:
			return YEAR_YOUTH;
		case 5:
			return OTHER;
		case 6:
		    return TRAIN;
		}
		throw new RuntimeException("Unknown invoice type:" + type);
	}

	private static Elements elements = (Elements) GWT.create(Elements.class);

	public String getDesc() {
		switch (this) {
		case SEMESTER:
			return elements.invoice_type_semester();
		case SEMESTER_YOUTH:
			return elements.invoice_type_semester_youth();
		case YEAR:
			return elements.invoice_type_year();
		case YEAR_YOUTH:
			return elements.invoice_type_year_youth();
		case OTHER:
			return elements.invoice_type_other();
		case TRAIN:
		    return elements.train_membership();
		default:
			return "???";
		}
	}
	
	public static void addInvoiceTypes(ListBoxWithErrorText box) {
		box.addItem("", "");
		box.addItem(elements.invoice_type_semester(), "1");
		box.addItem(elements.invoice_type_semester_youth(), "2");
		box.addItem(elements.invoice_type_year(), "3");
		box.addItem(elements.invoice_type_year_youth(), "4");
		box.addItem(elements.train_membership(), "6");
		box.addItem(elements.invoice_type_other(), "5");
	}

}
