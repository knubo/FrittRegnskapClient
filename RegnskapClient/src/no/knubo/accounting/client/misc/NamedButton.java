package no.knubo.accounting.client.misc;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;

public class NamedButton extends Button {

    public NamedButton(String name) {
        setName(name);
    }

    private void setName(String name) {
        DOM.setElementAttribute(this.getElement(), "name", name);
    }
    
    public NamedButton(String name, String text) {
        super(text);
        setName(name);
    }
}
