package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.HelpTexts;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

public class MissingDataPopup extends DialogBox implements ClickHandler {

    public MissingDataPopup(String data) {
        
        HelpTexts helpTexts = (HelpTexts) GWT.create(HelpTexts.class);
        Elements elements = (Elements) GWT.create(Elements.class);

        
        HTML html = new HTML(helpTexts.getString(data));
        
        DockPanel dp = new DockPanel();
        dp.add(html, DockPanel.NORTH);
        
        NamedButton closeButton = new NamedButton(elements.close(), elements.close());
        dp.add(closeButton, DockPanel.NORTH);
        
        closeButton.addClickHandler(this);
        
        setText(elements.missing_data());
        setModal(true);
        setWidget(dp);
    }

    public void onClick(ClickEvent event) {
        hide();
    }
}
