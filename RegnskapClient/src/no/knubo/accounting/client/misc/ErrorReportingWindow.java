package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.Elements;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorReportingWindow {

    public static void reportError(String title, String error) {
        DialogBox dialogBox = new DialogBox();
        
        dialogBox.addStyleName("errorwindow");
        
        VerticalPanel vp = new VerticalPanel();
        dialogBox.setText(title);
        

        Label label = new Label(error);
        if(error.length() > 250) {
            Elements elements = (Elements) GWT.create(Elements.class);

            vp.add(new Label(elements.error_general()));
            DisclosurePanel dp = new DisclosurePanel(elements.error_details());

            dp.add(new ScrollPanel(label));
            vp.add(dp);
            
        } else {
            vp.add(label);
        }

        dialogBox.center();
        dialogBox.setPopupPosition(dialogBox.getAbsoluteLeft(), 30);
        
        Button button = new Button("OK");
        button.addClickHandler(createCloseHandler(dialogBox));
        vp.add(button);
        dialogBox.add(vp);
    }

    private static ClickHandler createCloseHandler(final DialogBox dialogBox) {
        return new ClickHandler() {
            
            public void onClick(ClickEvent event) {
                dialogBox.hide();
            }
        };
    }
    
    
}
