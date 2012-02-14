package no.knubo.accounting.client.views.events;

import java.util.List;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.events.EventParticipants.EventGroupElem;
import no.knubo.accounting.client.views.events.EventParticipants.EventParticipantElement;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventPartisipantsListView extends Composite {

    private static EventPartisipantsListView instance;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;
    private EventParticipants event;
    private Label header;
    private DockPanel splitPanel;
    private AccountTable groupedTable;
    private AccountTable participantsTable;
    private AccountTable statsTable;

    public EventPartisipantsListView(Constants constants, I18NAccount messages, Elements elements) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;

        VerticalPanel vp = new VerticalPanel();

        header = new Label("Test");
        vp.add(header);

        splitPanel = new DockPanel();
        splitPanel.setSize("800px", "600px");
        vp.add(splitPanel);
        splitPanel.getElement().getStyle().setProperty("border", "3px solid #e7e7e7");

        statsTable = new AccountTable("tableborder");
        groupedTable = new AccountTable("tableborder");
        participantsTable = new AccountTable("tablecells");

        splitPanel.add(statsTable, DockPanel.NORTH);
        splitPanel.add(groupedTable, DockPanel.WEST);
        splitPanel.add(participantsTable, DockPanel.SOUTH);
        initWidget(vp);
    }

    public static EventPartisipantsListView getInstance(Constants constants, I18NAccount messages, Elements elements) {
        if (instance == null) {
            instance = new EventPartisipantsListView(constants, messages, elements);
        }
        return instance;
    }

    public void init(String id) {
        ServerResponse callback = new ServerResponse() {

            @Override
            public void serverResponse(JSONValue responseObj) {
                event = new EventParticipants(responseObj.isObject());
                fillGroupedData();
                fillPeopleData();
            }
        };
        AuthResponder.get(constants, messages, callback, "registers/events/event.php?action=participants&id=" + id);
    }

    protected void fillPeopleData() {
        List<EventParticipantElement> people = event.getParticipants();
        participantsTable.setHeaders(0, elements.name(), elements.event_register_date(), elements.event_change_date());

        int row = 1;
        for (EventParticipantElement eventGroupElem : people) {
            participantsTable.setText(row++, eventGroupElem.getName(), eventGroupElem.getRegisteredDate(), eventGroupElem.getLastUpdatedDate());
        }

    }

    protected void fillGroupedData() {
        List<EventGroupElem> values = event.getGroupedValues();
        groupedTable.setHeaders(0, "Gruppe", "Verdi", "Antall");
        int row = 1;
        for (EventGroupElem eventGroupElem : values) {
            groupedTable.setText(row++, eventGroupElem.getGroupKey(), eventGroupElem.getGroupValue(),
                    String.valueOf(eventGroupElem.getGroupCount()));
        }
    }
}
