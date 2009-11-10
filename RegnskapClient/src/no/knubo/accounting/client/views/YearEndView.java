package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class YearEndView extends Composite {

	private static YearEndView me;

	private final Constants constants;

	private final I18NAccount messages;

	private FlexTable table;

	private final ViewCallback callback;

	private final Elements elements;


	public static YearEndView getInstance(Constants constants,
			I18NAccount messages, ViewCallback callback, Elements elements) {
		if (me == null) {
			me = new YearEndView(constants, messages, callback, elements);
		}
		return me;
	}

	public YearEndView(Constants constants, I18NAccount messages,
			ViewCallback callback, Elements elements) {
		this.constants = constants;
		this.messages = messages;
		this.callback = callback;
		this.elements = elements;
		
		
		
	}
	
	public void init() {	
	}
}
