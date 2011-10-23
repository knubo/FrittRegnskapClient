package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventChoiceEditor extends Composite implements ClickHandler {

    private NamedButton newButton;
    private AccountTable choiceTable;
    private Event event;
    private final Elements elements;

    public EventChoiceEditor(Elements elements) {
        this.elements = elements;

        VerticalPanel vp = new VerticalPanel();

        newButton = new NamedButton("event_new_choice", elements.event_new_choice());
        newButton.addClickHandler(this);
        vp.add(newButton);

        choiceTable = new AccountTable("tableborder choicetable");

        FieldConfig.init(elements);

        choiceTable.setHeaders(0, FieldConfig.getHeaders(elements.delete()));

        vp.add(choiceTable);

        initWidget(vp);
    }

    public void onClick(ClickEvent event) {
        if (event.getSource() == newButton) {
            addNewRow(false);
        }
        if (event.getSource() instanceof Image) {
            delRow((Image) event.getSource());
        }
    }

    private void delRow(Image source) {
        String row = source.getElement().getId();

        String rownum = row.substring(3);

        choiceTable.removeRow(Integer.parseInt(rownum));
    }

    private void addNewRow(boolean disabled) {
        int row = choiceTable.getRowCount();

        for (int i = 0; i < FieldConfig.fieldConfigs.size(); i++) {
            FieldConfig fieldConfig = FieldConfig.fieldConfigs.get(i);

            choiceTable.setWidget(row, i, fieldConfig.widget);
        }

        if (!disabled) {
            Image delImage = ImageFactory.deleteImage("del" + row);
            choiceTable.setWidget(row, FieldConfig.fieldConfigs.size(), delImage);
            delImage.addClickHandler(this);
        }
    }

    public void setData(Event event) {
        this.event = event;
        while (choiceTable.getRowCount() > 1) {
            choiceTable.removeRow(1);
        }

        newButton.setEnabled(!event.isActive());

        List<EventChoice> choices = event.getChoices();

        int row = 1;
        for (EventChoice e : choices) {
            addNewRow(event.isActive());
            
            FieldConfig.setText(choiceTable, row, elements.name(), e.getName());
            FieldConfig.setText(choiceTable, row, elements.group(), e.getGroup());
            FieldConfig.setText(choiceTable, row, elements.from_date(), e.getFromDate());
            FieldConfig.setText(choiceTable, row, elements.to_date(), e.getToDate());
            FieldConfig.setText(choiceTable, row, elements.price(), e.getPrice());
            FieldConfig.setBoolean(choiceTable, row, elements.membership_required(), e.getMembershipRequired());

            FieldConfig.setText(choiceTable, row, elements.price(), e.getPrice()); 
            FieldConfig.setText(choiceTable, row, elements.price_year(), e.getPriceMembers());  
            FieldConfig.setText(choiceTable, row, elements.price_course(), e.getPriceLessons());
            FieldConfig.setText(choiceTable, row, elements.price_train(),e.getPriceTrain());
            FieldConfig.setText(choiceTable, row, elements.price_youth(),e.getPriceYouth());
            FieldConfig.setText(choiceTable, row, elements.count(), e.getMaxNumber());
            FieldConfig.setText(choiceTable, row, elements.max_diff_sex(), e.getMaxDifferenceSex());
            
            row++;
        }
    }

    public void sync() {

        ArrayList<EventChoice> all = new ArrayList<EventChoice>();

        for (int row = 1; row < choiceTable.getRowCount(); row++) {
            EventChoice data = getRowData(row);
            all.add(data);
        }

        event.setChoices(all);
    }

    private EventChoice getRowData(int row) {

        JSONObject obj = new JSONObject();
        EventChoice eventChoice = new EventChoice(obj);

        obj.put(EventChoice.NAME, FieldConfig.getText(choiceTable, row, elements.name()));
        obj.put(EventChoice.GROUP, FieldConfig.getText(choiceTable, row, elements.group()));
        obj.put(EventChoice.FROM_DATE, FieldConfig.getText(choiceTable, row, elements.from_date()));
        obj.put(EventChoice.TO_DATE, FieldConfig.getText(choiceTable, row, elements.to_date()));
        obj.put(EventChoice.MEMB_REQ, FieldConfig.getText(choiceTable, row, elements.membership_required()));
        obj.put(EventChoice.PRICE, FieldConfig.getText(choiceTable, row, elements.price()));
        obj.put(EventChoice.PRICE_MEMBERS, FieldConfig.getText(choiceTable, row, elements.price_year()));
        obj.put(EventChoice.PRICE_LESSONS, FieldConfig.getText(choiceTable, row, elements.price_course()));
        obj.put(EventChoice.PRICE_TRAIN, FieldConfig.getText(choiceTable, row, elements.price_train()));
        obj.put(EventChoice.PRICE_YOUTH, FieldConfig.getText(choiceTable, row, elements.price_youth()));
        obj.put(EventChoice.MAX, FieldConfig.getText(choiceTable, row, elements.count()));
        obj.put(EventChoice.MAX_DIFFERENCE_SEX, FieldConfig.getText(choiceTable, row, elements.max_diff_sex()));

        return eventChoice;
    }

    static class FieldConfig {
        static ArrayList<FieldConfig> fieldConfigs = new ArrayList<FieldConfig>();
        static HashMap<String, Integer> indexed = new HashMap<String, Integer>();
        
        final String name;
        final Widget widget;

        enum Config {
            DATEFIELD, DISABLED_WHEN_ACTIVE, MONEY
        }

        public FieldConfig(String name, Widget widget, Config... configs) {
            this.name = name;
            this.widget = widget;

            indexed.put(name, fieldConfigs.size());
            fieldConfigs.add(this);
            
            for (Config config : configs) {
                if (config == Config.DATEFIELD) {
                    ((TextBox) widget).setMaxLength(10);
                    ((TextBox) widget).setWidth("6em");
                }
                if (config == Config.MONEY) {
                    ((TextBox) widget).setWidth("7em");
                }
            }

        }

        public static JSONValue getText(AccountTable choiceTable, int row, String name) {
            Widget widgetInColumn = choiceTable.getWidget(row, indexed.get(name));

            if (widgetInColumn instanceof TextBox) {
                TextBox box = (TextBox) widgetInColumn;
                return new JSONString(box.getText());
            }

            return ((CheckBox) widgetInColumn).getValue() ? new JSONString("1") : new JSONString("0");
        }


        public static void setBoolean(AccountTable choiceTable, int row, String name,
                Boolean b) {
            Widget widgetInColumn = choiceTable.getWidget(row, indexed.get(name));
            
            if (widgetInColumn instanceof CheckBox) {
                CheckBox check = (CheckBox) widgetInColumn;
                check.setValue(b);
            }
            
        }

        public static void setText(AccountTable choiceTable, int row, String name, String textToSet) {
            Widget widgetInColumn = choiceTable.getWidget(row, indexed.get(name));
            
            if (widgetInColumn instanceof TextBox) {
                TextBox box = (TextBox) widgetInColumn;
                box.setText(textToSet);
            }
        }

        public static String[] getHeaders(String... extra) {
            ArrayList<String> headers = new ArrayList<String>();

            for (FieldConfig fc : fieldConfigs) {
                headers.add(fc.name);
            }
            headers.addAll(Arrays.asList(extra));

            return headers.toArray(new String[] {});
        }

        static void init(Elements elements) {
            add(elements.name(), new TextBox(), Config.DISABLED_WHEN_ACTIVE);
            add(elements.group(), new TextBox(), Config.DISABLED_WHEN_ACTIVE);
            add(elements.event_field_type(), new ListBox(), Config.DISABLED_WHEN_ACTIVE);
            add(elements.from_date(), new TextBox(), Config.DATEFIELD, Config.DISABLED_WHEN_ACTIVE);
            add(elements.to_date(), new TextBox(), Config.DATEFIELD, Config.DISABLED_WHEN_ACTIVE);
            add(elements.membership_required(), new CheckBox(), Config.DISABLED_WHEN_ACTIVE);
            add(elements.price(), new TextBox(), Config.DISABLED_WHEN_ACTIVE);
            add(elements.price_year(), new TextBox(), Config.MONEY, Config.DISABLED_WHEN_ACTIVE);
            add(elements.price_course(), new TextBox(), Config.MONEY, Config.DISABLED_WHEN_ACTIVE);
            add(elements.price_train(), new TextBox(), Config.MONEY, Config.DISABLED_WHEN_ACTIVE);
            add(elements.price_youth(), new TextBox(), Config.MONEY, Config.DISABLED_WHEN_ACTIVE);
            add(elements.count(), new TextBox());
            add(elements.max_diff_sex(), new TextBox());
        }

        private static void add(String name, Widget widget, Config... configs) {
            new FieldConfig(name, widget, configs);
        }
    }

}
