package no.knubo.accounting.client.views.ownings;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;

import com.google.gwt.user.client.ui.Composite;

public class OwningsListView extends Composite {

    private static OwningsListView instance;
    private Constants constants;
    private I18NAccount messages;
    private Elements elements;

    public static OwningsListView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new OwningsListView(constants, messages, elements);
        }
        return instance;
    }

    public OwningsListView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
    }
    
    public void init() {
        
    }
}
