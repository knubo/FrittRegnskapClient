package no.knubo.accounting.client.views.events;

import java.util.Arrays;
import java.util.List;

public class EventDAO {

    public static List<EventInList> getEvents() {
        return Arrays.asList(new EventInList(), new EventInList());
    }

    public static Event getEvent(String id) {
        return new Event();
    }
}
