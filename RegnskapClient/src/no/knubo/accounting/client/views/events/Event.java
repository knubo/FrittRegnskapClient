package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.knubo.accounting.client.Util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class Event extends EventInList {

    private List<EventChoice> choices;
    private Map<String, EventGroup> eventGroups = new HashMap<String, EventGroup>();

    private Map<Pair<Integer, Integer>, String> htmllabels = new HashMap<Pair<Integer, Integer>, String>();

    public Event() {
        super();
        data.put("headerHTML", new JSONString(""));
        choices = new ArrayList<EventChoice>();
    }

    public Event(JSONObject obj) {
        super(obj);

        if (!obj.containsKey("headerHTML")) {
            obj.put("headerHTML", new JSONString(""));
        }

        choices = new ArrayList<EventChoice>();

        if (obj.containsKey("choices")) {
            JSONArray choicesToFill = obj.get("choices").isArray();
            for (int i = 0; i < choicesToFill.size(); i++) {
                choices.add(new EventChoice(choicesToFill.get(i).isObject()));
            }
        }

        if (obj.containsKey("groups")) {
            JSONObject groups = obj.get("groups").isObject();
            Set<String> groupnames = groups.keySet();

            for (String group : groupnames) {
                JSONObject oneGroup = groups.get(group).isObject();

                EventGroup eg = new EventGroup(group);
                eg.setPosition((int) oneGroup.get("row").isNumber().doubleValue(), (int) oneGroup.get("col").isNumber()
                        .doubleValue());

                eventGroups.put(group, eg);
            }
        }

        if (obj.containsKey("html")) {
            JSONObject htmls = obj.get("html").isObject();

            Set<String> positions = htmls.keySet();

            for (String xandy : positions) {
                String[] parts = xandy.split(":");

                Pair<Integer, Integer> pair = new Pair<Integer, Integer>(Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]));

                htmllabels.put(pair, Util.str(htmls.get(xandy)));
            }
        }
    }

    public List<EventChoice> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<EventChoice> all) {
        choices = all;
    }

    public Collection<EventGroup> getEventGroupsAndUpdateWidgets() {
        for (EventGroup group : eventGroups.values()) {
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

        for (Iterator<String> i = eventGroups.keySet().iterator(); i.hasNext();) {
            String group = i.next();

            EventGroup eventGroup = eventGroups.get(group);

            if (!visited.contains(group)) {

                eventGroup.removeWidgetFromParent();
                i.remove();
            } else {
                eventGroup.checkAndUpdateChoices();
            }
        }

        return eventGroups.values();
    }

    public void setGroupPosition(int row, int col, String groupName) {
        eventGroups.get(groupName).setPosition(row, col);
    }

    public void resetHTML() {
        htmllabels.clear();
    }

    public void setHTML(int row, int col, String html) {
        htmllabels.put(new Pair<Integer, Integer>(row, col), html);
    }

    @Override
    public JSONObject getAsJSON() {
        JSONObject obj = super.getAsJSON();

        JSONObject groups = new JSONObject();
        obj.put("groups", groups);

        for (EventGroup group : eventGroups.values()) {
            JSONObject groupinfo = new JSONObject();

            if (!group.isPositioned()) {
                throw new IllegalStateException("Not positioned");
            }

            groupinfo.put("col", new JSONNumber(group.getCol()));
            groupinfo.put("row", new JSONNumber(group.getRow()));

            groups.put(group.name, groupinfo);
        }

        JSONArray jsChoices = new JSONArray();
        obj.put("choices", jsChoices);

        int pos = 0;
        for (EventChoice choice : choices) {
            jsChoices.set(pos++, choice.getAsJSON());
        }

        JSONObject htmls = new JSONObject();
        obj.put("html", htmls);

        for (Pair<Integer, Integer> pair : htmllabels.keySet()) {
            htmls.put(pair.getA() + ":" + pair.getB(), new JSONString(htmllabels.get(pair)));
        }

        return obj;
    }

    public Map<Pair<Integer, Integer>, String> getHTMLLabels() {
        return htmllabels;
    }

    public void setHeaderHTML(String html) {
        data.put("headerHTML", new JSONString(html));
    }

    public String getHeaderHTML() {
        return Util.str(data.get("headerHTML"));
    }

    public void setGroupData(String groupName, String inputType, Boolean required) {
        for (EventChoice choice : choices) {
            if(choice.getGroup().equals(groupName)) {
                choice.setInputType(inputType);
            }
        }
    }

}
