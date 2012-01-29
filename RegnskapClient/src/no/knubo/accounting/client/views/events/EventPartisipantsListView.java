package no.knubo.accounting.client.views.events;

import java.util.List;
import java.util.Map;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.events.EventParticipants.EventGroupElem;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventPartisipantsListView extends Composite {

    private static EventPartisipantsListView instance;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;
    private EventParticipants event;
    private Label header;
    private SplitLayoutPanel splitPanel;
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

        splitPanel = new SplitLayoutPanel();
        splitPanel.setSize("800px", "600px");
        vp.add(splitPanel);
        splitPanel.getElement().getStyle().setProperty("border", "3px solid #e7e7e7");

        statsTable = new AccountTable("tableborder");
        groupedTable = new AccountTable("tableborder");
        participantsTable = new AccountTable("tableborder");

        splitPanel.addNorth(statsTable, 50);
        splitPanel.addWest(groupedTable, 400);
        splitPanel.addSouth(participantsTable, 300);
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

            public void serverResponse(JSONValue responseObj) {
                event = new EventParticipants(responseObj.isObject());
                fillEventData();
            }
        };
        AuthResponder.get(constants, messages, callback, "registers/events/event.php?action=participants&id=" + id);
    }

    protected void fillEventData() {
        List<EventGroupElem> values = event.getGroupedValues();
        groupedTable.setHeaders(0, "Gruppe","Verdi","Antall");
        int row = 1;
        for (EventGroupElem eventGroupElem : values) {
            groupedTable.setText(row++, eventGroupElem.getGroupKey(), eventGroupElem.getGroupValue(),
                    String.valueOf(eventGroupElem.getGroupCount()));
        }
    }
}
