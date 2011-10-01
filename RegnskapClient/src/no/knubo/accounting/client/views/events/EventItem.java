package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventItem extends EventListItem {

    private List<EventChoice> choices;

    public EventItem() {
        choices = Arrays.asList(new EventChoice(), new EventChoice(), new EventChoice());
    }
    
    public List<EventChoice> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<EventChoice> all) {
        choices = all;
    }


}
