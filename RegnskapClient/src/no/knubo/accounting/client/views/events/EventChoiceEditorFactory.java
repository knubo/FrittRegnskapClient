package no.knubo.accounting.client.views.events;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EventChoiceEditorFactory {

    public static final TextFieldFactory textFieldFactory = new TextFieldFactory();
    public static final FieldTypeListBoxFactory fieldTypeListBox = new FieldTypeListBoxFactory();
    public static final CheckBoxFactory checkBoxFactory = new CheckBoxFactory();

    static abstract class WidgetFactory {
        abstract public Widget makeWidget();
    }

    static class CheckBoxFactory extends WidgetFactory {

        @Override
        public Widget makeWidget() {
            return new CheckBox();
        }

    }

    static class TextFieldFactory extends WidgetFactory {

        @Override
        public Widget makeWidget() {
            return new TextBox();
        }

    }

    static class FieldTypeListBoxFactory extends WidgetFactory {

        @Override
        public ListBox makeWidget() {
            ListBox listBox = new ListBox();
            listBox.addItem("");
            listBox.addItem(EventGroup.TYPE_CHECKBOX);
            listBox.addItem(EventGroup.TYPE_TEXTFIELD);
            listBox.addItem(EventGroup.TYPE_TEXTAREA);
            return listBox;
        }
    }
}
