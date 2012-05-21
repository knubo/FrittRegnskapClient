package no.knubo.accounting.client.ui;

import java.util.Date;

import org.gwt.advanced.client.ui.widget.DatePicker;

public class DatePickerButton extends DatePicker {
    public DatePickerButton(Date date) {
        super(date);
        getSelectedValue().setVisible(false);
    }
}
