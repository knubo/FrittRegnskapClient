package no.knubo.accounting.client.views.events;

import java.util.LinkedList;
import java.util.List;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class EventParticipants {

    private final JSONObject eventData;

    public EventParticipants(JSONObject object) {
        this.eventData = object;
    }

    public List<EventGroupElem> getGroupedValues() {
        List<EventGroupElem> result = new LinkedList<EventGroupElem>();
        
       JSONArray groupedData = eventData.get("data").isArray();
       
       for(int i=0; i < groupedData.size(); i++) {
           JSONObject grouped = groupedData.get(i).isObject();
           
           result.add(new EventGroupElem(grouped));
       }
       
        
        return result;
    }

    
    static class EventGroupElem {

        private final JSONObject elem;

        public EventGroupElem(JSONObject elem) {
            this.elem = elem;
        }
        
        public String getGroupKey() {
            return Util.str(elem.get("group_key"));
        }

        public String getGroupValue() {
            return Util.str(elem.get("group_value"));
        }

        public int getGroupCount() {
            return Util.getInt(elem.get("count"));
        }
    }
}
