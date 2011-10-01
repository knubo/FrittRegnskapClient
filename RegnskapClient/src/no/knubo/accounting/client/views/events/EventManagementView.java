package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventManagementView extends Composite implements SelectionHandler<Integer> {
    static EventManagementView instance;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;
    private AccountTable eventData;
    private TextBoxWithErrorText eventTitle;
    private TextBoxWithErrorText eventStartRegistrationDate;
    private TextBoxWithErrorText eventStopRegistrationDate;
    private TextBoxWithErrorText eventDate;
    private EventFormEditor eventEditor;
    private TextBoxWithErrorText eventMaxPartisipants;
    private EventChoiceEditor eventChoicesEditor;
    private TabPanel panel;
    private Integer previous;

    public EventManagementView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;

        panel = new TabPanel();

        VerticalPanel vp = new VerticalPanel();

        eventData = new AccountTable("tableborder");

        eventData.setText(0, 0, elements.event());
        eventTitle = new TextBoxWithErrorText("event");
        eventData.setWidget(0, 1, eventTitle);

        eventData.setText(1, 0, elements.event_start());
        eventStartRegistrationDate = new TextBoxWithErrorText("event_start");
        eventData.setWidget(1, 1, eventStartRegistrationDate);

        eventData.setText(2, 0, elements.event_stop());
        eventStopRegistrationDate = new TextBoxWithErrorText("event_stop");
        eventData.setWidget(2, 1, eventStopRegistrationDate);

        eventData.setText(3, 0, elements.event_date());
        eventDate = new TextBoxWithErrorText("event_date");
        eventData.setWidget(3, 1, eventDate);

        eventData.setText(4, 0, elements.event_max_partisipants());
        eventMaxPartisipants = new TextBoxWithErrorText("event_max_partisipants");
        eventData.setWidget(4, 1, eventMaxPartisipants);
        vp.add(eventData);

        panel.add(vp, elements.event());

        eventChoicesEditor = new EventChoiceEditor(elements);

        panel.add(eventChoicesEditor, elements.event_choices());

        eventEditor = new EventFormEditor();
        panel.add(eventEditor, elements.event_form());

        panel.addSelectionHandler(this);

        initWidget(panel);

    }

    public static EventManagementView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new EventManagementView(constants, messages, elements);
        }
        return instance;
    }

    public void init(String id) {
        Event event = EventDAO.getEvent(id);

        eventTitle.setText(event.getName());
        eventStartRegistrationDate.setText(event.getStartDate());
        eventStopRegistrationDate.setText(event.getEndDate());
        eventDate.setText(event.getEventDate());

        eventChoicesEditor.setData(event);
        eventEditor.setData(event);
        panel.selectTab(0);
    }

    public void init() {
        panel.selectTab(0, false);

    }

    public void onSelection(SelectionEvent<Integer> selected) {
        Integer item = selected.getSelectedItem();

        if (previous != null && previous == 1) {
            eventChoicesEditor.sync();
        }
        
        if(item == 2) {
            eventEditor.setUpWidgets();
        }

        previous = item;
    }
}
