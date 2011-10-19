package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;
import no.knubo.accounting.client.ui.TextBoxWithErrorText;
import no.knubo.accounting.client.validation.MasterValidator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
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
    private TextBoxWithErrorText eventEndRegistrationDate;
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
    private NamedButton activateButton;
    private NamedButton deactivateButton;
    private final Constants constants;

    public EventManagementView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
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
        eventEndRegistrationDate = new TextBoxWithErrorText("event_stop");
        eventData.setWidget(2, 1, eventEndRegistrationDate);

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
        saveButton.addStyleName("buttonrow");
        saveButton.addClickHandler(this);

        activateButton = new NamedButton("event_activate", elements.event_activate());
        activateButton.addStyleName("buttonrow");
        activateButton.addClickHandler(this);

        deactivateButton = new NamedButton("event_deactivate", elements.event_deactivate());
        deactivateButton.addStyleName("buttonrow");
        deactivateButton.addClickHandler(this);

        buttonRow.add(saveButton);

        vp.add(buttonRow);
        return vp;
    }

    public static EventManagementView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new EventManagementView(constants, messages, elements);
        }
        return instance;
    }

    public void init(String id) {
        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                event = new Event(responseObj.isObject());

                eventTitle.setText(event.getName());
                eventStartRegistrationDate.setText(event.getStartDate());
                eventEndRegistrationDate.setText(event.getEndDate());
                eventDate.setText(event.getEventDate());
                eventMaxPartisipants.setText(event.getMaxPeople());

                eventChoicesEditor.setData(event);
                eventEditor.setData(event);
                panel.selectTab(0);

                activateButton.setEnabled(!event.isActive());
                deactivateButton.setEnabled(event.isActive());
            }
        };
        AuthResponder.get(constants, messages, callback, "registers/events/event.php?action=get&id=" + id);

    }

    public void init() {
        panel.selectTab(0, true);
        event = new Event();

        eventChoicesEditor.setData(event);
        eventEditor.setData(event);
        panel.selectTab(0);

        activateButton.setEnabled(!event.isActive());
        deactivateButton.setEnabled(event.isActive());
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
        } else if (event.getSource() == activateButton) {
            activate();
        } else if (event.getSource() == deactivateButton) {
            deactivate();
        }

    }

    private void deactivate() {
        EventActions.deactivate();
    }

    private void activate() {
        EventActions.activate();
    }

    private void save() {
        if (!validate_ok()) {
            return;
        }
        JSONObject obj = null;
        try {
            obj = getEventAsJSON();
            infoLabel.setText("");
            infoLabel.removeStyleName("error");
        } catch (IllegalStateException e) {
            infoLabel.addStyleName("error");
            infoLabel.setText(messages.event_position_grops());
            return;
        }
        StringBuffer parameters = new StringBuffer();
        parameters.append("action=save");
        Util.addPostParam(parameters, "data", obj.toString());

        ServerResponse callback = new ServerResponse() {

            public void serverResponse(JSONValue responseObj) {
                JSONObject object = responseObj.isObject();

                event.setId(Util.str(object.get("id")));

                infoLabel.setText(messages.save_ok());

                Util.timedMessage(infoLabel, "", 15);
            }
        };
        AuthResponder.post(constants, messages, callback, parameters, "registers/events.php");

    }

    private boolean validate_ok() {
        MasterValidator mv = new MasterValidator();
        mv.mandatory(messages.required_field(), eventTitle, eventStartRegistrationDate, eventEndRegistrationDate,
                eventDate);

        mv.date(messages.date_format(), eventStartRegistrationDate, eventEndRegistrationDate, eventDate);
        mv.range(messages.field_positive(), 0, Integer.MAX_VALUE, eventMaxPartisipants);

        return mv.validateStatus();
    }

    private JSONObject getEventAsJSON() {
        event.setName(eventTitle.getText());
        event.setStartDate(eventStartRegistrationDate.getText());
        event.setEndDate(eventEndRegistrationDate.getText());
        event.setEventDate(eventDate.getText());
        event.setMaxPeople(eventMaxPartisipants.getText());

        eventEditor.setGroupPositionsAndHTML();

        return event.getAsJSON();
    }
}
