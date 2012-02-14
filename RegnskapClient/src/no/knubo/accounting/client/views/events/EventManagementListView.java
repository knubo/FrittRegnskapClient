package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventManagementListView extends Composite implements ClickHandler {

    static EventManagementListView instance;
    private AccountTable events;
    private final ViewCallback callback;
    private NamedButton newButton;
    private final Constants constants;
    private final I18NAccount messages;

    public static EventManagementListView getInstance(Constants constants, I18NAccount messages, Elements elements,
            ViewCallback callback) {
        if (instance == null) {
            instance = new EventManagementListView(constants, elements, messages, callback);
        }
        return instance;
    }

    public EventManagementListView(Constants constants, Elements elements, I18NAccount messages, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.callback = callback;

        VerticalPanel vp = new VerticalPanel();

        events = new AccountTable("tableborder");

        events.setHeader(0, 0, elements.event());
        events.setHeader(0, 1, elements.event_start());
        events.setHeader(0, 2, elements.event_stop());
        events.setHeader(0, 3, elements.event_date());
        events.setHeader(0, 4, elements.event_end_date());
        events.setHeaderRowStyle(0);

        newButton = new NamedButton("new_event", elements.new_event());
        newButton.addClickHandler(this);
        vp.add(newButton);

        vp.add(events);

        initWidget(vp);
    }

    public void init() {
        while (events.getRowCount() > 1) {
            events.removeRow(1);
        }

        ServerResponse response = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                fillList(responseObj);
            }
        };
        AuthResponder.get(constants, messages, response, "registers/events.php?action=list");

    }

    protected void fillList(JSONValue responseObj) {
        JSONArray array = responseObj.isArray();
        List<EventInList> items = new ArrayList<EventInList>();

        for (int i = 0; i < array.size(); i++) {
            items.add(new EventInList(array.get(i).isObject()));
        }

        int row = 1;
        for (EventInList eventListItem : items) {
            Anchor anchor = new Anchor(eventListItem.getName());
            anchor.setName(eventListItem.getId());
            anchor.addStyleName("nowrap");
            anchor.addClickHandler(this);
            events.setWidget(row, 0, anchor);
            events.setText(row, 1, eventListItem.getStartDate());
            events.setText(row, 2, eventListItem.getEndDate());
            events.setText(row, 3, eventListItem.getEventDate());
            events.setText(row, 4, eventListItem.getEventEndDate());
            
            row++;
        }

    }

    @Override
    public void onClick(ClickEvent event) {

        if (event.getSource() == newButton) {
            doNew();
            return;
        }

        Anchor w = (Anchor) event.getSource();

        callback.openEvent(w.getName());

    }

    private void doNew() {
        callback.openEvent(null);
    }

}
