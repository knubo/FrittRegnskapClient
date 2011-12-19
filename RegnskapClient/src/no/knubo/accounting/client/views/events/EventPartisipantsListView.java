package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;

import com.google.gwt.user.client.ui.Composite;

public class EventPartisipantsListView extends Composite {

    private static EventPartisipantsListView instance;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;

    public EventPartisipantsListView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        
        AccountTable table = new AccountTable("edittable");
        
        table.setText(0,0, elements.event());
        
        initWidget(table);
    }

    public static EventPartisipantsListView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new EventPartisipantsListView(constants, messages, elements);
        }
        return instance;
    }

    public void init() {
        
        
    }

}
