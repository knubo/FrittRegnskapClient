package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HTMLStreamWindow extends DialogBox implements ClickHandler {

    private HTMLStreamWindow(String address, Elements elements) {
        
        Frame frame = new Frame(address);

        frame.setWidth("800px");
        frame.setHeight("600px");
        
        VerticalPanel vp = new VerticalPanel();
        vp.add(frame);
        NamedButton button = new NamedButton("close", elements.close());
        button.addClickHandler(this);
        vp.add(button);
        setWidget(vp);
        center();
    }

    public static HTMLStreamWindow open(String address, Elements elements) {
        return new HTMLStreamWindow(address, elements);
    }

    @Override
    public void onClick(ClickEvent event) {
        hide();
    }
}
