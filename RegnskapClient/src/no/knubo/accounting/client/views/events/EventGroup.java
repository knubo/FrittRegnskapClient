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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EventGroup {
    final String name;
    Map<String, EventChoice> choices = new HashMap<String, EventChoice>();
    private Widget widget;
    private PaletteWidget paletteWidget;
    private Integer row;
    private Integer col;
    static final String TYPE_CHECKBOX = "Checkbox";
    static final String TYPE_TEXTFIELD = "Tekstbox";
    static final String TYPE_TEXTAREA = "Textarea";

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
        if (choices.size() == 1) {
            EventChoice choice = choices.values().iterator().next();
            String type = choice.getInputType();

            if (type.equals(TYPE_TEXTFIELD)) {
                TextBox textBox = new TextBox();
                textBox.setTitle(choice.getName());
                return textBox;
            } else if (type.equals(TYPE_TEXTAREA)) {
                TextArea textBox = new TextArea();
                textBox.setTitle(choice.getName());
                return textBox;
            }

            return new CheckBox(choice.getName());

        }

        List<String> choices = getStringChoices();

        ListBox box = new ListBox();
        box.addItem("");
        for (String c : choices) {
            box.addItem(c);
        }
        return box;
    }

    public void checkAndUpdateChoices() {
        if (widget == null) {
            return;
        }

        boolean turnedIntoListbox = !(widget instanceof ListBox) && choices.size() > 1;
        boolean turnedIntoSingleChoice = widget instanceof ListBox && choices.size() == 1;

        if (turnedIntoSingleChoice || turnedIntoListbox) {
            replaceWidget();
            return;
        }

        if (choices.size() == 1) {
            EventChoice choice = choices.values().iterator().next();

            if (widget instanceof CheckBox && !choice.getInputType().equals(TYPE_CHECKBOX)) {
                replaceWidget();
                return;
            }

            if (widget instanceof TextBox && !choice.getInputType().equals(TYPE_TEXTFIELD)) {
                replaceWidget();
                return;
            }

            if (widget instanceof TextArea && !choice.getInputType().equals(TYPE_TEXTAREA)) {
                replaceWidget();
                return;
            }

            if (widget instanceof CheckBox) {
                CheckBox box = (CheckBox) widget;
                if (!box.getText().equals(choice.getName())) {
                    replaceWidget();
                }
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
