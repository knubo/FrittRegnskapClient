package no.knubo.accounting.client.views.events;

import java.util.ArrayList;
import java.util.List;

import no.knubo.accounting.client.Elements;
import no.knubo.accounting.client.misc.ImageFactory;
import no.knubo.accounting.client.ui.AccountTable;
import no.knubo.accounting.client.ui.NamedButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EventChoiceEditor extends Composite implements ClickHandler {

    private NamedButton newButton;
    private AccountTable choiceTable;
    private Event event;

    public EventChoiceEditor(Elements elements) {

        VerticalPanel vp = new VerticalPanel();

        newButton = new NamedButton("event_new_choice", elements.event_new_choice());
        newButton.addClickHandler(this);
        vp.add(newButton);

        choiceTable = new AccountTable("tableborder choicetable");

        choiceTable.setHeaders(0, elements.name(), elements.group(), elements.from_date(), elements.to_date(),
                elements.membership_required(), elements.price(), elements.price_year(), elements.price_course(),
                elements.price_train(), elements.price_youth(), elements.count(), elements.max_diff_sex(),
                elements.delete());

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

        for (int i = 0; i < 4; i++) {
            TextBox box = new TextBox();
            choiceTable.setWidget(row, i, box);
            if (i == 2 || i == 3) {
                box.setMaxLength(10);
                box.setWidth("6em");
            }

            if (disabled && (i >= 0 && i <= 9)) {
                box.setEnabled(false);
            }
        }

        CheckBox choiceBox = new CheckBox();

        if (disabled) {
            choiceBox.setEnabled(false);
        }

        choiceTable.setWidget(row, 4, choiceBox);
        for (int i = 5; i < 12; i++) {
            TextBox box = new TextBox();
            choiceTable.setWidget(row, i, box);
            box.setWidth("7em");

            if (disabled && (i >= 0 && i <= 9)) {
                box.setEnabled(false);
            }

        }

        if (!disabled) {
            Image delImage = ImageFactory.deleteImage("del" + row);
            choiceTable.setWidget(row, 12, delImage);
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
            setText(row, 0, e.getName(), e.getGroup(), e.getFromDate(), e.getToDate(), null, e.getPrice(),
                    e.getPriceMembers(), e.getPriceLessons(), e.getPriceTrain(), e.getPriceYouth(), e.getMaxNumber(),
                    e.getMaxDifferenceSex());

            CheckBox checkbox = (CheckBox) choiceTable.getWidget(row, 4);

            checkbox.setValue(e.getMembershipRequired());

            row++;
        }
    }

    void setText(int row, int col, String... texts) {

        int column = col;

        for (String text : texts) {
            if (text != null) {
                TextBox box = (TextBox) choiceTable.getWidget(row, column);
                box.setText(text);
            }
            column++;
        }
    }

    JSONString getText(int row, int col) {
        Widget widget = choiceTable.getWidget(row, col);

        if (widget instanceof TextBox) {
            TextBox box = (TextBox) widget;
            return new JSONString(box.getText());
        }

        return ((CheckBox) widget).getValue() ? new JSONString("1") : new JSONString("0");

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

        obj.put(EventChoice.NAME, getText(row, 0));
        obj.put(EventChoice.GROUP, getText(row, 1));
        obj.put(EventChoice.FROM_DATE, getText(row, 2));
        obj.put(EventChoice.TO_DATE, getText(row, 3));
        obj.put(EventChoice.MEMB_REQ, getText(row, 4));
        obj.put(EventChoice.PRICE, getText(row, 5));
        obj.put(EventChoice.PRICE_MEMBERS, getText(row, 6));
        obj.put(EventChoice.PRICE_LESSONS, getText(row, 7));
        obj.put(EventChoice.PRICE_TRAIN, getText(row, 8));
        obj.put(EventChoice.PRICE_YOUTH, getText(row, 9));
        obj.put(EventChoice.MAX, getText(row, 10));
        obj.put(EventChoice.MAX_DIFFERENCE_SEX, getText(row, 11));

        return eventChoice;
    }
}
