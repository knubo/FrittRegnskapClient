package no.knubo.accounting.client.views.events;

import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventManagementListView extends Composite implements ClickHandler {

    static EventManagementListView instance;
    private AccountTable events;
    private final ViewCallback callback;

    public static EventManagementListView getInstance(Constants constants, I18NAccount messages, Elements elements,
            ViewCallback callback) {
        if (instance == null) {
            instance = new EventManagementListView(elements, callback);
        }
        return instance;
    }

    public EventManagementListView(Elements elements, ViewCallback callback) {
        this.callback = callback;

        VerticalPanel vp = new VerticalPanel();

        events = new AccountTable("tableborder");

        events.setHeader(0, 0, elements.event());
        events.setHeader(0, 1, elements.event_start());
        events.setHeader(0, 2, elements.event_stop());
        events.setHeader(0, 3, elements.event_date());
        events.setHeaderRowStyle(0);

        vp.add(events);

        initWidget(vp);
    }

    public void init() {
        while (events.getRowCount() > 1) {
            events.removeRow(1);
        }

        List<EventInList> items = EventDAO.getEvents();

        int row = 1;
        for (EventInList eventListItem : items) {
            Anchor anchor = new Anchor(eventListItem.getName());
            anchor.setName(eventListItem.getId());
            anchor.addClickHandler(this);
            events.setWidget(row, 0, anchor);
            events.setText(row, 1, eventListItem.getStartDate());
            events.setText(row, 2, eventListItem.getEndDate());
            row++;
        }
    }

    public void onClick(ClickEvent event) {
        Anchor w = (Anchor) event.getSource();

        callback.openEvent(w.getName());

    }

}
