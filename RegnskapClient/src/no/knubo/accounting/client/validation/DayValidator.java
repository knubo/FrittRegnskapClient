package no.knubo.accounting.client.validation;

import java.util.Date;

import no.knubo.accounting.client.Util;

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

    @Override
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
    public static boolean testDate(int testDay, int testMonth, int testYear) {
        Date date = new Date(testYear, testMonth, testDay, 0, 0, 0);

        return date.getDate() == testDay && date.getMonth() == testMonth && date.getYear() == testYear;
    }

    @SuppressWarnings("deprecation")
    public static Date getDate(String text) {
        if (text == null || text.length() != 10) {
            Util.log("Bad text:" + text);
            return null;
        }

        String[] split = text.split("\\.");

        int testDay = 0;
        int testMonth = 0;
        int testYear = 0;

        try {
            testDay = Integer.parseInt(split[0]);
            testMonth = Integer.parseInt(split[1]) - 1;
            testYear = Integer.parseInt(split[2]);
        } catch (NumberFormatException e) {
            Util.log("NFE:" + e);
            return null;
        }

        Date date = new Date(testYear - 1900, testMonth, testDay, 0, 0, 0);

        boolean ok = date.getDate() == testDay && date.getMonth() == testMonth && (date.getYear() + 1900) == testYear;

        if (!ok) {
            return null;
        }

        return date;
    }

}
