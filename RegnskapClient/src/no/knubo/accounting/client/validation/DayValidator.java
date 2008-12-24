package no.knubo.accounting.client.validation;

import java.util.Date;

public class DayValidator extends ValidatorBase {

    private final int year;

    private final int month;

    public DayValidator(String errorText, int month, int year) {
        super(errorText);
        this.month = month - 1;
        this.year = year - 1900;
    }

    public DayValidator(String error, String mouseover, int month, int year) {
        this(error, month, year);
        this.mouseOver = mouseover;
    }

    protected boolean validate(Validateable val) {
        int day = 0;

        /* Handle blank using mandatory validators. */
        if (val.getText().length() == 0) {
            return true;
        }

        try {
            day = Integer.parseInt(val.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        return testDate(day, month, year);
    }

    @SuppressWarnings("deprecation")
    static boolean testDate(int testDay, int testMonth, int testYear) {
        Date date = new Date(testYear, testMonth, testDay, 0, 0, 0);

        return date.getDate() == testDay && date.getMonth() == testMonth
                && date.getYear() == testYear;
    }

}
