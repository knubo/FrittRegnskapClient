package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.HTTPRequest;
import com.google.gwt.user.client.ResponseTextHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MonthView extends Composite implements ResponseTextHandler {

	static class MonthLoader extends LazyLoad {
		private MonthView instance;

		public final Widget getInstance(Constants constants,
				I18NAccount messages) {
			if (instance != null) {
				return instance;
			}
			return (instance = new MonthView(constants, messages));
		}
	}

	private HTML html;

	private final Constants constants;


	public MonthView(Constants constants, I18NAccount messages) {
		this.constants = constants;
		html = new HTML();
		html.setText(messages.loding_page());

		if (!HTTPRequest.asyncGet(this.constants.baseurl()
				+ "accounting/showmonth.php", this)) {
			html.setText(messages.failedConnect());
		}

		initWidget(html);
	}

	public static LazyLoad loader() {
		return new MonthLoader();
	}

	public void onCompletion(String responseText) {
		html.setText(responseText);
	}
}
