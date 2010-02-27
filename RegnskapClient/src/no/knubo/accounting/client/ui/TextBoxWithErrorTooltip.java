package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;

public class TextBoxWithErrorTooltip extends TextBox implements Validateable {

    public TextBoxWithErrorTooltip(String name) {
        super();
        DOM.setElementAttribute(this.getElement(), "id", name);

    }

    public TextBox getTextBox() {
        return this;
    }

    /**
     * Sets the text and resets error view.
     * 
     * @param string
     */
    @Override
    public void setText(String string) {
        setTitle("");
        removeStyleName("errorinput");
        super.setText(string);
    }

    
    @Override
    public void setEnabled(boolean enabled) {
        UIObject.setStyleName(this.getElement(), "disabled", !enabled);
        super.setEnabled(enabled);
    }

    public void setErrorText(String text) {
        setTitle(text);
        addStyleName("errorinput");
    }

    public void setMouseOver(String mouseOver) {
        setTitle(mouseOver);
    }
}
