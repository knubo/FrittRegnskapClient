package no.knubo.accounting.client.views.events;

import no.knubo.accounting.client.Constants;
import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.I18NAccount;
import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.AuthResponder;
import no.knubo.accounting.client.misc.ServerResponse;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.views.ViewCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventListView extends Composite implements ClickHandler {

    private static EventListView instance;
    private final Constants constants;
    private final I18NAccount messages;
    private final Elements elements;
    private AccountTable events;
    private final ViewCallback callback;

    public EventListView(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        this.constants = constants;
        this.messages = messages;
        this.elements = elements;
        this.callback = callback;

        VerticalPanel vp = new VerticalPanel();

        events = new AccountTable("tableborder");
        vp.add(events);

        events.setHeader(0, 0, elements.event());
        events.setHeader(0, 1, elements.event_start());
        events.setHeader(0, 2, elements.event_partisipant_count());
        events.setHeaderRowStyle(0);

        initWidget(vp);
    }

    public static EventListView getInstance(Constants constants, I18NAccount messages, Elements elements, ViewCallback callback) {
        if (instance == null) {
            instance = new EventListView(constants, messages, elements,callback);
        }
        return instance;
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
        AuthResponder.get(constants, messages, response, "registers/events.php?action=list_participants");
    }

    protected void fillList(JSONValue responseObj) {
        JSONArray array = responseObj.isArray();

        for (int i = 0; i < array.size(); i++) {
            JSONObject event = array.get(i).isObject();

            Anchor anchor = new Anchor(Util.str(event.get("eventdesc")));
            anchor.setName("e"+Util.str(event.get("id")));
            anchor.addClickHandler(this);
            
            events.setText((i + 1), null, Util.formatDate(event.get("startDate")),
                    Util.str(event.get("participants")));
            events.setWidget((i+1), 0, anchor,"nowrap");
        }

    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        
        if(source instanceof Anchor) {
            String id = ((Anchor)source).getName().substring(1);
            
            callback.openEventPartisipants(id);
        }
    }

}
