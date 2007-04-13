package no.knubo.accounting.client.views;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class AboutView extends Composite {

	static class AboutLoader extends LazyLoad {
		private AboutView instance;

		public final Widget getInstance(Constants constants,
				I18NAccount messages) {
			if (instance != null) {
				return instance;
			}
			return (instance = new AboutView(constants, messages));
		}
	}

	private AboutView(Constants constants, I18NAccount messages) {
		Frame frame = new Frame("about.html");
		frame.setSize("800", "600");
		initWidget(frame);
	}
	
	public static LazyLoad loader() {
		return new AboutLoader();
	}
}
