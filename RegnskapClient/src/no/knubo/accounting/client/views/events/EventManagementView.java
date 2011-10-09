package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventManagementView extends Composite implements SelectionHandler<Integer>, ClickHandler {
    static EventManagementView instance;
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
    private HTML previewPanel;
    private Label infoLabel;
    private NamedButton saveButton;
    private Event event;
    private final I18NAccount messages;

    public EventManagementView(I18NAccount messages, Elements elements) {
        this.messages = messages;
        panel = new TabPanel();

        panel.add(createMainEntry(elements), elements.event());

        eventChoicesEditor = new EventChoiceEditor(elements);

        panel.add(eventChoicesEditor, elements.event_choices());

        eventEditor = new EventFormEditor(elements, messages);
        panel.add(eventEditor, elements.event_form());

        panel.addSelectionHandler(this);

        previewPanel = new HTML();
        panel.add(previewPanel, elements.preview());

        initWidget(panel);

    }

    private VerticalPanel createMainEntry(Elements elements) {
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

        infoLabel = new Label();
        vp.add(infoLabel);
        HorizontalPanel buttonRow = new HorizontalPanel();

        saveButton = new NamedButton("save", elements.save());
        saveButton.addClickHandler(this);

        buttonRow.add(saveButton);

        vp.add(buttonRow);
        return vp;
    }

    public static EventManagementView getInstance(I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new EventManagementView(messages, elements);
        }
        return instance;
    }

    public void init(String id) {
        event = EventDAO.getEvent(id);

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

        if (item == 2) {
            eventEditor.setUpWidgets();
        }

        if (item == 3) {
            previewPanel.setHTML(eventEditor.getHTMLView());
        }

        previous = item;
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == saveButton) {
            save();
        }
    }

    private void save() {
        if (!validate_ok()) {
            return;
        }

        JSONObject obj = getEventAsJSON();
        System.out.println(obj.toString());
    }

    private boolean validate_ok() {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), eventTitle, eventStartRegistrationDate, eventStopRegistrationDate,
                eventDate);

        mv.date(messages.date_format(), eventStartRegistrationDate, eventStopRegistrationDate, eventDate);
        mv.range(messages.field_positive(), 0, Integer.MAX_VALUE, eventMaxPartisipants);

        return mv.validateStatus();
    }

    private JSONObject getEventAsJSON() {
        event.setName(eventTitle.getText());
        event.setStartDate(eventStartRegistrationDate.getText());
        event.setStopDate(eventStopRegistrationDate.getText());
        event.setEventDate(eventDate.getText());
        event.setMaxPeople(eventMaxPartisipants.getText());

        eventEditor.setGroupPositionsAndHTML();

        return event.getAsJSON();
    }
}
