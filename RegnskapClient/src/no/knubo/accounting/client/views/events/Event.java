package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Event extends EventInList {

    private List<EventChoice> choices;
    private Map<String, EventGroup> eventGroups = new HashMap<String, EventGroup>();

    public Event() {
        choices = Arrays.asList(new EventChoice(), new EventChoice(), new EventChoice());
    }

    public List<EventChoice> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<EventChoice> all) {
        choices = all;
    }

    public Collection<EventGroup> getEventGroups() {
        for(EventGroup group : eventGroups.values()) {
            group.reset();
        }
        
        HashSet<String> visited = new HashSet<String>();
        
        for (EventChoice choice : choices) {
            EventGroup eventGroup = eventGroups.get(choice.getGroup());

            if (eventGroup == null) {
                eventGroup = new EventGroup(choice.getGroup());
                eventGroups.put(choice.getGroup(), eventGroup);
            }

            eventGroup.registerChoice(choice);
            visited.add(choice.getGroup());
        }

        for(Iterator<String> i = eventGroups.keySet().iterator();i.hasNext();) {
            String group = i.next();
            
            EventGroup eventGroup = eventGroups.get(group);
            
            if(!visited.contains(group)) {
                
                eventGroup.removeWidgetFromParent();
                i.remove();
            } else {
                eventGroup.checkAndUpdateChoices();
            }
        }
        
        return eventGroups.values();
    }

}
