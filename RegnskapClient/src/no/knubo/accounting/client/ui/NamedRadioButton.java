package no.knubo.accounting.client.ui;

import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.user.client.ui.RadioButton;

public class NamedRadioButton extends RadioButton implements Validateable {

    private String originalText;

    public NamedRadioButton(String name, String label) {
        super(name, label);
    }

    @Override
    public void setText(String text) {
        if (originalText == null) {
            this.originalText = text;
        }
        super.setText(text);
    }

    public void setErrorText(String text) {
        setTitle(text);
        setText(originalText + " " + text);
        
        if (text.length() == 0) {
            LabelElement label = (LabelElement) getElement().getChild(1);
            label.removeClassName("error");
        } else {
            LabelElement label = (LabelElement) getElement().getChild(1);
            label.addClassName("error");
        }
    }

    public void setMouseOver(String mouseOver) {
        setTitle(mouseOver);
    }

}
