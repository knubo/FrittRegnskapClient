package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TextBoxWithErrorText extends Composite implements Validateable {

    private final TextBox textBox;

    private final HTML label;

    public TextBoxWithErrorText(HTML label) {
        this.textBox = new TextBox();
        this.label = label;
        label.setStyleName("error");
        initWidget(textBox);
    }

    public TextBoxWithErrorText() {
        textBox = new TextBox();
        label = new HTML();
        label.setStyleName("error");

        HorizontalPanel hp = new HorizontalPanel();

        hp.add(textBox);
        hp.add(label);

        initWidget(hp);
    }

    public TextBox getTextBox() {
        return textBox;
    }

    /**
     * Sets the text and resets error view.
     * 
     * @param string
     */
    public void setText(String string) {
        label.setText("");
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
     * 
     * @param text
     *            The text.
     */
    public void setErrorText(String text) {
        label.setHTML(text);
    }

    public void setFocus(boolean b) {
        textBox.setFocus(b);
    }

    public void setMouseOver(String mouseOver) {
        textBox.setTitle(mouseOver);
    }

    public void addFocusListener(final FocusCallback callback) {
        final TextBoxWithErrorText me = this;
        FocusListener eventhandler = new FocusListener() {

            public void onFocus(Widget sender) {
                callback.onFocus(me);
            }

            public void onLostFocus(Widget sender) {
                callback.onLostFocus(me);
            }
        };
        textBox.addFocusListener(eventhandler);
    }
}
