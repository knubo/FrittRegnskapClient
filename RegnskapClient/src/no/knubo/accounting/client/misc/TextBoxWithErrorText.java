package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class TextBoxWithErrorText extends ErrorLabelWidget implements
        Validateable {
    public TextBox textBox;

    public TextBoxWithErrorText(HTML errorLabel) {
        super(new TextBox(), errorLabel);
        this.label = errorLabel;
        this.textBox = (TextBox) widget;

        errorLabel.setStyleName("error");
        initWidget(textBox);
    }

    public TextBoxWithErrorText() {
        super(new TextBox());
        textBox = (TextBox) widget;

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

    public void setEnabled(boolean enabled) {
        UIObject.setStyleName(textBox.getElement(), "disabled", !enabled);
        textBox.setEnabled(enabled);
    }

    public void addFocusListener(final FocusCallback callback) {
        final ErrorLabelWidget me = this;
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
