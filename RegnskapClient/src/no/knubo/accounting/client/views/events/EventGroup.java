package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EventGroup {
    String name;
    Map<String, EventChoice> choices = new HashMap<String, EventChoice>();
    Integer xPos;
    Integer yPos;
    private Widget widget;

    public EventGroup(String name) {
        this.name = name;
    }

    public void registerChoice(EventChoice choice) {
        choices.put(choice.getName(), choice);
    }

    public void reset() {
        choices.clear();
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public boolean hasWidget() {
        return widget != null;
    }

    public List<String> getStringChoices() {
        ArrayList<String> stringChoices = new ArrayList<String>();

        for (EventChoice choice : choices.values()) {
            stringChoices.add(choice.getName());
        }

        if (stringChoices.size() > 1) {
            Collections.sort(stringChoices);
        }

        return stringChoices;
    }

    public Widget createWidget() {
        List<String> choices = getStringChoices();

        Widget widget = null;

        if (choices.size() == 1) {
            widget = new CheckBox(choices.get(0));
        } else {
            ListBox box = (ListBox) (widget = new ListBox());

            for (String c : choices) {
                box.addItem(c);
            }
        }
        return widget;
    }

}
