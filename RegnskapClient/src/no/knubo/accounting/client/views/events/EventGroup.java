package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import no.knubo.accounting.client.views.events.dad.PaletteWidget;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EventGroup {
    final String name;
    Map<String, EventChoice> choices = new HashMap<String, EventChoice>();
    private Widget widget;
    private PaletteWidget paletteWidget;
    private Integer row;
    private Integer col;

    public EventGroup(String name) {
        this.name = name;
    }
    

    public void registerChoice(EventChoice choice) {
        choices.put(choice.getName(), choice);
    }

    public void reset() {
        choices.clear();
    }

    public boolean hasWidget() {
        return paletteWidget != null;
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
        widget = createWidgetInt();

        paletteWidget = new PaletteWidget(widget, name);
        return paletteWidget;
    }

    private Widget createWidgetInt() {
        List<String> choices = getStringChoices();

        if (choices.size() == 1) {
            return new CheckBox(choices.get(0));
        }
        ListBox box = new ListBox();

        for (String c : choices) {
            box.addItem(c);
        }
        return box;
    }

    public void checkAndUpdateChoices() {
        if (widget == null) {
            return;
        }

        boolean turnedIntoListbox = widget instanceof CheckBox && choices.size() > 1;
        boolean turnedIntoCheckbox = widget instanceof ListBox && choices.size() == 1;

        if (turnedIntoCheckbox || turnedIntoListbox) {
            replaceWidget();
            return;
        }

        if (choices.size() == 1) {

            CheckBox box = (CheckBox) widget;
            if (!box.getText().equals(choices.values().iterator().next().getName())) {
                replaceWidget();
            }
            return;
        }

        ListBox box = (ListBox) widget;

        if (box.getItemCount() != choices.size()) {
            replaceWidget();
            return;
        }

        Iterator<String> choiceIterator = getStringChoices().iterator();
        for (int i = 0; i < box.getItemCount() && choiceIterator.hasNext(); i++) {
            String existingChoice = choiceIterator.next();

            if (!existingChoice.equals(box.getItemText(i))) {
                replaceWidget();
                return;
            }
        }

    }

    private void replaceWidget() {
        widget = createWidgetInt();
        paletteWidget.replaceWidget(widget);
    }

    public void removeWidgetFromParent() {
        paletteWidget.removeFromParent();
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }


    public boolean isPositioned() {
        return row != null && col != null;
    }
}
