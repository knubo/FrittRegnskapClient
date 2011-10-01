package no.knubo.accounting.client.views.events;

import java.util.Arrays;
import java.util.List;

public class EventDAO {

    public static List<EventListItem> getEvents() {
        return Arrays.asList(new EventListItem(), new EventListItem());
    }

    public static EventItem getEvent(String id) {
        return new EventItem();
    }
}
