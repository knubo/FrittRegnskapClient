package no.knubo.accounting.client.misc;

import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.validation.Validateable;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxWithErrorText extends ErrorLabelWidget implements
        Validateable {

    private ListBox listbox;

    public String getText() {
        return Util.getSelected(listbox);
    }

    public ListBoxWithErrorText() {
        super(new ListBox());
        listbox = (ListBox) widget;
        
        HorizontalPanel hp = new HorizontalPanel();
        
        hp.add(listbox);
        hp.add(label);
        initWidget(hp);
    }

    public ListBoxWithErrorText(HTML errorLabel) {
        super(new ListBox(), errorLabel);
        listbox = (ListBox) widget;
        
        initWidget(listbox);
    }

    public ListBox getListbox() {
        return listbox;
    }
}
