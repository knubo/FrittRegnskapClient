package no.knubo.accounting.client.views.registers;

import no.knubo.accounting.client.Util;
import no.knubo.accounting.client.misc.BlinkImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class PickCityPopup extends PopupPanel implements ClickHandler {

    private FlexTable table;
    private final PersonEditView personEditView;

    public PickCityPopup(PersonEditView personEditView, JSONArray cities, BlinkImage addressInfo) {

        this.personEditView = personEditView;
        table = new FlexTable();
        table.addStyleName("citySelectTable");

        for (int i = 0; i < cities.size(); i++) {
            JSONObject obj = cities.get(i).isObject();
            table.setText(i, 0, Util.str(obj.get("street")));
            table.setText(i, 1, Util.str(obj.get("zipcode")));
            table.setText(i, 2, Util.str(obj.get("city")));
        }

        table.addClickHandler(this);

        add(table);

        setAutoHideEnabled(true);

        showRelativeTo(addressInfo);
    }

    public void onClick(ClickEvent event) {
        Cell cell = table.getCellForEvent(event);

        int row = cell.getRowIndex();
        
        String street = table.getText(row, 0);
        String zip = table.getText(row, 1);
        String city = table.getText(row, 2);

        personEditView.cityPicked(street, zip, city);
        hide();
    
    }

}
