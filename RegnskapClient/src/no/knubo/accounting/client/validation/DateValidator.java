package no.knubo.accounting.client.validation;

public class DateValidator extends ValidatorBase {

    public DateValidator(String error) {
        super(error);
    }

    protected boolean validate(Validateable val) {
        String dateText = val.getText();

        if (dateText.length() == 0) {
            return true;
        }

        if (dateText.length() != 10) {
            return false;
        }

        String[] split = dateText.split("\\.");

        int day = 0;
        int month = 0;
        int year = 0;

        try {
            day = Integer.parseInt(split[0]);
            month = Integer.parseInt(split[1]) - 1;
            year = Integer.parseInt(split[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        return DayValidator.testDate(day, month, year);
    }

}
