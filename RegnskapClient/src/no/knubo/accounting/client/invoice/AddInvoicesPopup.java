package no.knubo.accounting.client.invoice;

import java.math.BigDecimal;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.ui.ListBoxWithErrorText;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AddInvoicesPopup extends DialogBox implements ClickHandler {

    private NamedButton addButton;
    private NamedButton cancelButton;

    public AddInvoicesPopup(RegisterInvoiceChooseInvoiceTypePage registerInvoiceChooseInvoiceTypePage, Elements elements, BigDecimal amount) {

        setText(elements.first_month());

        ListBoxWithErrorText box = new ListBoxWithErrorText("month");
        for (int i = 1; i < 12; i++) {
            box.addItem(Util.monthString(elements, i), String.valueOf(i));
        }

        VerticalPanel vp = new VerticalPanel();
        vp.add(box);

        FlowPanel buttons = new FlowPanel();
     
        addButton = new NamedButton("add", elements.add());
        addButton.addClickHandler(this);
        buttons.add(addButton);
        
        cancelButton = new NamedButton("cancel", elements.cancel());
        cancelButton.addClickHandler(this);
        buttons.add(cancelButton);
        
        setAutoHideEnabled(true);
        center();

    }

    @Override
    public void onClick(ClickEvent event) {
        if(event.getSource() == cancelButton) {
            hide();
        }
    }

}
