package no.knubo.accounting.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;

public class NamedButton extends Button {

    public NamedButton(String name) {
        setId(name);
    }

    public void setId(String id) {
        DOM.setElementAttribute(this.getElement(), "id", id);
    }

    public NamedButton(String name, String text) {
        super(text);
        setId(name);
        setAccessKey(Character.toLowerCase(text.charAt(0)));
    }

    public NamedButton(String name, String text, String style) {
        this(name, text);
        addStyleName(style);
    }
}
