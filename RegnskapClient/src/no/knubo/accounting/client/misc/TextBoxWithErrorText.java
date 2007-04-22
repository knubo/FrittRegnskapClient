package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class TextBoxWithErrorText extends Composite implements Validateable {

	private TextBox textBox;

	private HTML label;

	public TextBoxWithErrorText() {
		textBox = new TextBox();
		label = new HTML();
		label.setStyleName("error");
		
		HorizontalPanel hp = new HorizontalPanel();

		hp.add(textBox);
		hp.add(label);

		initWidget(hp);
	}

	public void setText(String string) {
		textBox.setText(string);
	}

	public void setMaxLength(int i) {
		textBox.setMaxLength(i);
	}

	public void setVisibleLength(int i) {
		textBox.setVisibleLength(i);
	}

	public String getText() {
		return textBox.getText();
	}

	/**
	 * Sets error to be displayed if error occures.
	 * @param text The text.
	 */
	public void setErrorText(String text) {
		label.setHTML(text);
	}

	public void setFocus(boolean b) {
		textBox.setFocus(b);
	}
	
}
